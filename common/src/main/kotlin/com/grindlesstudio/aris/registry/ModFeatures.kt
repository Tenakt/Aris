package com.grindlesstudio.aris.registry

import com.grindlesstudio.aris.Aris
import com.grindlesstudio.aris.worldgen.feature.SlopeSlabFeature
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature

object ModFeatures {
    val SLOPE_SLAB: Feature<DefaultFeatureConfig> = Registry.register(
        Registries.FEATURE,
        Aris.id("slope_slab"),
        SlopeSlabFeature(DefaultFeatureConfig.CODEC)
    )

    fun registerFeatures() {
        Aris.LOGGER.info("Registering Mod Features for " + Aris.MOD_ID)
    }
}