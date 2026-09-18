package com.grindlesstudio.aris.skill

import net.minecraft.world.effect.MobEffects

object ArisSkillTrees {

    fun create(): ArisSkillTree {

        val speed = ArisSkill(
            id = "speed",
            name = "Speed",
            description = "Move faster",
            effect = MobEffects.SPEED
        )

        val nightVision = ArisSkill(
            id = "night_vision",
            name = "Night Vision",
            description = "See in the dark",
            effect = MobEffects.NIGHT_VISION
        )

        val jump = ArisSkill(
            id = "jump",
            name = "Jump",
            description = "Jump higher",
            effect = MobEffects.JUMP_BOOST
        )

        val haste = ArisSkill(
            id = "haste",
            name = "Haste",
            description = "Mine faster.",
            effect = MobEffects.HASTE
        )

        val root = ArisSkillItem(
            skill = null,
            root = true,
            children = mutableListOf()
        )

        val speedItem = ArisSkillItem(
            skill = speed,
            root = false,
            children = mutableListOf()
        )

        val jumpItem = ArisSkillItem(
            skill = jump,
            root = false,
            children = mutableListOf()
        )

        val hasteItem = ArisSkillItem(
            skill = haste,
            root = false,
            children = mutableListOf()
        )

        val nightVisionItem = ArisSkillItem(
            skill = nightVision,
            root = false,
            children = mutableListOf()
        )

        root.addChild(speedItem)
        root.addChild(jumpItem)
        root.addChild(nightVisionItem)
        speedItem.addChild(hasteItem)

        return ArisSkillTree(
            listOf(
                root,
                speedItem,
                nightVisionItem,
                jumpItem,
                hasteItem
            )
        )
    }
}