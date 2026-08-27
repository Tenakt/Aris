package com.grindlesstudio.aris.worldgen.biome

import com.grindlesstudio.aris.worldgen.terrain.ArisRegion
import com.grindlesstudio.aris.worldgen.terrain.ArisTerrain
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.biome.source.util.MultiNoiseUtil
import java.util.stream.Stream

class ArisBiomeSource(
    private val ocean: RegistryEntry<Biome>,
    private val plains: RegistryEntry<Biome>,
    private val taiga: RegistryEntry<Biome>,
    private val mountains: RegistryEntry<Biome>
) : BiomeSource() {

    companion object {

        val CODEC: MapCodec<ArisBiomeSource> =
            RecordCodecBuilder.mapCodec { instance ->

                instance.group(

                    Biome.REGISTRY_CODEC
                        .fieldOf("ocean")
                        .forGetter { source ->
                            source.ocean
                        },

                    Biome.REGISTRY_CODEC
                        .fieldOf("plains")
                        .forGetter { source ->
                            source.plains
                        },

                    Biome.REGISTRY_CODEC
                        .fieldOf("taiga")
                        .forGetter { source ->
                            source.taiga
                        },

                    Biome.REGISTRY_CODEC
                        .fieldOf("mountains")
                        .forGetter { source ->
                            source.mountains
                        }

                ).apply(
                    instance,
                    ::ArisBiomeSource
                )
            }
    }

    override fun getCodec(): MapCodec<out BiomeSource> {
        return CODEC
    }

    override fun getBiome(
        x: Int,
        y: Int,
        z: Int,
        noise: MultiNoiseUtil.MultiNoiseSampler
    ): RegistryEntry<Biome> {

        val region = ArisTerrain.getRegion(
            x,
            z,
            0L
        )

        return when (region) {

            ArisRegion.OCEAN ->
                ocean

            ArisRegion.PLAINS ->
                plains

            ArisRegion.TAIGA ->
                taiga

            ArisRegion.MOUNTAINS ->
                mountains
        }
    }

    override fun biomeStream(): Stream<RegistryEntry<Biome>> {

        return Stream.of(
            ocean,
            plains,
            taiga,
            mountains
        )
    }
}