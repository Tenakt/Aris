package com.grindlesstudio.aris.client

import com.grindlesstudio.aris.block.ModBlocks
import net.minecraft.client.renderer.BiomeColors
import net.minecraft.world.level.GrassColor
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent

@EventBusSubscriber(
    modid = "aris",
    value = [Dist.CLIENT]
)
object ArisNeoForgeClient {

    @JvmStatic
    @SubscribeEvent
    fun registerBlockColors(event: RegisterColorHandlersEvent.Block) {

        // Цвет травы для Grass Slab
        event.register(
            { _, level, pos, _ ->
                if (level != null && pos != null) {
                    BiomeColors.getAverageGrassColor(level, pos)
                } else {
                    GrassColor.getDefaultColor()
                }
            },
            ModBlocks.GRASS_SLAB
        )

        // Цвет листвы для Aris Taiga Leaves
        event.register(
            { _, level, pos, _ ->
                if (level != null && pos != null) {
                    BiomeColors.getAverageFoliageColor(level, pos)
                } else {
                    4764952
                }
            },
            ModBlocks.TAIGA_LEAVES
        )
    }
}