package com.grindlesstudio.aris.skill

import com.daqem.uilib.api.skilltree.ISkillTreeItem
import com.daqem.uilib.api.widget.skilltree.ISkillTreeItemWidget
import com.daqem.uilib.gui.widget.ButtonWidget
import com.daqem.knot.Knot
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.Component

class ArisSkillNodeWidget(
    private val item: ArisSkillItem
) : ButtonWidget(
    0,
    0,
    40,
    40,
    Component.literal(item.skill?.name ?: "ROOT"),
    { button ->

        val widget = button as ArisSkillNodeWidget
        val skill = widget.item.skill

        if (skill != null && !ArisSkillState.isUnlocked(skill.id)) {
            ArisSkillState.isUnlocked(skill.id)

            Knot.NETWORKING.sendToServer(
                ArisSkillNetworking.UnlockSkillPacket(
                    skill.id
                )
            )
        }
    }
), ISkillTreeItemWidget {

    init {
        item.skill?.let { skill ->
            setTooltip(
                Tooltip.create(
                    Component.literal(
                        "${skill.name}\n${skill.description}"
                    )
                )
            )
        }
    }

    override fun getSkillTreeItem(): ISkillTreeItem {
        return item
    }

    override fun renderTooltips(
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int
    ) {
    }

    override fun renderContents(
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        val skillId = item.skill?.id

        val unlocked =
            skillId != null &&
                    ArisSkillState.isUnlocked(skillId)

        val color = when {
            item.isRoot() -> 0xFF555555.toInt()
            unlocked -> 0xFF55AA55.toInt()
            isHoveredOrFocused -> 0xFFAAAA55.toInt()
            else -> 0xFF5555AA.toInt()
        }

        guiGraphics.fill(
            x,
            y,
            x + width,
            y + height,
            color
        )

        renderDefaultLabel(
            guiGraphics.textRendererForWidget(
                this,
                GuiGraphics.HoveredTextEffects.NONE
            )
        )
    }
}