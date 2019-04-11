/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.tools.processor;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import com.squareup.javapoet.*;
import com.sygic.maps.tools.annotations.Assisted;
import com.sygic.maps.tools.annotations.AutoFactory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.*;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class Processor extends AbstractProcessor {

    private static final String GENERATED_CLASS_SUFFIX = "Factory";
    private static final String DEFAULT_FACTORY_INTERFACE = "com.sygic.maps.tools.viewmodel.factory.ViewModelCreatorFactory";
    private static final String ASSISTED_PARAMETER_NAME = "assistedValues";

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    /*
     * annotations: list of unique annotations that are getting processed
     */
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {

            // find all the classes that uses the supported annotations
            final Set<TypeElement> typeElements = getTypeElementsToProcess(
                    roundEnv.getRootElements(),
                    annotations);

            // for each such class create a wrapper class for binding
            for (final TypeElement typeElement : typeElements) {
                final AutoFactory annotation = typeElement.getAnnotation(AutoFactory.class);
                if (annotation == null) {
                    continue;
                }

                ClassName[] interfaces;
                try {
                    Class[] classes = annotation.implementing();
                    if (classes.length == 0) {
                        try {
                            classes = new Class[]{Class.forName(DEFAULT_FACTORY_INTERFACE)};
                        } catch (final ClassNotFoundException e) {
                            messager.printMessage(Diagnostic.Kind.WARNING, "Default AutoFactory super type - " + DEFAULT_FACTORY_INTERFACE + " not found! Define your own super type to resolve this warning.");
                        }
                    }

                    interfaces = new ClassName[classes.length];
                    for (int i = 0; i < classes.length; i++) {
                        interfaces[i] = ClassName.get(classes[i]);
                    }
                } catch (final MirroredTypesException mte) {
                    final List<? extends TypeMirror> typeMirrors = mte.getTypeMirrors();
                    interfaces = new ClassName[typeMirrors.size()];

                    if (typeMirrors.size() == 0) {
                        try {
                            interfaces = new ClassName[]{ClassName.get(Class.forName(DEFAULT_FACTORY_INTERFACE))};
                        } catch (final ClassNotFoundException e) {
                            messager.printMessage(Diagnostic.Kind.WARNING, "Default AutoFactory super type - " + DEFAULT_FACTORY_INTERFACE + " not found! Define your own super type to resolve this warning.");
                        }
                    } else {
                        for (int i = 0; i < typeMirrors.size(); i++) {
                            interfaces[i] = ClassName.get(((TypeElement) typeUtils.asElement(typeMirrors.get(i))));
                        }
                    }
                }

                final String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
                final String typeName = typeElement.getSimpleName().toString();
                final ClassName className = ClassName.get(packageName, typeName);

                final ClassName generatedClassName = ClassName
                        .get(packageName, typeName + GENERATED_CLASS_SUFFIX);

                // define the wrapper class
                final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Keep.class);

                for (final ClassName interfaceClass : interfaces) {
                    classBuilder.addSuperinterface(interfaceClass);
                }

                final List<Map<Integer, ? extends VariableElement>> assistedParametersList = new ArrayList<>();
                final List<Map<Integer, ? extends VariableElement>> nonAssistedParametersList = new ArrayList<>();

                final List<ExecutableElement> constructors = ElementFilter.constructorsIn(typeElement.getEnclosedElements());
                if (constructors.isEmpty()) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Annotated class " + typeName + " has no constructor!", typeElement);
                }

                for (final ExecutableElement element : constructors) {
                    final List<? extends VariableElement> parameters = element.getParameters();
                    final Map<Integer, VariableElement> assistedList = new LinkedHashMap<>(parameters.size());
                    final Map<Integer, VariableElement> nonAssistedList = new LinkedHashMap<>(parameters.size());
                    for (int i = 0; i < parameters.size(); i++) {
                        final VariableElement parameter = parameters.get(i);
                        if (parameter.getAnnotation(Assisted.class) != null) {
                            assistedList.put(i, parameter);
                        } else {
                            nonAssistedList.put(i, parameter);
                        }
                    }

                    assistedParametersList.add(assistedList);
                    nonAssistedParametersList.add(nonAssistedList);
                }

                // add constructor
                for (final Map<Integer, ? extends VariableElement> nonAssistedParameters : nonAssistedParametersList) {
                    final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
                    builder.addAnnotation(Inject.class);

                    for (final VariableElement parameter : nonAssistedParameters.values()) {
                        final String parameterName = parameter.getSimpleName().toString();
                        final String parameterClassName = parameter.asType().toString();
                        final String parameterPackage = elementUtils.getPackageOf(parameter).getQualifiedName().toString();
                        final ClassName parameterType = ClassName.get(parameterPackage, parameterClassName);
                        builder.addParameter(parameterType, parameterName, Modifier.FINAL);
                        builder.addStatement("this.$N = $N", parameterName, parameterName);

                        classBuilder.addField(FieldSpec.builder(parameterType, parameterName, Modifier.FINAL, Modifier.PRIVATE).build());
                    }

                    classBuilder.addMethod(builder.build());

                    if (nonAssistedParametersList.size() > 1) {
                        messager.printMessage(Diagnostic.Kind.WARNING, "More than 1 constructor found. Factory method will be generated only for the first one!");
                        break;
                    }
                }

                // add method that creates the target object
                final Map<Integer, ? extends VariableElement> assistedParameters = assistedParametersList.get(0);
                final MethodSpec.Builder createMethodBuilder = MethodSpec
                        .methodBuilder("create")
                        .addModifiers(Modifier.PUBLIC)
                        .varargs()
                        .addParameter(ParameterSpec.builder(ArrayTypeName.of(Object.class), ASSISTED_PARAMETER_NAME)
                                .addAnnotation(NonNull.class)
                                .build())
                        .addAnnotation(Override.class)
                        .addAnnotation(NonNull.class)
                        .returns(className);

                final Map<Integer, ? extends VariableElement> nonAssistedParameters = nonAssistedParametersList.get(0);
                final int parametersSize = nonAssistedParameters.size() + assistedParameters.size();

                final List<Object> args = new ArrayList<>();
                final StringBuilder sb = new StringBuilder();

                for (final Map.Entry<Integer, ? extends VariableElement> assistedParameter : assistedParameters.entrySet()) {
                    sb.append("$T $N = null;\n");
                    args.add(assistedParameter.getValue().asType());
                    args.add(assistedParameter.getValue().getSimpleName().toString());
                }

                if (assistedParameters.size() > 0) {
                    sb.append("int i = 0;\n");
                }

                for (final Map.Entry<Integer, ? extends VariableElement> entry : assistedParameters.entrySet()) {
                    final VariableElement parameter = entry.getValue();

                    if (parameter.getAnnotation(NotNull.class) != null || parameter.getAnnotation(NonNull.class) != null) {
                        //check for null in @NonNull
                        sb.append("if (i >= " + ASSISTED_PARAMETER_NAME + ".length || " + ASSISTED_PARAMETER_NAME + "[i] == null || !(" + ASSISTED_PARAMETER_NAME + "[i] instanceof $T)) { " +
                                "throw new IllegalArgumentException(\"$N: null value provided for @NonNull parameter $N\");" +
                                " } " +
                                "else { " +
                                "$N = ($T)" + ASSISTED_PARAMETER_NAME + "[i];\n" +
                                "i++;" +
                                " }\n");
                        args.add(parameter.asType());
                        args.add(typeName);
                        args.add(parameter.getSimpleName().toString());
                        args.add(parameter.getSimpleName().toString());
                        args.add(parameter.asType());
                    } else {
                        //can be null so replace with null if not provided
                        sb.append("if (i < " + ASSISTED_PARAMETER_NAME + ".length && " + ASSISTED_PARAMETER_NAME + "[i] != null && (" + ASSISTED_PARAMETER_NAME + "[i] instanceof $T)) { " +
                                "$N = ($T)" + ASSISTED_PARAMETER_NAME + "[i];\n" +
                                "i++;" +
                                " }\n");
                        args.add(parameter.asType());
                        args.add(parameter.getSimpleName().toString());
                        args.add(parameter.asType());
                    }
                }

                sb.append("return new $N(");
                args.add(className.simpleName());
                for (int j = 0; j < parametersSize; j++) {
                    VariableElement parameter = nonAssistedParameters.get(j);
                    if (parameter == null) {
                        parameter = assistedParameters.get(j);
                        sb.append("$N");
                        args.add(parameter.getSimpleName().toString());
                    } else {
                        sb.append("$N");
                        args.add(parameter.getSimpleName().toString());
                    }

                    if (j + 1 < parametersSize) {
                        sb.append(", ");
                    }
                }
                sb.append(")");

                createMethodBuilder.addStatement(sb.toString(), args.toArray());
                classBuilder.addMethod(createMethodBuilder.build());

                // write the defines class to a java file
                try {
                    JavaFile.builder(packageName,
                            classBuilder.build())
                            .build()
                            .writeTo(filer);
                } catch (final IOException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR, e.toString(), typeElement);
                }
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new TreeSet<>(Arrays.asList(
                AutoFactory.class.getCanonicalName(),
                Assisted.class.getCanonicalName()));
    }

    private static Set<TypeElement> getTypeElementsToProcess(final Set<? extends Element> elements,
                                                             final Set<? extends Element> supportedAnnotations) {
        final Set<TypeElement> typeElements = new HashSet<>();
        for (final Element element : elements) {
            if (element instanceof TypeElement) {
                boolean found = false;
                final List<Element> processingElements = new ArrayList<>(element.getEnclosedElements());
                processingElements.add(0, element);
                for (final Element subElement : processingElements) {
                    for (final AnnotationMirror mirror : subElement.getAnnotationMirrors()) {
                        for (final Element annotation : supportedAnnotations) {
                            if (mirror.getAnnotationType().asElement().equals(annotation)) {
                                typeElements.add((TypeElement) element);
                                found = true;
                                break;
                            }
                        }
                        if (found) break;
                    }
                    if (found) break;
                }
            }
        }
        return typeElements;
    }
}