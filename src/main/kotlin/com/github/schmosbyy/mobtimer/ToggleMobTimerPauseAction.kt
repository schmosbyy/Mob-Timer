package com.github.schmosbyy.mobtimer

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager

class ToggleMobTimerPauseAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("PlayPauseTimer") // The ID of your tool window

        toolWindow?.let {
            if (!it.isVisible) {
                it.show(null) // Shows the tool window (this simulates the click to open)
            } else {
                it.hide(null) // Focuses on the tool window if it's already open (this simulates the click behavior)
            }
        }
    }
}