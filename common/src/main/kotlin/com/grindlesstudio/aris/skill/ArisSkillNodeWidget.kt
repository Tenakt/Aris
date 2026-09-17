//
//package com.grindlesstudio.aris.skill
//
//import com.daqem.uilib.gui.widget.CustomButtonWidget
//import com.daqem.uilib.api.skilltree.ISkillTreeItem
//import com.daqem.uilib.api.widget.skilltree.ISkillTreeItemWidget
//import net.minecraft.network.chat.Component
//import net.minecraft.client.gui.components.Tooltip
//
//class ArisSkillNodeWidget(
//    private val item: ArisSkillItem
//) : CustomButtonWidget(
//    0,
//    0,
//    40,
//    40,
//    Component.literal(item.skill?.name ?: ""),
//    /* WidgetSprites */
//), ISkillTreeItemWidget {
//
//    private var unlocked = false
//
//    init {
//        item.skill?.let { skill ->
//            tooltip = Tooltip.create(
//                Component.literal(
//                    "${skill.name}\n${skill.description}"
//                )
//            )
//        }
//    }
//
//    override fun getSkillTreeItem(): ISkillTreeItem {
//        return item
//    }
//
//    private fun unlock() {
//        if (unlocked) {
//            return
//        }
//
//        unlocked = true
//
//        // Здесь позже добавим выдачу Minecraft Effect.
//    }
//}
