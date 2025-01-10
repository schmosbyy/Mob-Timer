package com.github.schmosbyy.mobtimer

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager

class ToggleMobTimerAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        println("here")
        val project = event.project ?: return
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val mobTimer = toolWindowManager.getToolWindow("MobTimer")
        mobTimer?.let {
            if (it.isVisible) {
                it.hide(null)
            } else {
                it.show(null)
            }
        }
    }
}