package io.github.jwyoon1220.witchGrades.model

import java.util.UUID

data class CustomName(
    val uuid: UUID,
    var title: String? = null,
    var nickname: String? = null
) {
    fun formatted(): String {
        val t = title?.takeIf { it.isNotBlank() }
        val n = nickname?.takeIf { it.isNotBlank() } ?: "Unknown"
        return if (t != null) "[$t] $n" else n
    }
}