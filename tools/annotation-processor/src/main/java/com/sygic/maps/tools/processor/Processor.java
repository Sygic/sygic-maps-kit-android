/*
 * Copyright (c) 2020 Sygic a.s. All rights reserved.
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

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.sygic.maps.tools.annotations.Assisted;
import com.sygic.maps.tools.annotations.AutoFactory;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
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

                final Set<List<ParameterSpec>> createdConstructors = new HashSet<>();

                // add constructor
                for (final Map<Integer, ? extends VariableElement> nonAssistedParameters : nonAssistedParametersList) {
                    final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
                    builder.addAnnotation(Inject.class);

                    final Set<FieldSpec> fields = new HashSet<>();
                    for (final VariableElement parameter : nonAssistedParameters.values()) {
                        final String parameterName = parameter.getSimpleName().toString();
                        final String parameterClassName = parameter.asType().toString();
                        final String parameterPackage = elementUtils.getPackageOf(parameter).getQualifiedName().toString();
                        final ClassName parameterType = ClassName.get(parameterPackage, parameterClassName);
                        builder.addParameter(parameterType, parameterName, Modifier.FINAL);
                        builder.addStatement("this.$N = $N", parameterName, parameterName);

                        fields.add(FieldSpec.builder(parameterType, parameterName, Modifier.FINAL, Modifier.PRIVATE).build());
                    }

                    final MethodSpec constructor = builder.build();
                    if (createdConstructors.add(constructor.parameters)) {
                        for (final FieldSpec field : fields) {
                            classBuilder.addField(field);
                        }
                        classBuilder.addMethod(constructor);
                    }
                }

                // add method that creates the target object
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

                final CodeBlock.Builder cb = CodeBlock.builder();

                cb.addStatement("int variant = -1");
                cb.addStatement("final $T<$T[]> constructorAssistedParameters = new  $T<>()", List.class, Class.class, ArrayList.class);
                cb.addStatement("final $T<$T[]> constructorAssistedParametersNullability = new  $T<>()", List.class, Boolean.class, ArrayList.class);
                cb.add("\n");

                for (final Map<Integer, ? extends VariableElement> parameterMap : assistedParametersList) {
                    cb.add("constructorAssistedParameters.add(new $T[] {$W", Class.class);
                    for (final Iterator<? extends VariableElement> iterator = parameterMap.values().iterator(); iterator.hasNext(); ) {
                        VariableElement parameter = iterator.next();
                        cb.add("$T.class", typeUtils.erasure(parameter.asType()));

                        if (iterator.hasNext()) {
                            cb.add(",$W");
                        }
                    }
                    cb.addStatement("$W})");
                }

                for (final Map<Integer, ? extends VariableElement> parameterMap : assistedParametersList) {
                    cb.add("constructorAssistedParametersNullability.add(new $T[] {$W", Boolean.class);
                    for (final Iterator<? extends VariableElement> iterator = parameterMap.values().iterator(); iterator.hasNext(); ) {
                        VariableElement parameter = iterator.next();
                        cb.add("$L", parameter.getAnnotation(NotNull.class) == null && parameter.getAnnotation(NonNull.class) == null);

                        if (iterator.hasNext()) {
                            cb.add(",$W");
                        }
                    }
                    cb.addStatement("$W})");
                }

                cb.add("\n");

                cb.addStatement("final $1T[][] parameters = new $1T[constructorAssistedParameters.size()][]", Object.class);
                cb.beginControlFlow("for ($T v = 0; v < constructorAssistedParameters.size(); v++)", int.class);
                cb.addStatement("final $T[] entry = constructorAssistedParameters.get(v)", Class.class);
                cb.addStatement("final $T[] nullableInfo = constructorAssistedParametersNullability.get(v)", Boolean.class);
                cb.addStatement("parameters[v] = new $T[entry.length]", Object.class);
                cb.addStatement("boolean allNullable = true");
                cb.beginControlFlow("for (final $T nullable : nullableInfo)", Boolean.class);
                cb.beginControlFlow("if (!nullable)");
                cb.addStatement("allNullable = false");
                cb.addStatement("break");
                cb.endControlFlow();
                cb.endControlFlow();
                cb.beginControlFlow("if (allNullable)");
                cb.addStatement("variant = v");
                cb.endControlFlow();
                cb.addStatement("boolean found = true");
                cb.addStatement("$T k = 0", int.class);
                cb.beginControlFlow("for ($T i = 0; i < entry.length; i++)", int.class);
                cb.addStatement("final $T cls = entry[i]", Class.class);
                cb.addStatement("final $T nullable = nullableInfo[i]", boolean.class);
                cb.beginControlFlow("if (nullable)");
                cb.beginControlFlow("if (" + ASSISTED_PARAMETER_NAME + ".length <= k || " + ASSISTED_PARAMETER_NAME + "[k] == null)");
                cb.addStatement("k++");
                cb.addStatement("continue");
                cb.endControlFlow();
                cb.beginControlFlow("if (cls.isInstance(" + ASSISTED_PARAMETER_NAME + "[k]))");
                cb.addStatement("parameters[v][i] = assistedValues[k]");
                cb.addStatement("k++");
                cb.addStatement("continue");
                cb.endControlFlow();
                cb.endControlFlow();
                cb.beginControlFlow("if (" + ASSISTED_PARAMETER_NAME + ".length <= k || !cls.isInstance(" + ASSISTED_PARAMETER_NAME + "[k]))");
                cb.addStatement("found = false");
                cb.addStatement("break");
                cb.endControlFlow();
                cb.addStatement("parameters[v][i] = assistedValues[k]");
                cb.addStatement("k++");
                cb.endControlFlow();
                cb.beginControlFlow("if (found)");
                cb.addStatement("variant = v");
                cb.addStatement("break");
                cb.endControlFlow();
                cb.endControlFlow();

                cb.add("\n");

                for (int i = 0; i < nonAssistedParametersList.size(); i++) {
                    final Map<Integer, ? extends VariableElement> nonAssistedParameters = nonAssistedParametersList.get(i);
                    final Map<Integer, ? extends VariableElement> assistedParameters = assistedParametersList.get(i);
                    int parametersSize = nonAssistedParameters.size() + assistedParameters.size();

                    cb.beginControlFlow("if (variant == $L)", i);
                    cb.add("return new $N(", className.simpleName());

                    int a = 0;
                    for (int j = 0; j < parametersSize; j++) {
                        VariableElement parameter = nonAssistedParameters.get(j);
                        if (parameter == null) {
                            parameter = assistedParameters.get(j);
                            cb.add("($T)parameters[variant][$L]", parameter.asType(), a++);
                        } else {
                            cb.add("$N", parameter.getSimpleName());
                        }

                        if (j + 1 < parametersSize) {
                            cb.add(",$W");
                        }
                    }
                    cb.addStatement(")");
                    cb.endControlFlow();
                }

                cb.add("\n");
                cb.addStatement("throw new IllegalArgumentException($S)",
                        "Provided assisted " + ASSISTED_PARAMETER_NAME + " values don't match any available constructor");

                createMethodBuilder.addCode(cb.build());
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