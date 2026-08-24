package com.grindlesstudio.aris.client

import com.grindlesstudio.aris.block.ModBlocks
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.color.world.BiomeColors
import net.minecraft.world.biome.GrassColors

class ArisClient : ClientModInitializer {
	override fun onInitializeClient() {
		// В Minecraft 1.21.11 цвет регистрируется только для блока
		ColorProviderRegistry.BLOCK.register(
			{ _, world, pos, _ ->
				if (world != null && pos != null) {
					BiomeColors.getGrassColor(world, pos)
				} else {
					GrassColors.getDefaultColor()
				}
			},
			ModBlocks.GRASS_SLAB
		)
	}
}