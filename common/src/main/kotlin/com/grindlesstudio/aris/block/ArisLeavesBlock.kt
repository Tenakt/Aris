package com.grindlesstudio.aris.block

import net.minecraft.core.Direction
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import java.util.Optional

class ArisLeavesBlock(
    properties: BlockBehaviour.Properties
) : LeavesBlock(
    AmbientLeavesBlockSoundPlayer(
        Optional.empty(),
        0,
        Optional.empty(),
        0,
        0
    ),
    properties
) {

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