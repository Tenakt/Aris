package com.grindlesstudio.aris.skill

import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect

class ArisSkill(
    val id: String,
    val name: String,
    val description: String,
    val effect: Holder<MobEffect>
)