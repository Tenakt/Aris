package com.grindlesstudio.aris.skill

import com.daqem.knot.Knot
import com.daqem.knot.networking.ClientboundContext
import com.daqem.knot.networking.ServerboundContext
import com.grindlesstudio.aris.Aris
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects

object ArisSkillNetworking {

    private var isInitialized = false

    data class UnlockSkillPacket(
        val skillId: String
    ) : CustomPacketPayload {

        companion object {
            val TYPE = CustomPacketPayload.Type<UnlockSkillPacket>(
                Aris.id("unlock_skill")
            )

            val CODEC: StreamCodec<RegistryFriendlyByteBuf, UnlockSkillPacket> =
                StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    UnlockSkillPacket::skillId,
                    ::UnlockSkillPacket
                )
        }

        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
            return TYPE
        }
    }

    class RequestSkillStatePacket : CustomPacketPayload {

        companion object {
            val TYPE = CustomPacketPayload.Type<RequestSkillStatePacket>(
                Aris.id("request_skill_state")
            )

            val CODEC: StreamCodec<RegistryFriendlyByteBuf, RequestSkillStatePacket> =
                StreamCodec.unit(RequestSkillStatePacket())
        }

        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
            return TYPE
        }
    }

    data class SkillStatePacket(
        val unlockedSkills: List<String>
    ) : CustomPacketPayload {

        companion object {
            val TYPE = CustomPacketPayload.Type<SkillStatePacket>(
                Aris.id("skill_state")
            )

            val CODEC: StreamCodec<RegistryFriendlyByteBuf, SkillStatePacket> =
                StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
                    SkillStatePacket::unlockedSkills,
                    ::SkillStatePacket
                )
        }

        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
            return TYPE
        }
    }

    fun init() {
        if (isInitialized) return
        isInitialized = true

        Knot.NETWORKING.registerServerbound(
            UnlockSkillPacket.TYPE,
            UnlockSkillPacket.CODEC,
            ::handleUnlockSkill
        )

        Knot.NETWORKING.registerServerbound(
            RequestSkillStatePacket.TYPE,
            RequestSkillStatePacket.CODEC,
            ::handleRequestSkillState
        )

        Knot.NETWORKING.registerClientbound(
            SkillStatePacket.TYPE,
            SkillStatePacket.CODEC,
            ::handleSkillState
        )
    }

    private fun handleUnlockSkill(
        packet: UnlockSkillPacket,
        context: ServerboundContext
    ) {
        val player = context.player()

        val skillId = packet.skillId

        val effect = when (skillId) {
            "speed" -> MobEffects.SPEED
            "jump" -> MobEffects.JUMP_BOOST
            "haste" -> MobEffects.HASTE
            "night_vision" -> MobEffects.NIGHT_VISION
            else -> return
        }

        val data = ArisSkillData.get(player)

        if (data.isUnlocked(player, skillId)) {
            sendSkillState(player)
            return
        }

        data.unlock(player, skillId)

        player.addEffect(
            MobEffectInstance(
                effect,
                Int.MAX_VALUE,
                0,
                false,
                false,
                true
            )
        )

        sendSkillState(player)
    }

    private fun handleRequestSkillState(
        packet: RequestSkillStatePacket,
        context: ServerboundContext
    ) {
        sendSkillState(context.player())
    }

    private fun sendSkillState(
        player: ServerPlayer
    ) {
        val data = ArisSkillData.get(player)

        val unlocked = data
            .getUnlocked(player)
            .toList()

        Knot.NETWORKING.sendToPlayer(
            player,
            SkillStatePacket(unlocked)
        )
    }

    private fun handleSkillState(
        packet: SkillStatePacket,
        context: ClientboundContext
    ) {
        ArisSkillState.setUnlocked(
            packet.unlockedSkills
        )
    }
}