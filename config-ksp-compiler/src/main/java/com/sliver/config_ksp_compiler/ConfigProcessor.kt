package com.sliver.config_ksp_compiler

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.sliver.config_annotation.PreferenceName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.asStream

class ConfigProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(PreferenceName::class.qualifiedName!!)

        Stream.of(symbols)
            .flatMap { it.asStream() }
            .filter { it.validate() }
            .filter { it is KSClassDeclaration }
            .map { it as KSClassDeclaration }
            .peek {
                environment.logger.warn("${it.simpleName}Provider")
                FileSpec.builder(it.packageName.asString(), "${it.toClassName().simpleName}Provider")
                    .addType(
                        TypeSpec.classBuilder("${it.toClassName().simpleName}Provider")
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