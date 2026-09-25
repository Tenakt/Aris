package com.grindlesstudio.aris.registry

import com.grindlesstudio.aris.Aris
import com.grindlesstudio.aris.worldgen.feature.SlopeSlabFeature
import com.grindlesstudio.aris.worldgen.feature.road.VillageRoadFeature
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
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

    fun register() {
        Registry.register(
            BuiltInRegistries.FEATURE,
            Aris.id("slope_slab"),
            SLOPE_SLAB
        )
        Registry.register(
            BuiltInRegistries.FEATURE,
            Aris.id("village_road"),
            VILLAGE_ROAD
        )

        Aris.LOGGER.info("Aris features registered")
    }
}