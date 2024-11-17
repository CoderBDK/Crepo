package com.lazy.crepo.processor.helper

class GenerateRepository(private val className: String, private val packageName: String) {

    private var status = false
    private val imports = mutableListOf<String>()
    private val functions = mutableListOf<String>()
    private val params = mutableListOf<Pair<String, String>>()

    fun addHiltInject(status: Boolean) {
        this.status = status
    }

    fun addImports(importList: List<String>): GenerateRepository {
        imports.addAll(importList)
        return this
    }

    fun addParams(paramList: List<Pair<String, String>>): GenerateRepository {
        params.addAll(paramList)
        return this
    }

    fun addFunction(
        name: String,
        returnType: String,
        parameters: List<Pair<String, String>>,
        body: String
    ): GenerateRepository {
        val paramsString = parameters.joinToString(", ") { "${it.first}: ${it.second}" }
        val function = """
        |    fun $name($paramsString): $returnType = flow {
        |        emit(DataState.Loading)
        |        try {
        |            $body
        |        } catch (e: Exception) {
        |            emit(DataState.Error(e))
        |        }
        |    }
    """.trimMargin()
        functions.add(function)
        return this
    }

    fun build(): String {
        val importsString = imports.distinct().joinToString("\n") { "import $it" }
        val paramsString = params.joinToString(", ") { "private val ${it.first}: ${it.second}" }
        val functionsString = functions.joinToString("\n\n")

        return """
        |package $packageName
        |
        |$importsString
        |
        |class $className ${if (status) "@Inject" else ""} constructor($paramsString) {
        |
        |$functionsString
        |
        |}
    """.trimMargin()
    }
}
