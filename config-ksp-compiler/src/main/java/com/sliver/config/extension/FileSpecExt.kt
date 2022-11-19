package com.sliver.config.extension

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec

fun FileSpec.Builder.addFunctions(funSpecs: Iterable<FunSpec>) =
    apply { funSpecs.forEach { addFunction(it) } }