package com.grindlesstudio.aris.registry

import com.grindlesstudio.aris.worldgen.feature.SlopeSlabFeature
import com.grindlesstudio.aris.worldgen.feature.road.VillageRoadFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

object ModFeatures {

    val SLOPE_SLAB: Feature<NoneFeatureConfiguration> =
        SlopeSlabFeature(
            NoneFeatureConfiguration.CODEC
        )

    val VILLAGE_ROAD: Feature<NoneFeatureConfiguration> =
        VillageRoadFeature(
            NoneFeatureConfiguration.CODEC
        )
}