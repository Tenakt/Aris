package com.grindlesstudio.aris

import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Aris {
    const val MOD_ID = "aris"

    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    fun id(path: String): Identifier {
        return Identifier.of(MOD_ID, path)
    }

    fun initialize() {
        LOGGER.info("Aris initialized")
    }
}