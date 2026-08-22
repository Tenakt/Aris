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
        Regions.register(ArisOverworldRegion(Aris.id("overworld_region"), 4))

        // Добавляем скалистые склоны для Верхнего мира
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
        val points = ParameterUtils.ParameterPointListBuilder()
            .temperature(ParameterUtils.Temperature.COOL, ParameterUtils.Temperature.FROZEN)
            .humidity(ParameterUtils.Humidity.NEUTRAL)
            .continentalness(ParameterUtils.Continentalness.FAR_INLAND)
            .erosion(ParameterUtils.Erosion.EROSION_5, ParameterUtils.Erosion.EROSION_6) // Холмы и горы
            .depth(ParameterUtils.Depth.SURFACE)
            .weirdness(ParameterUtils.Weirdness.LOW_SLICE_NORMAL_DESCENDING, ParameterUtils.Weirdness.MID_SLICE_NORMAL_ASCENDING)
            .build()

        for (point in points) {
            mapper.accept(Pair.of(point, BiomeKeys.TAIGA))
        }
    }
}