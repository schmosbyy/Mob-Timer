package com.github.schmosbyy.mobtimer

import com.github.schmosbyy.mobtimer.settings.VehiklPluginSettings
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager

class UpdateTimerName : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val settings = VehiklPluginSettings.getInstance(project)
        
        val currentTimer = settings.timerName
        val newTimer = Messages.showInputDialog(
            project,
            "Enter Mob Timer Name:",
            "Update Mob Timer",
            Messages.getQuestionIcon(),
            currentTimer,
            null
        )

        if (!newTimer.isNullOrBlank()) {
            settings.timerName = newTimer
            
            // Force reload all WebView instances
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("MobTimer")
            toolWindow?.contentManager?.contents?.forEach { content ->
                // Just update the settings and let the WebViewPanel handle the refresh
                content.displayName = "Mob Timer: $newTimer"
            }
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}