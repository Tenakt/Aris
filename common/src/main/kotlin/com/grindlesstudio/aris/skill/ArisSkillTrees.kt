//package com.grindlesstudio.aris.skill
//
//import net.minecraft.world.effect.MobEffects
//
//object ArisSkillTrees {
//
//    fun create(): ArisSkillTree {
//
//        val speed = ArisSkill(
//            id = "speed",
//            name = "Speed",
//            description = "Move faster.",
//            effect = MobEffects.SPEED
//        )
//
//        val jump = ArisSkill(
//            id = "jump",
//            name = "Jump",
//            description = "Jump higher.",
//            effect = MobEffects.JUMP_BOOST
//        )
//
//        val haste = ArisSkill(
//            id = "haste",
//            name = "Haste",
//            description = "Mine faster.",
//            effect = MobEffects.HASTE
//        )
//
//        val root = ArisSkillItem(
//            skill = null,
//            isRoot = true,
//            children = mutableListOf()
//        )
//
//        val speedItem = ArisSkillItem(
//            skill = speed,
//            isRoot = false,
//            children = mutableListOf()
//        )
//
//        val jumpItem = ArisSkillItem(
//            skill = jump,
//            isRoot = false,
//            children = mutableListOf()
//        )
//
//        val hasteItem = ArisSkillItem(
//            skill = haste,
//            isRoot = false,
//            children = mutableListOf()
//        )
//
//        root.addChild(speedItem)
//        root.addChild(jumpItem)
//
//        speedItem.addChild(hasteItem)
//
//        return ArisSkillTree(
//            listOf(
//                root,
//                speedItem,
//                jumpItem,
//                hasteItem
//            )
//        )
//    }
//}
