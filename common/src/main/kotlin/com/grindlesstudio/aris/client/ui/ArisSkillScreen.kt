//package com.grindlesstudio.aris.client.ui
//
//import com.daqem.uilib.gui.component.skilltree.SkillTreeComponent
//import com.grindlesstudio.aris.skill.ArisSkillTrees
//import net.minecraft.client.gui.screens.Screen
//import net.minecraft.network.chat.Component
//
//class ArisSkillScreen : Screen(
//    Component.literal("Aris Skills")
//) {
//
//    override fun init() {
//        super.init()
//
//        val tree = ArisSkillTrees.create()
//
//        val treeView = SkillTreeComponent(
//            20,
//            20,
//            width - 40,
//            height - 40,
//            tree
//        )
//
//        addComponent(treeView)
//    }
//}
