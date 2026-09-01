package com.grindlesstudio.aris.registry

import com.grindlesstudio.aris.Aris
import com.grindlesstudio.aris.worldgen.feature.SlopeSlabFeature
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

object ModFeatures {

    val SLOPE_SLAB: Feature<NoneFeatureConfiguration> =
        SlopeSlabFeature(
            NoneFeatureConfiguration.CODEC
        )

    fun registerFabric() {
        Registry.register(
            BuiltInRegistries.FEATURE,
            Aris.id("slope_slab"),
            SLOPE_SLAB
        )

        Aris.LOGGER.info("Aris features registered")
    }
}