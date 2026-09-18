package com.grindlesstudio.aris.client.ui

import com.daqem.uilib.gui.AbstractScreen
import com.daqem.uilib.gui.component.skilltree.SkillTreeComponent
import com.grindlesstudio.aris.skill.ArisSkillTrees
import net.minecraft.network.chat.Component

class ArisSkillScreen : AbstractScreen(
    Component.literal("Aris Skills")
) {

    override fun init() {
        super.init()

        val skillTree = SkillTreeComponent(
            20,
            20,
            width - 40,
            height - 40,
            ArisSkillTrees.create()
        )

        addComponent(skillTree)
    }
}