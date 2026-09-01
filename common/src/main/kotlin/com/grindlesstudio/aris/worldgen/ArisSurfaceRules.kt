package com.grindlesstudio.aris.worldgen

import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.SurfaceRules

object ArisSurfaceRules {
    private val STONE = SurfaceRules.state(Blocks.STONE.defaultBlockState())

    fun makeRules(): SurfaceRules.RuleSource {
        val isSteep = SurfaceRules.steep()

        return SurfaceRules.sequence(
            SurfaceRules.ifTrue(isSteep, STONE)
        )
    }
}