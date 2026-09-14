package com.grindlesstudio.aris.client.ui

import com.daqem.uilib.gui.component.text.TextComponent
import com.daqem.uilib.gui.widget.ButtonWidget
import com.daqem.uilib.gui.AbstractScreen
import net.minecraft.network.chat.Component

class CoinScreen : AbstractScreen(
    Component.literal("Coins")
) {

    private lateinit var coinText: TextComponent

    override fun init() {
        super.init()

        val centerX = width / 2
        val centerY = height / 2

        coinText = TextComponent(
            centerX,
            centerY - 30,
            Component.literal("Coins: ${CoinScreenState.coins}"),
            0xFFFFFFFF.toInt()
        )

        coinText.setTextAlign(
            com.daqem.uilib.gui.component.text.TextAlign.CENTER
        )

        addComponent(coinText)

        val coinButton = ButtonWidget(
            centerX - 50,
            centerY,
            100,
            20,
            Component.literal("+ Coin")
        ) {
            CoinScreenState.addCoin()
        }

        val resetButton = ButtonWidget(
            centerX - 50,
            centerY + 25,
            100,
            20,
            Component.literal("Reset")
        ) {
            CoinScreenState.reset()
        }

        addWidget(resetButton)

        addWidget(coinButton)
    }
    override fun render(
        guiGraphics: net.minecraft.client.gui.GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        coinText.setText(
            Component.literal("Coins: ${CoinScreenState.coins}")
        )

        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }
}