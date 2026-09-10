package com.grindlesstudio.aris.worldgen

import dev.architectury.event.events.common.PlayerEvent
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.FluidTags

object ArisSpawn {

    fun init() {
        // Игрок заходит на сервер
        PlayerEvent.PLAYER_JOIN.register { player ->
            checkAndTeleportFromWater(player)
        }

        // Игрок возрождается после смерти
        PlayerEvent.PLAYER_RESPAWN.register { player, _, _ -> checkAndTeleportFromWater(player) } }

    private fun checkAndTeleportFromWater(player: ServerPlayer) {
        val level = player.level()

        val pos = player.blockPosition()

        // Проверяем воду в ногах и на уровне головы.
        val isInWater =
            level.getFluidState(pos).`is`(FluidTags.WATER) ||
                    level.getFluidState(pos.above()).`is`(FluidTags.WATER)

        if (!isInWater) {
            return
        }

        val safePos = findSafeLand(level, pos)

        // Если подходящее место найдено — телепортируем игрока.
        player.teleportTo(
            safePos.x + 0.5,
            safePos.y.toDouble(),
            safePos.z + 0.5
        )
    }

    private fun findSafeLand(
        level: ServerLevel,
        startPos: BlockPos
    ): BlockPos {

        var currentPos = startPos

        while (
            currentPos.y < level.maxY &&
            !level.getFluidState(currentPos).isEmpty
        ) {
            currentPos = currentPos.above()
        }

        val radius = 15

        for (r in 0..radius) {

            for (x in -r..r) {

                for (z in -r..r) {

                    val checkPos = currentPos.offset(x, 0, z)

                    if (isSafePosition(level, checkPos)) {
                        return checkPos
                    }
                }
            }
        }

        return currentPos
    }

    private fun isSafePosition(
        level: ServerLevel,
        checkPos: BlockPos
    ): Boolean {

        val feetPos = checkPos
        val headPos = checkPos.above()
        val groundPos = checkPos.below()

        val feetState = level.getBlockState(feetPos)
        val headState = level.getBlockState(headPos)
        val groundState = level.getBlockState(groundPos)

        if (!feetState.isAir) {
            return false
        }

        if (!headState.isAir) {
            return false
        }

        if (!level.getFluidState(feetPos).isEmpty) {
            return false
        }

        if (!level.getFluidState(headPos).isEmpty) {
            return false
        }

        if (groundState.getCollisionShape(level, groundPos).isEmpty) {
            return false
        }

        return true
    }
}
