package com.grindlesstudio.aris

import com.grindlesstudio.aris.worldgen.ArisSurfaceRules
import com.mojang.datafixers.util.Pair
import terrablender.api.Regions
import terrablender.api.ParameterUtils
import terrablender.api.Region
import terrablender.api.RegionType
import terrablender.api.SurfaceRuleManager
import terrablender.api.TerraBlenderApi

import net.minecraft.core.Registry
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.Climate

import java.util.function.Consumer

class ArisFabricTerraBlender : TerraBlenderApi {

    override fun onTerraBlenderInitialized() {

        Regions.register(
            ArisOverworldRegion(
                Aris.id("overworld_region"),
                10
            )
        )

        SurfaceRuleManager.addSurfaceRules(
            SurfaceRuleManager.RuleCategory.OVERWORLD,
            Aris.MOD_ID,
            ArisSurfaceRules.makeRules()
        )
    }
}

class ArisOverworldRegion(
    name: Identifier,
    weight: Int
) : Region(name, RegionType.OVERWORLD, weight) {

    override fun addBiomes(
        registry: Registry<Biome>,
        mapper: Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>>
    ) {

        val taigaPoints = ParameterUtils.ParameterPointListBuilder()
            .temperature(
                ParameterUtils.Temperature.COOL,
                ParameterUtils.Temperature.FROZEN
            )
            .humidity(
                ParameterUtils.Humidity.NEUTRAL,
                ParameterUtils.Humidity.WET
            )
            .continentalness(
                ParameterUtils.Continentalness.FAR_INLAND
            )
            .erosion(
                ParameterUtils.Erosion.EROSION_0,
                ParameterUtils.Erosion.EROSION_2
            )
            .depth(
                ParameterUtils.Depth.SURFACE
            )
            .weirdness(
                ParameterUtils.Weirdness.FULL_RANGE
            )
            .build()

        for (point in taigaPoints) {
            mapper.accept(
                Pair.of(
                    point,
                    Biomes.TAIGA
                )
            )
        }

        val plainsPoints = ParameterUtils.ParameterPointListBuilder()
            .temperature(ParameterUtils.Temperature.NEUTRAL, ParameterUtils.Temperature.WARM)
            .humidity(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.NEUTRAL)
            .continentalness(ParameterUtils.Continentalness.FAR_INLAND)
            .erosion(ParameterUtils.Erosion.EROSION_5, ParameterUtils.Erosion.EROSION_6)
            .depth(ParameterUtils.Depth.SURFACE)
            .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
            .build()

        for (point in plainsPoints) {
            mapper.accept(
                Pair.of(
                    point,
                    Biomes.PLAINS
                )
            )
        }
    }
}
