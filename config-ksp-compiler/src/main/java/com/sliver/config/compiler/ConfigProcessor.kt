package com.sliver.config.compiler

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.sliver.config.ConfigBase
import com.sliver.config.ConfigBasic
import com.sliver.config.ConfigTarget
import com.sliver.config.annotation.PreferenceKey
import com.sliver.config.annotation.PreferenceName
import com.sliver.config.extension.addFunctions
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.asStream

class ConfigProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        //技巧：
        //1.需要生成对应类的变量应不需要加载该类的KClass、Class，而只需要该类的包名和类名
        //val className = ClassName("kotlin", "Int")
        //2.需要用到类的地方优先使用ClassName、TypeName
        //val typeName = typeNameOf<MutableList<Class<out Initializer<*>>>>()

        val symbols = resolver.getSymbolsWithAnnotation(PreferenceName::class.qualifiedName!!)

        //生成Config类
        Stream.of(symbols)
            .flatMap { it.asStream() }
            .filter { it.validate() }
            .filter { it is KSClassDeclaration }
            .map { it as KSClassDeclaration }
            .peek { classDeclaration ->
                val packageName = classDeclaration.packageName.asString()
                val fileName = "${classDeclaration.toClassName().simpleName}Config"
                val typeName = "${classDeclaration.toClassName().simpleName}Config"
                val returnType = ClassName(packageName, typeName)
                val targetPropName = "getTarget()"
                val configPropName = "super"
                FileSpec.builder(packageName, fileName)
                    .addImport(ConfigBase::class.asClassName().packageName, ConfigBase::class.asClassName().simpleName)
                    .addImport(ConfigBasic::class.asClassName().packageName, ConfigBasic::class.asClassName().simpleName)
                    .addImport(ConfigTarget::class.asClassName().packageName, ConfigTarget::class.asClassName().simpleName)
                    .addImport(PreferenceName::class.asClassName().packageName, PreferenceName::class.asClassName().simpleName)
                    .addType(TypeSpec.classBuilder(typeName)
                        .superclass(classDeclaration.toClassName())
                        .addSuperinterface(ConfigBasic::class, CodeBlock.of("${ConfigBase::class.asClassName().simpleName}()"))
                        .addProperties(Stream.of(classDeclaration.getDeclaredProperties())
                            .flatMap { it.asStream() }
                            .map {
                                val propName = it.simpleName.asString()
                                val setFunName = "set" + propName.first().uppercase() + propName.substring(1)
                                val getFunName = "get" + propName.first().uppercase() + propName.substring(1)
                                PropertySpec.builder(propName, it.type.resolve().toClassName(), KModifier.OVERRIDE)
                                    .mutable()
                                    .getter(
                                        FunSpec.getterBuilder()
                                            .addStatement("return ${getFunName}() ?: ${configPropName}.${propName}")
                                            .build()
                                    )
                                    .setter(
                                        FunSpec.setterBuilder()
                                            .addParameter(propName, it.type.resolve().toClassName())
                                            .addStatement("return run { ${setFunName}(${propName}) }")
                                            .build()
                                    )
                                    .build()
                            }
                            .collect(Collectors.toList()))
                        .addFunctions(Stream.of(classDeclaration.getDeclaredProperties())
                            .flatMap { it.asStream() }
                            .flatMap {
                                val propName = it.simpleName.asString()
                                val setFunName = "set" + propName.first().uppercase() + propName.substring(1)
                                val getFunName = "get" + propName.first().uppercase() + propName.substring(1)
                                val keyValue = it.getAnnotationsByType(PreferenceKey::class).last().key
                                Stream.of(
                                    FunSpec.builder(setFunName)
                                        .addParameter(propName, it.type.toTypeName().copy(true))
                                        .addStatement("return·apply·{ ${targetPropName}.set(\"$keyValue\",·$propName)·}")
                                        .returns(returnType)
                                        .build(),
                                    FunSpec.builder(getFunName)
                                        .addParameter(
                                            ParameterSpec.builder("default", it.type.toTypeName().copy(true))
                                                .defaultValue("${configPropName}.${propName}")
                                                .build()
                                        )
                                        .addStatement("return·run·{·${targetPropName}.get(\"$keyValue\",·default)·as·${it.type.resolve().declaration.simpleName.asString()}?·}")
                                        .returns(it.type.resolve().toTypeName().copy(nullable = true))
                                        .build(),
                                )
                            }
                            .collect(Collectors.toList()))
                        .build()
                    )
                    /*去除扩展方法
                    .addFunctions(Stream.of(classDeclaration.getDeclaredProperties())
                        .flatMap { it.asStream() }
                        .flatMap {
                            val propName = it.simpleName.asString()
                            val setFunName = "set" + propName.first().uppercase() + propName.substring(1)
                            val getFunName = "get" + propName.first().uppercase() + propName.substring(1)
                            val keyValue = it.getAnnotationsByType(PreferenceKey::class).last().key
                            Stream.of(
                                FunSpec.builder(setFunName)
                                    .receiver(classDeclaration.toClassName())
                                    .addParameter(propName, it.type.toTypeName().copy(true))
                                    .addStatement("return·apply·{·(this·as·$typeName).${targetPropName}.set(\"$keyValue\",·$propName)·}")
                                    .build(),
                                FunSpec.builder(getFunName)
                                    .receiver(classDeclaration.toClassName())
                                    .addParameter(
                                        ParameterSpec.builder("default", it.type.toTypeName().copy(true))
                                            .defaultValue("this.${propName}")
                                            .build()
                                    )
                                    //.addStatement("return·run·{·(this·as·$typeName).$getFunName(default)·}")
                                    .addStatement("return·run·{·(this·as·$typeName).${targetPropName}.get(\"$keyValue\",·default)·}")
                                    .build(),
                            )
                        }
                        .collect(Collectors.toList()))*/
                    .build()
                    .writeTo(environment.codeGenerator, false)
            }
            .collect(Collectors.counting())

        /*//生成Initializer
        val symbolsInitializeWithTarget = resolver.getSymbolsWithAnnotation(InitializeWithTarget::class.qualifiedName!!)
        val configClassName = ClassName("com.sliver.config", "Config")
        val targetClassName = ClassName("com.sliver.config.target", "PreferenceTarget")
        val initializerClassName = ClassName("androidx.startup", "Initializer")
        val contextClassName = ClassName("android.content", "Context")
        Stream.of(symbolsInitializeWithTarget)
            .flatMap { it.asStream() }
            .filter { it.validate() }
            .filter { it is KSClassDeclaration }
            .map { it as KSClassDeclaration }
            .map {
                val simpleName = it.simpleName.asString()
                val typeName = it.asStarProjectedType().toTypeName()
                val outPackageName = "com.sliver.config.startup"
                val outSimpleName = "${simpleName}ConfigInitializer"
                val outClassName = ClassName(outPackageName, outSimpleName)
                FileSpec.builder(outClassName.packageName, outClassName.simpleName)
                    .addImport(configClassName.packageName, configClassName.simpleName)
                    .addImport(targetClassName.packageName, targetClassName.simpleName)
                    .addImport(initializerClassName.packageName, initializerClassName.simpleName)
                    .addType(
                        TypeSpec.classBuilder(outClassName.simpleName)
                            .addSuperinterface(initializerClassName.parameterizedBy(typeName))
                            .addFunction(
                                FunSpec.builder("create")
                                    .addParameter("context", contextClassName)
                                    .addModifiers(KModifier.OVERRIDE)
                                    .addStatement("·return Config.create<${simpleName}>(PreferenceTarget(context))")
                                    .returns(typeName)
                                    .build()
                            )
                            .addFunction(
                                FunSpec.builder("dependencies")
                                    .addModifiers(KModifier.OVERRIDE)
                                    .addStatement("·return mutableListOf()")
                                    .returns(typeNameOf<MutableList<Class<out Initializer<*>>>>())
                                    .build()
                            )
                            .build()
                    )
                    .build()
                    .writeTo(environment.codeGenerator, false)
                outClassName
            }
            .collect(Collectors.toList())
            .let { classNames ->
                if (classNames.isEmpty()) return@let
                val outPackageName = "com.sliver.config.startup"
                val outSimpleName = "ConfigInitializer"
                val outClassName = ClassName(outPackageName, outSimpleName)
                val initializerClasses = Stream.of(classNames)
                    .flatMap { it.stream() }
                    .map { "${it.simpleName}::class.java" }
                    .collect(Collectors.joining(", "))
                FileSpec.builder(outClassName.packageName, outClassName.simpleName)
                    .addType(
                        TypeSpec.classBuilder(outClassName.simpleName)
                            .addSuperinterface(initializerClassName.parameterizedBy(Unit::class.asTypeName()))
                            .addFunction(
                                FunSpec.builder("create")
                                    .addParameter("context", contextClassName)
                                    .addModifiers(KModifier.OVERRIDE)
                                    .build()
                            )
                            .addFunction(
                                FunSpec.builder("dependencies")
                                    .addModifiers(KModifier.OVERRIDE)
                                    .addStatement("·return mutableListOf(${initializerClasses})")
                                    .returns(typeNameOf<MutableList<Class<out Initializer<*>>>>())
                                    .build()
                            )
                            .build()
                    )
                    .build()
                    .writeTo(environment.codeGenerator, false)
            }*/
        return Stream.of(symbols)
            .flatMap { it.asStream() }
            .filter { !it.validate() }
            .collect(Collectors.toList())
    }
}