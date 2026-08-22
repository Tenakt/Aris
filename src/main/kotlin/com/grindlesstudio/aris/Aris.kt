package com.grindlesstudio.aris

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Aris : ModInitializer {
	const val MOD_ID: String = "aris"
	val LOGGER = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		LOGGER.info("Hello Fabric world!")
	}

	fun id(path: String): Identifier
			= Identifier.of(MOD_ID, path)
}