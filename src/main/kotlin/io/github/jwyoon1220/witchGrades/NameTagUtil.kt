package io.github.jwyoon1220.witchGrades

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedChatComponent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Team
import java.util.concurrent.ThreadLocalRandom

object NameTagUtil {

    // 플레이어를 이름표 숨김 팀에 추가
    fun applyNoNametagTeam(player: Player, teamName: String = "wg_hidden_nametag") {
        val sb = Bukkit.getScoreboardManager().mainScoreboard
        val team: Team = sb.getTeam(teamName) ?: sb.registerNewTeam(teamName)
        team.setCanSeeFriendlyInvisibles(true)
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
        if (!team.hasEntry(player.name)) {
            team.addEntry(player.name)
        }
    }

    // 가짜 TEXT_DISPLAY 엔티티 스폰 (클라이언트 전용)
    fun spawnTextDisplay(player: Player, loc: Location, text: String): Int {
        val pm = ProtocolLibrary.getProtocolManager()
        val entityId = ThreadLocalRandom.current().nextInt(100000, 200000) // 충돌 가능성 낮춘 임의 ID

        val spawn = PacketContainer(com.comphenix.protocol.PacketType.Play.Server.SPAWN_ENTITY)
        spawn.integers.write(0, entityId)
        spawn.uuiDs.write(0, java.util.UUID.randomUUID())
        // Entity type: 86 = TEXT_DISPLAY (1.20+ 기준). 서버 버전에 맞게 확인 필요.
        spawn.entityTypeModifier.write(0, EntityType.TEXT_DISPLAY)
        spawn.doubles.write(0, loc.x)
        spawn.doubles.write(1, loc.y)
        spawn.doubles.write(2, loc.z)
        // Pitch/Yaw/HeadYaw
        spawn.bytes.write(0, ((loc.yaw % 360 + 360) % 360 / 360.0 * 256).toInt().toByte())
        spawn.bytes.write(1, ((loc.pitch % 360 + 360) % 360 / 360.0 * 256).toInt().toByte())
        spawn.bytes.write(2, 0.toByte()) // head yaw

        // TEXT_DISPLAY 초기 메타데이터 설정 (텍스트)
        val meta = buildTextDisplayMetadata(entityId, text)

        pm.sendServerPacket(player, spawn)
        pm.sendServerPacket(player, meta)

        return entityId
    }

    // TEXT_DISPLAY 텍스트 업데이트
    fun updateTextDisplay(player: Player, entityId: Int, newText: String) {
        val pm = ProtocolLibrary.getProtocolManager()
        val meta = buildTextDisplayMetadata(entityId, newText)
        pm.sendServerPacket(player, meta)
    }

    // TEXT_DISPLAY 제거
    fun destroyTextDisplay(player: Player, entityId: Int) {
        val pm = ProtocolLibrary.getProtocolManager()
        val destroy = PacketContainer(com.comphenix.protocol.PacketType.Play.Server.ENTITY_DESTROY)
        destroy.intLists.write(0, listOf(entityId))
        pm.sendServerPacket(player, destroy)
    }

    // TEXT_DISPLAY 메타데이터 패킷 생성
    private fun buildTextDisplayMetadata(entityId: Int, text: String): PacketContainer {
        val packet = PacketContainer(com.comphenix.protocol.PacketType.Play.Server.ENTITY_METADATA)
        packet.integers.write(0, entityId)

        val watcher = com.comphenix.protocol.wrappers.WrappedDataWatcher()
        val serializer = com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry.getChatComponentSerializer(true)

        // TextDisplay의 text 필드 인덱스는 서버 버전에 따라 달라질 수 있음.
        // 일반적으로 index 22가 text(Components)로 사용됨 (1.20.x). 필요 시 서버 버전에 맞춰 조정.
        val textIndex = 22
        val chat = WrappedChatComponent.fromJson("{\"text\":\"${escapeJson(text)}\"}")
        watcher.setObject(textIndex, serializer, chat.handle)

        packet.watchableCollectionModifier.write(0, watcher.watchableObjects)
        return packet
    }

    // 간단한 JSON 이스케이프
    private fun escapeJson(input: String): String =
        input.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
}