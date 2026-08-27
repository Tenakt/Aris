package com.grindlesstudio.aris.worldgen

import net.minecraft.block.Blocks
import net.minecraft.world.gen.surfacebuilder.MaterialRules

object ArisSurfaceRules {
    private val STONE = MaterialRules.block(Blocks.STONE.defaultState)

    fun makeRules(): MaterialRules.MaterialRule {
        // Условие steep() автоматически проверяет угол наклона блоков
        val isSteep = MaterialRules.condition(MaterialRules.steepSlope(), STONE)

        return MaterialRules.sequence(
            isSteep
        )
    }
}