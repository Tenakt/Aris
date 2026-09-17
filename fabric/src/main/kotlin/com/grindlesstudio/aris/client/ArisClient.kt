package com.grindlesstudio.aris.client

import com.grindlesstudio.aris.block.ModBlocks
import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.Minecraft
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

//		KeyMappingRegistry.register(
//			ArisKeyMappings.OPEN_COIN_SCREEN
//		)
//
//		ClientTickEvent.CLIENT_POST.register {
//
//			if (ArisKeyMappings.OPEN_COIN_SCREEN.consumeClick()) {
//				Minecraft.getInstance().setScreen(
//					CoinScreen()
//				)
//			}
//		}
	}
}