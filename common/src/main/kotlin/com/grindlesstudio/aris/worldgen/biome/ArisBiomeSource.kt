package com.grindlesstudio.aris.worldgen.biome

import com.grindlesstudio.aris.worldgen.terrain.ArisRegion
import com.grindlesstudio.aris.worldgen.terrain.ArisTerrain
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeSource
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.Climate
import java.util.stream.Stream

class ArisBiomeSource(
    private val ocean: Holder<Biome>,
    private val plains: Holder<Biome>,
    private val taiga: Holder<Biome>,
    private val mountains: Holder<Biome>
) : BiomeSource() {

    companion object {

        val CODEC: MapCodec<ArisBiomeSource> =
            RecordCodecBuilder.mapCodec { instance ->

                instance.group(

                    Biome.CODEC
                        .fieldOf("ocean")
                        .forGetter { source ->
                            source.ocean
                        },

                    Biome.CODEC
                        .fieldOf("plains")
                        .forGetter { source ->
                            source.plains
                        },

                    Biome.CODEC
                        .fieldOf("taiga")
                        .forGetter { source ->
                            source.taiga
                        },

                    Biome.CODEC
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

    override fun codec(): MapCodec<out BiomeSource> {
        return CODEC
    }

    override fun getNoiseBiome(
        x: Int,
        y: Int,
        z: Int,
        noise: Climate.Sampler
    ): Holder<Biome> {

        val region =
            ArisTerrain.getRegion(
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

    override fun collectPossibleBiomes(): Stream<Holder<Biome>> {

        return Stream.of(
            ocean,
            plains,
            taiga,
            mountains
        )
    }
}