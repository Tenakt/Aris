package com.grindlesstudio.aris.client

import com.grindlesstudio.aris.block.ModBlocks
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.renderer.BiomeColors
import net.minecraft.world.level.GrassColor

class ArisClient : ClientModInitializer {

	override fun onInitializeClient() {

		// Цвет травы для Grass Slab
		ColorProviderRegistry.BLOCK.register(
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
		ColorProviderRegistry.BLOCK.register(
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