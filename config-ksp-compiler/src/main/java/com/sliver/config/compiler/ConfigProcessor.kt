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
import com.sliver.config.ConfigTarget
import com.sliver.config.annotation.PreferenceKey
import com.sliver.config.annotation.PreferenceName
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
        val symbols = resolver.getSymbolsWithAnnotation(PreferenceName::class.qualifiedName!!)

        Stream.of(symbols)
            .flatMap { it.asStream() }
            .filter { it.validate() }
            .filter { it is KSClassDeclaration }
            .map { it as KSClassDeclaration }
            .peek {
                val packageName = it.packageName.asString()
                val fileName = "${it.toClassName().simpleName}Config"
                val typeName = "${it.toClassName().simpleName}Config"
                val targetPropName = "target"
                val configPropName = "config"
                val configInitializer = "${it.toClassName().simpleName}()"
                FileSpec.builder(packageName, fileName)
                    .addImport(ConfigBase::class.asClassName().packageName, ConfigBase::class.asClassName().simpleName)
                    .addImport(ConfigTarget::class.asClassName().packageName, ConfigTarget::class.asClassName().simpleName)
                    .addType(TypeSpec.classBuilder(typeName)
                        .superclass(ConfigBase::class)
                        .addSuperclassConstructorParameter(targetPropName)
                        .primaryConstructor(
                            FunSpec.constructorBuilder()
                                .addParameter(targetPropName, ConfigTarget::class)
                                .build()
                        )
                        .addProperty(
                            PropertySpec.builder(targetPropName, ConfigTarget::class, KModifier.PROTECTED, KModifier.OVERRIDE)
                                .initializer(targetPropName)
                                .build()
                        )
                        .addProperty(
                            PropertySpec.builder(configPropName, it.toClassName(), KModifier.PRIVATE)
                                .initializer(configInitializer)
                                .build()
                        )
                        .addProperties(Stream.of(it.getDeclaredProperties())
                            .flatMap { it.asStream() }
                            .map {
                                val propName = it.simpleName.asString()
                                val setFunName = "set" + propName.first().uppercase() + propName.substring(1)
                                val getFunName = "get" + propName.first().uppercase() + propName.substring(1)
                                PropertySpec.builder(propName, it.type.resolve().toClassName())
                                    .mutable()
                                    .getter(
                                        FunSpec.getterBuilder()
                                            .addStatement("return ${getFunName}() ?: ${configPropName}.${propName}")
                                            .build()
                                    )
                                    .setter(
                                        FunSpec.setterBuilder()
                                            .addParameter("${propName}", it.type.resolve().toClassName())
                                            .addStatement("return run { ${setFunName}(${propName}) }")
                                            .build()
                                    )
                                    .build()
                            }
                            .collect(Collectors.toList()))
                        .addFunctions(Stream.of(it.getDeclaredProperties())
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
                                        .build(),
                                    FunSpec.builder(getFunName)
                                        .addParameter(
                                            ParameterSpec.builder("default", it.type.toTypeName().copy(true))
                                                .defaultValue("${configPropName}.${propName}")
                                                .build()
                                        )
                                        .addStatement("return·run·{·${targetPropName}.get(\"$keyValue\",·default)·as·${it.type.resolve().declaration.simpleName.asString()}?·}")
                                        .build(),
                                )
                            }
                            .collect(Collectors.toList()))
                        .build()
                    )
                    .build()
                    .writeTo(environment.codeGenerator, false)
            }
            .collect(Collectors.counting())

        return Stream.of(symbols)
            .flatMap { it.asStream() }
            .filter { !it.validate() }
            .collect(Collectors.toList())
    }
}