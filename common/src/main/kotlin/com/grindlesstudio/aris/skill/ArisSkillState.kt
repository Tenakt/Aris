package com.grindlesstudio.aris.skill

object ArisSkillState {

    private val unlockedSkills = mutableSetOf<String>()

    fun isUnlocked(skillId: String): Boolean {
        return skillId in unlockedSkills
    }

    fun setUnlocked(skillIds: Collection<String>) {
        unlockedSkills.clear()
        unlockedSkills.addAll(skillIds)
    }

    fun clear() {
        unlockedSkills.clear()
    }
}