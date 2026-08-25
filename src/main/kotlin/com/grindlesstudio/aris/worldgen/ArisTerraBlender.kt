package com.grindlesstudio.aris.worldgen

import com.grindlesstudio.aris.Aris
import com.mojang.datafixers.util.Pair
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeKeys
import net.minecraft.world.biome.source.util.MultiNoiseUtil
import terrablender.api.ParameterUtils
import terrablender.api.Region
import terrablender.api.RegionType
import terrablender.api.Regions
import terrablender.api.SurfaceRuleManager
import terrablender.api.TerraBlenderApi
import java.util.function.Consumer

class ArisTerraBlender : TerraBlenderApi {
    override fun onTerraBlenderInitialized() {
        // Вес 10 даст паритет с ванильным генератором (50/50)
        Regions.register(ArisOverworldRegion(Aris.id("overworld_region"), 10))

        SurfaceRuleManager.addSurfaceRules(
            SurfaceRuleManager.RuleCategory.OVERWORLD,
            Aris.MOD_ID,
            ArisSurfaceRules.makeRules()
        )
    }
}

class ArisOverworldRegion(name: Identifier, weight: Int) : Region(name, RegionType.OVERWORLD, weight) {
    override fun addBiomes(
        registry: Registry<Biome>,
        mapper: Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>>
    ) {
        // 1. Тайга
        val taigaPoints = ParameterUtils.ParameterPointListBuilder()
            .temperature(ParameterUtils.Temperature.COOL, ParameterUtils.Temperature.FROZEN)
            .humidity(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET)
            .continentalness(ParameterUtils.Continentalness.FAR_INLAND)
            .erosion(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2)
            .depth(ParameterUtils.Depth.SURFACE)
            .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
            .build()

        for (point in taigaPoints) {
            mapper.accept(Pair.of(point, BiomeKeys.TAIGA))
        }

        // 2. Равнины
        val plainsPoints = ParameterUtils.ParameterPointListBuilder()
            .temperature(ParameterUtils.Temperature.NEUTRAL, ParameterUtils.Temperature.WARM)
            .humidity(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.NEUTRAL)
            .continentalness(ParameterUtils.Continentalness.FAR_INLAND)
            .erosion(ParameterUtils.Erosion.EROSION_5, ParameterUtils.Erosion.EROSION_6)
            .depth(ParameterUtils.Depth.SURFACE)
            .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
            .build()

        for (point in plainsPoints) {
            mapper.accept(Pair.of(point, BiomeKeys.PLAINS))
        }
    }
}