package com.sygic.tools.processor;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import com.squareup.javapoet.*;
import com.sygic.tools.annotations.Assisted;
import com.sygic.tools.annotations.AutoFactory;
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
    private static final String DEFAULT_FACTORY_INTERFACE = "com.sygic.tools.viewmodel.ViewModelCreatorFactory";
    private static final String ASSISTED_PARAMETER_NAME = "assistedValues";

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
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
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {

            // find all the classes that uses the supported annotations
            Set<TypeElement> typeElements = getTypeElementsToProcess(
                    roundEnv.getRootElements(),
                    annotations);

            // for each such class create a wrapper class for binding
            for (TypeElement typeElement : typeElements) {
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
                        } catch (ClassNotFoundException e) {
                            messager.printMessage(Diagnostic.Kind.WARNING, "Default AutoFactory super type - " + DEFAULT_FACTORY_INTERFACE + " not found! Define your own super type to resolve this warning.");
                        }
                    }

                    interfaces = new ClassName[classes.length];
                    for (int i = 0; i < classes.length; i++) {
                        interfaces[i] = ClassName.get(classes[i]);
                    }
                } catch (MirroredTypesException mte) {
                    final List<? extends TypeMirror> typeMirrors = mte.getTypeMirrors();
                    interfaces = new ClassName[typeMirrors.size()];

                    if (typeMirrors.size() == 0) {
                        try {
                            interfaces = new ClassName[]{ClassName.get(Class.forName(DEFAULT_FACTORY_INTERFACE))};
                        } catch (ClassNotFoundException e) {
                            messager.printMessage(Diagnostic.Kind.WARNING, "Default AutoFactory super type - " + DEFAULT_FACTORY_INTERFACE + " not found! Define your own super type to resolve this warning.");
                        }
                    } else {
                        for (int i = 0; i < typeMirrors.size(); i++) {
                            interfaces[i] = ClassName.get(((TypeElement) typeUtils.asElement(typeMirrors.get(i))));
                        }
                    }
                }

                String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
                String typeName = typeElement.getSimpleName().toString();
                ClassName className = ClassName.get(packageName, typeName);

                ClassName generatedClassName = ClassName
                        .get(packageName, typeName + GENERATED_CLASS_SUFFIX);

                // define the wrapper class
                TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Keep.class);

                for (ClassName interfaceClass : interfaces) {
                    classBuilder.addSuperinterface(interfaceClass);
                }

                List<Map<Integer, ? extends VariableElement>> assistedParametersList = new ArrayList<>();
                List<Map<Integer, ? extends VariableElement>> nonAssistedParametersList = new ArrayList<>();

                for (ExecutableElement element : ElementFilter.constructorsIn(typeElement.getEnclosedElements())) {
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
                for (Map<Integer, ? extends VariableElement> nonAssistedParameters : nonAssistedParametersList) {
                    final MethodSpec.Builder builder = MethodSpec.constructorBuilder();
                    builder.addAnnotation(Inject.class);

                    for (VariableElement parameter : nonAssistedParameters.values()) {
                        String parameterName = parameter.getSimpleName().toString();
                        String parameterClassName = parameter.asType().toString();
                        String parameterPackage = elementUtils.getPackageOf(parameter).getQualifiedName().toString();
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
                MethodSpec.Builder createMethodBuilder = MethodSpec
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
                int parametersSize = nonAssistedParameters.size() + assistedParameters.size();

                List<Object> args = new ArrayList<>();

                StringBuilder sb = new StringBuilder("if (" + ASSISTED_PARAMETER_NAME + ".length != $L) { " +
                        "throw new IllegalStateException(\"Wrong number of assisted parameters! Expected count $L\");" +
                        " }\n");
                args.add(assistedParameters.size());
                args.add(assistedParameters.size());

                int assistedValuesCount = 0;
                for (Map.Entry<Integer, ? extends VariableElement> entry : assistedParameters.entrySet()) {
                    final VariableElement parameter = entry.getValue();

                    if (parameter.getAnnotation(NotNull.class) != null || parameter.getAnnotation(NonNull.class) != null) {
                        sb.append("if (" + ASSISTED_PARAMETER_NAME + "[$L] == null) { " +
                                "throw new IllegalArgumentException(\"Null value provided for @NonNull parameter $N\");" +
                                " }\n");
                        args.add(assistedValuesCount);
                        args.add(parameter.getSimpleName().toString());
                    }

                    if (!parameter.asType().getKind().isPrimitive()) {
                        sb.append("if (!(" + ASSISTED_PARAMETER_NAME + "[$L] instanceof $N)) { " +
                                "throw new IllegalArgumentException(\"Value provided for parameter $N is not of correct type $N\");" +
                                " }\n");
                        args.add(assistedValuesCount);
                        args.add(parameter.asType().toString());
                        args.add(parameter.getSimpleName().toString());
                        args.add(parameter.asType().toString());
                    }

                    assistedValuesCount++;
                }

                int assistedIndex = 0;
                sb.append("return new $N(");
                args.add(className.simpleName());
                for (int j = 0; j < parametersSize; j++) {
                    VariableElement parameter = nonAssistedParameters.get(j);
                    if (parameter == null) {
                        parameter = assistedParameters.get(j);
                        sb.append("($N)" + ASSISTED_PARAMETER_NAME + "[$L]");
                        args.add(parameter.asType().toString());
                        args.add(assistedIndex++);
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
                } catch (IOException e) {
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

    private static Set<TypeElement> getTypeElementsToProcess(Set<? extends Element> elements,
                                                             Set<? extends Element> supportedAnnotations) {
        Set<TypeElement> typeElements = new HashSet<>();
        for (Element element : elements) {
            if (element instanceof TypeElement) {
                boolean found = false;
                List<Element> processingElements = new ArrayList<>(element.getEnclosedElements());
                processingElements.add(0, element);
                for (Element subElement : processingElements) {
                    for (AnnotationMirror mirror : subElement.getAnnotationMirrors()) {
                        for (Element annotation : supportedAnnotations) {
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