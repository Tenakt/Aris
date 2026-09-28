//package com.grindlesstudio.aris.worldgen
//
//import net.minecraft.world.level.biome.Biomes
//import net.minecraft.world.level.block.Block
//import net.minecraft.world.level.block.Blocks
//import net.minecraft.world.level.levelgen.SurfaceRules
//import net.minecraft.world.level.levelgen.VerticalAnchor
//import net.minecraft.world.level.levelgen.placement.CaveSurface
//
//object ArisSurfaceRules {
//    private val BEDROCK = makeStateRule(Blocks.BEDROCK)
//    private val SAND = makeStateRule(Blocks.SAND)
//    private val SANDSTONE = makeStateRule(Blocks.SANDSTONE)
//    private val GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK)
//    private val DIRT = makeStateRule(Blocks.DIRT)
//    private val STONE = makeStateRule(Blocks.STONE)
//
//    private fun makeStateRule(block: Block): SurfaceRules.RuleSource {
//        return SurfaceRules.state(block.defaultBlockState())
//    }
//
//    fun makeRules(): SurfaceRules.RuleSource {
//        val isAtBedrock = SurfaceRules.verticalGradient(
//            "bedrock_floor",
//            VerticalAnchor.bottom(),
//            VerticalAnchor.aboveBottom(5)
//        )
//        val isSteep = SurfaceRules.steep()
//        val isFloor = SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR)
//        val isAbovePreliminarySurface = SurfaceRules.abovePreliminarySurface()
//
//        val desertSurface = SurfaceRules.ifTrue(
//            SurfaceRules.stoneDepthCheck(0, true, CaveSurface.FLOOR),
//            SurfaceRules.sequence(
//                SurfaceRules.ifTrue(isFloor, SAND),
//                SANDSTONE
//            )
//        )
//
//        val defaultSurface = SurfaceRules.ifTrue(
//            isFloor,
//            SurfaceRules.sequence(
//                SurfaceRules.ifTrue(isAbovePreliminarySurface, GRASS_BLOCK),
//                DIRT
//            )
//        )
//
//        return SurfaceRules.sequence(
//            SurfaceRules.ifTrue(isAtBedrock, BEDROCK),
//            SurfaceRules.ifTrue(isSteep, STONE),
//            SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DESERT), desertSurface),
//            defaultSurface,
//            STONE
//        )
//    }
//}