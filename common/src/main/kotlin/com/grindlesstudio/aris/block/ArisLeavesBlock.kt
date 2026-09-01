package com.grindlesstudio.aris.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class ArisLeavesBlock(
    properties: Properties
) : LeavesBlock(
    0.02f,
    properties
) {

    override fun codec(): MapCodec<out LeavesBlock> {
        return MapCodec.unit(this)
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return Shapes.empty()
    }

    override fun spawnFallingLeavesParticle(
        level: Level,
        pos: BlockPos,
        random: RandomSource
    ) {
        // Частицы отключены
    }

    override fun skipRendering(
        state: BlockState,
        adjacentBlockState: BlockState,
        direction: Direction
    ): Boolean {
        if (adjacentBlockState.`is`(this)) {
            return true
        }

        return super.skipRendering(
            state,
            adjacentBlockState,
            direction
        )
    }
}