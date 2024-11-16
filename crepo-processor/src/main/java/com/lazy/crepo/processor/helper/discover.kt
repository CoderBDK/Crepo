package com.lazy.crepo.processor.helper

import com.google.devtools.ksp.processing.Resolver

fun Resolver.rootPackage(): String? {
    val allPackages = this.getAllFiles().map { it.packageName.asString() }
        .distinct()

    return allPackages.minByOrNull { it.length }
}