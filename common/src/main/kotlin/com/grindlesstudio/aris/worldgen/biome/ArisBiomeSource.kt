package com.grindlesstudio.aris.worldgen.biome

import com.grindlesstudio.aris.worldgen.terrain.ArisRegion
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeSource
import net.minecraft.world.level.biome.Climate
import java.util.stream.Stream

class ArisBiomeSource(
    private val ocean: Holder<Biome>,
    private val plains: Holder<Biome>,
    private val taiga: Holder<Biome>,
    private val hills: Holder<Biome>,
    private val desert: Holder<Biome>
) : BiomeSource() {

    companion object {
        val CODEC: MapCodec<ArisBiomeSource> =
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Biome.CODEC.fieldOf("ocean").forGetter { source -> source.ocean },
                    Biome.CODEC.fieldOf("plains").forGetter { source -> source.plains },
                    Biome.CODEC.fieldOf("taiga").forGetter { source -> source.taiga },
                    Biome.CODEC.fieldOf("hills").forGetter { source -> source.hills },
                    Biome.CODEC.fieldOf("desert").forGetter { source -> source.desert }
                ).apply(instance, ::ArisBiomeSource)
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
        // 1. Получаем точное значение континентальности из генератора шумов Minecraft (от -1.0 до 1.0)
        val targetPoint = noise.sample(x, y, z)
        val continentalness = Climate.unquantizeCoord(targetPoint.continentalness())

        // 2. Если шумом зафиксирован океан (континентальность отрицательная), возвращаем биом океана
        if (continentalness <= -0.30f) {
            return ocean
        }

        // 3. Для суши определяем наземный биом (равнины, тайга, горы)
        val blockX = x shl 2
        val blockZ = z shl 2
        val region = ArisBiomeRegion.getRegion(blockX, blockZ, 0L)

        return when (region) {
            ArisRegion.OCEAN -> ocean
            ArisRegion.HILLS -> hills
            ArisRegion.TAIGA -> taiga
            ArisRegion.PLAINS -> plains
            ArisRegion.DESERT -> desert
        }
    }

    override fun collectPossibleBiomes(): Stream<Holder<Biome>> {
        return Stream.of(
            ocean,
            plains,
            taiga,
            hills,
            desert
        )
    }
}