package com.grindlesstudio.aris

import org.slf4j.LoggerFactory

object Aris {

    const val MOD_ID = "aris"

    val LOGGER =
        LoggerFactory.getLogger(MOD_ID)

    fun initialize() {

        LOGGER.info("========================================")
        LOGGER.info("Aris common initialized")
        LOGGER.info("========================================")
    }
}