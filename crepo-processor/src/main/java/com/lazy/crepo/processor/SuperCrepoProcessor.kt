package com.lazy.crepo.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.lazy.crepo.Repository
import com.lazy.crepo.processor.helper.GenerateRepository
import com.lazy.crepo.processor.helper.rootPackage
import java.io.OutputStreamWriter

class SuperCrepoProcessor(
    private val generator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Repository::class.qualifiedName!!)
        val invalidSymbols = symbols.filterNot {
            it is KSClassDeclaration && it.validate()
        }

        val packageName = resolver.rootPackage()

        symbols.filterIsInstance<KSClassDeclaration>()
            .filter {
                it.validate()
            }
            .forEach {
                generate(it, packageName)
            }
        return invalidSymbols.toList()
    }

    private fun generate(ksClass: KSClassDeclaration, packageName: String?) {
        val kClassName = ksClass.simpleName.asString()
        val repoClassName = "${kClassName}Repository"

        val imports = mutableSetOf(
            "kotlinx.coroutines.flow.Flow",
            "kotlinx.coroutines.flow.flow",
            "${ksClass.packageName.asString()}." + kClassName,
            "com.lazy.crepo.state.DataState"
        )

        val repo = GenerateRepository(repoClassName, packageName ?: ksClass.packageName.asString())
        repo.addParams(
            listOf(
                "api" to kClassName
            )
        )

        ksClass.getAllFunctions()
            .filter { function ->
                val functionName = function.simpleName.asString()
                functionName !in listOf("equals", "hashCode", "toString")
            }
            .forEach { function ->
                val functionName = function.simpleName.asString()
                val returnType =
                    function.returnType?.resolve()?.declaration?.qualifiedName?.asString() ?: "Unit"

                if (!returnType.startsWith("kotlin.")) {
                    imports.add(returnType)
                }

                val params = function.parameters.map { param ->
                    val paramName = param.name?.asString() ?: "param"
                    val paramType =
                        param.type.resolve().declaration.qualifiedName?.asString() ?: "Any"

                    if (!paramType.startsWith("kotlin.")) {
                        imports.add(paramType)
                    }

                    paramName to paramType
                }

                repo.addFunction(
                    name = functionName,
                    returnType = "Flow<DataState<$returnType>>",
                    parameters = params,
                    body = "emit(DataState.Success(api.$functionName(${params.joinToString(", ") { it.first }})))"
                )
            }

        repo.addImports(imports.toList())


        val file = generator.createNewFile(
            Dependencies(false, ksClass.containingFile!!),
            packageName ?: ksClass.packageName.asString(),
            repoClassName
        )

        OutputStreamWriter(file).use { writer ->
            writer.write(repo.build())
        }
    }
}