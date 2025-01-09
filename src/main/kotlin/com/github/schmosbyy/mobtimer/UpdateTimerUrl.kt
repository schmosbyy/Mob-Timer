package com.github.schmosbyy.mobtimer

import com.github.schmosbyy.mobtimer.settings.VehiklPluginSettings
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager

class UpdateTimerUrl : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val settings = VehiklPluginSettings.getInstance(project)

        val currentUrl = settings.mobUrl
        val newUrl = Messages.showInputDialog(
            project,
            "Enter Mob Timer URL:",
            "Update Mob Timer URL",
            Messages.getQuestionIcon(),
            currentUrl,
            null
        )

        if (!newUrl.isNullOrBlank()) {
            settings.mobUrl = newUrl
            // Update the WebViewPanel URL
            val webViewPanel = project.getService(WebViewPanel::class.java)
            webViewPanel.reloadWebView(newUrl)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}