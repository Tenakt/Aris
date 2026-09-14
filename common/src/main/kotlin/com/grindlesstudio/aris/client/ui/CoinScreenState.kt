package com.grindlesstudio.aris.client.ui

object CoinScreenState {

    var coins: Int = 0

    fun addCoin() {
        coins++
    }

    fun reset() {
        coins = 0
    }
}