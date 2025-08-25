package com.mshdabiola.model.note

import kotlinx.serialization.json.Json


object Converter {
    val json = Json
    fun pathToString(paths: List<Path>): String {
        return json.encodeToString(paths)
    }

    fun toPath(string: String): List<Path> {
        return json.decodeFromString(string)
    }
}
