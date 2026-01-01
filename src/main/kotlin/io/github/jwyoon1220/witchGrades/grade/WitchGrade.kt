package io.github.jwyoon1220.witchGrades.grade

import net.kyori.adventure.text.format.TextColor
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs

@SerializableAs("WitchGrade")
enum class WitchGrade(
    val level: Int,
    val displayName: String,
    val englishName: String,
    val description: String,
    val color: TextColor,
    val message: String,
    val title: String
) : ConfigurationSerializable {

    LITTLE_SEED(
        1,
        "작은 씨앗",
        "Little Seed",
        "마법의 세계로 첫걸음을 내딛은 존재",
        TextColor.fromHexString("#A3E635")!!,
        "<gray>🌱</gray> <green>작은 씨앗</green>이 <yellow>조심스럽게</yellow> <green>싹</green>을 틔웁니다.",
        "\uDB80\uDC00" // U+F0000
    ),

    SPROUT_WITCH(
        2,
        "새싹 마녀",
        "Sprout Witch",
        "조용히 마법을 배우며 성장 중",
        TextColor.fromHexString("#22C55E")!!,
        "<dark_green>🌿</dark_green> <green>새싹</green>이 자라나 <white>[</white><green>새싹 마녀</green><white>]</white>가 되었습니다.",
        "\uDB80\uDC01" // U+F0001
    ),

    SUNNY_WITCH(
        3,
        "햇살 마녀",
        "Sunny Witch",
        "따뜻한 빛으로 주변을 밝히는 마녀",
        TextColor.fromHexString("#FACC15")!!,
        "<yellow>🌞 햇살</yellow>이 <gold>포근하게</gold> 당신을 감쌉니다. <white>[</white><yellow>햇살 마녀</yellow><white>]</white>",
        "\uDB80\uDC02" // U+F0002
    ),

    WILDFLOWER_WITCH(
        4,
        "들꽃 마녀",
        "Wildflower Witch",
        "자연과 친구가 된 순수한 마녀",
        TextColor.fromHexString("#FB7185")!!,
        "<pink>🌸 들꽃</pink>이 피어납니다. <light_purple>자연의 향기</light_purple>가 감돕니다.",
        "\uDB80\uDC03" // U+F0003
    ),

    WIND_WITCH(
        5,
        "바람 마녀",
        "Wind Witch",
        "자유롭게 세상을 여행하는 마녀",
        TextColor.fromHexString("#38BDF8")!!,
        "<aqua>🍃 바람</aqua>이 <white>당신의 이름</white>을 속삭입니다. <white>[</white><aqua>바람 마녀</aqua><white>]</white>",
        "\uDB80\uDC04" // U+F0004
    ),

    STARLIGHT_WITCH(
        6,
        "별빛 마녀",
        "Starlight Witch",
        "하늘의 언어를 이해하는 마녀",
        TextColor.fromHexString("#60A5FA")!!,
        "<blue>🌟 별빛</blue>이 반짝입니다. <gray>하늘의 마력</gray>이 깨어납니다.",
        "\uDB80\uDC05" // U+F0005
    ),

    LUNA_WITCH(
        7,
        "달의 마녀",
        "Luna Witch",
        "평화로운 밤을 지키는 치유의 마녀",
        TextColor.fromHexString("#A855F7")!!,
        "<light_purple>🌕 달빛</light_purple>이 <white>고요히</white> 흐릅니다. <white>[</white><light_purple>달의 마녀</light_purple><white>]</white>",
        "\uDB80\uDC06" // U+F0006
    ),

    GUARDIAN_OF_THE_GROVE(
        8,
        "숲의 수호자",
        "Guardian of the Grove",
        "생명을 감싸고 보호하는 현자",
        TextColor.fromHexString("#16A34A")!!,
        "<dark_green>🌲 숲</dark_green>이 <green>당신의 이름</green>을 부릅니다. <white>[</white><dark_green>숲의 수호자</dark_green><white>]</white>",
        "\uDB80\uDC07" // U+F0007
    ),

    QUEEN_OF_LIFE(
        9,
        "자연의 여왕",
        "Queen of Life",
        "모든 생명과 조화를 이루는 존재",
        TextColor.fromHexString("#FBBF24")!!,
        "<gold>👑 자연</gold>이 <yellow>당신</yellow>을 축복합니다. <white>[</white><gold>자연의 여왕</gold><white>]</white>",
        "\uDB80\uDC08" // U+F0008
    ),

    WITCH_OF_LIGHT(
        10,
        "빛의 마녀",
        "Witch of Light",
        "세상을 치유하는 궁극의 마녀",
        TextColor.fromHexString("#F8FAFC")!!,
        "<white>✨ 빛</white>이 <yellow>세상</yellow>을 물들입니다. <bold><white>[빛의 마녀]</white></bold>",
        "\uDB80\uDC09" // U+F0009 (리소스팩에 10번째 글리프가 필요합니다)
    );

    override fun serialize(): Map<String, Any> = mapOf("level" to level)

    companion object {
        fun byLevel(level: Int): WitchGrade =
            entries.firstOrNull { it.level == level } ?: LITTLE_SEED

        @JvmStatic
        fun deserialize(map: Map<String, Any>): WitchGrade {
            val level = map["level"] as? Int
                ?: error("WitchGrade deserialize failed: level missing")
            return byLevel(level)
        }
    }
}