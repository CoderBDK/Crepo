package com.lazy.crepo.processor.helper

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun Resolver.rootPackage(): String? {
    val allPackages = this.getAllFiles().map { it.packageName.asString() }
        .distinct()

    return allPackages.minByOrNull { it.length }
}

private fun resolveType(type: KSAnnotated?): String {
    return type?.let {
        (it as? KSClassDeclaration)?.qualifiedName?.asString() ?: "Any"
    } ?: "Any"
}
