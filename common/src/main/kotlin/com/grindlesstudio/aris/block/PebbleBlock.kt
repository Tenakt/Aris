package com.grindlesstudio.aris.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class PebbleBlock(
    properties: BlockBehaviour.Properties
) : Block(properties) {

    override fun canSurvive(
        state: BlockState,
        level: LevelReader,
        pos: BlockPos
    ): Boolean {
        val downPos = pos.below()

        return level.getBlockState(downPos)
            .isFaceSturdy(level, downPos, Direction.UP)
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {

        val baseShape = Block.box(
            4.8,
            0.0,
            4.8,
            11.2,
            2.0,
            11.2
        )

        val offset = state.getOffset(pos)

        return baseShape.move(
            offset.x,
            offset.y,
            offset.z
        )
    }
}