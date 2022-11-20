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
import com.sliver.config.annotation.InitializeWithTarget
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
    private val excludeProcessSet = HashSet<String>()

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(PreferenceName::class.qualifiedName!!)

        Stream.of(symbols)
            .flatMap { it.asStream() }
            .filter { it.validate() }
            .filter { it is KSClassDeclaration }
            .map { it as KSClassDeclaration }
            .filter { it.toClassName().toString() !in excludeProcessSet }
            .peek { excludeProcessSet.add("${it.toClassName()}Config") }
            .peek { classDeclaration ->
                val annotation = classDeclaration.getAnnotationsByType(PreferenceName::class).first()
                val packageName = classDeclaration.packageName.asString()
                val fileName = "${classDeclaration.toClassName().simpleName}Config"
                val typeName = "${classDeclaration.toClassName().simpleName}Config"
                val targetPropName = "getTarget()"
                val configPropName = "super"
                FileSpec.builder(packageName, fileName)
                    .addImport(ConfigBase::class.asClassName().packageName, ConfigBase::class.asClassName().simpleName)
                    .addImport(ConfigBasic::class.asClassName().packageName, ConfigBasic::class.asClassName().simpleName)
                    .addImport(ConfigTarget::class.asClassName().packageName, ConfigTarget::class.asClassName().simpleName)
                    .addImport(PreferenceName::class.asClassName().packageName, PreferenceName::class.asClassName().simpleName)
                    .addType(TypeSpec.classBuilder(typeName)
                        .addAnnotation(
                            AnnotationSpec.builder(PreferenceName::class)
                                .addMember("\"${annotation.name}\"")
                                .build()
                        )
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
                                    .addStatement("return·run·{·(this·as·$typeName).$getFunName(default)·}")
                                    .build(),
                            )
                        }
                        .collect(Collectors.toList()))
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