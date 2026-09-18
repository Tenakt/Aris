package com.grindlesstudio.aris.skill

import com.grindlesstudio.aris.Aris
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType

class ArisSkillData private constructor(
    private val players: MutableMap<String, List<String>>
) : SavedData() {

    constructor() : this(mutableMapOf())

    fun isUnlocked(
        player: ServerPlayer,
        skillId: String
    ): Boolean {
        return players[player.uuid.toString()]
            ?.contains(skillId) == true
    }

    fun unlock(
        player: ServerPlayer,
        skillId: String
    ) {
        val uuid = player.uuid.toString()

        val skills = players[uuid]
            ?.toMutableSet()
            ?: mutableSetOf()

        if (skills.add(skillId)) {
            players[uuid] = skills.toList()
            setDirty()
        }
    }

    fun getUnlocked(
        player: ServerPlayer
    ): Set<String> {
        return players[player.uuid.toString()]
            ?.toSet()
            ?: emptySet()
    }

    companion object {

        private val PLAYERS_CODEC: Codec<MutableMap<String, List<String>>> =
            Codec.unboundedMap(
                Codec.STRING,
                Codec.list(Codec.STRING)
            ).xmap(
                { it.toMutableMap() },
                { it }
            )

        val CODEC: Codec<ArisSkillData> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    PLAYERS_CODEC
                        .fieldOf("players")
                        .forGetter { it.players }
                ).apply(
                    instance,
                    ::ArisSkillData
                )
            }

        val TYPE = SavedDataType(
            "skill_data",
            ::ArisSkillData,
            CODEC,
            DataFixTypes.LEVEL
        )

        fun get(
            level: ServerLevel
        ): ArisSkillData {
            val overworld = level.server.overworld()

            return overworld.dataStorage.computeIfAbsent(TYPE)
        }

        fun get(
            player: ServerPlayer
        ): ArisSkillData {
            return get(player.level() as ServerLevel)
        }
    }
}