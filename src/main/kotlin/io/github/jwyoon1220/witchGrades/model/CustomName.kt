package io.github.jwyoon1220.witchGrades.model

import java.util.UUID

data class CustomName(
    val uuid: UUID,
    var title: String? = null,                 // 활성 칭호 이름(TitleEntry.name)
    var nickname: String? = null,
    var titles: List<TitleEntry> = emptyList() // 보유 칭호 목록
) {
    fun formatted(): String {
        // 활성 칭호의 본문(MiniMessage가 아닌 순수 문자열; 별도 처리에서 MiniMessage 적용)
        val activeBody = titles.firstOrNull { it.name == title }?.bodyMini ?: title
        return listOfNotNull(activeBody, nickname).joinToString(" ")
    }
}
data class TitleEntry(
    val name: String,
    val description: String,
    val bodyMini: String
) {
    companion object {
        fun fromAny(raw: Any?): TitleEntry? {
            return when (raw) {
                is Map<*, *> -> {
                    val name = raw["name"] as? String ?: return null
                    val desc = raw["description"] as? String ?: ""
                    val body = raw["bodyMini"] as? String ?: name
                    TitleEntry(name, desc, body)
                }
                is String -> TitleEntry(raw, "", raw) // 구버전(문자열) 호환
                else -> null
            }
        }
    }
}