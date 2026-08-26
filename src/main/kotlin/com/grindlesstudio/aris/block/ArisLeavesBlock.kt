package com.grindlesstudio.aris.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockState
import net.minecraft.block.LeavesBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

class ArisLeavesBlock(
    settings: AbstractBlock.Settings
) : LeavesBlock(
    0.02f,
    settings
) {

    override fun getCodec(): MapCodec<out LeavesBlock> {
        return MapCodec.unit(this)
    }

    override fun spawnLeafParticle(
        world: World,
        pos: BlockPos,
        random: Random
    ) {
        // Ничего не делаем.
        // Частицы для нашей листвы пока отключены.
    }

    override fun isSideInvisible(
        state: BlockState,
        stateFrom: BlockState,
        direction: Direction
    ): Boolean {
        if (stateFrom.isOf(this)) {
            return true
        }

        return super.isSideInvisible(
            state,
            stateFrom,
            direction
        )
    }
}