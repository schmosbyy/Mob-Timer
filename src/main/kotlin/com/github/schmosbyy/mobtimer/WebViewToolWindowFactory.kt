package com.github.schmosbyy.mobtimer

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.content.ContentFactory
import com.intellij.openapi.actionSystem.*
import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.Messages
import com.github.schmosbyy.mobtimer.settings.VehiklPluginSettings

class WebViewToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        (toolWindow as? ToolWindowEx)?.let { toolWindowEx ->
            val editAction = object : AnAction("Update Timer", "Change Timer Name", AllIcons.Actions.Edit) {
                override fun actionPerformed(e: AnActionEvent) {
                    UpdateTimerName().actionPerformed(e)
                }
            }

            val editUrlAction = object : AnAction("Update Mob URL", "Change Mob Timer URL", AllIcons.Javaee.WebServiceClient) {
                override fun actionPerformed(e: AnActionEvent) {
                    UpdateTimerUrl().actionPerformed(e)
                }
            }

            val titleActionGroup = DefaultActionGroup().apply {
                add(editAction)
                add(editUrlAction)
            }
            toolWindowEx.setTitleActions(titleActionGroup)
        }

        val webViewPanel = project.getService(WebViewPanel::class.java)
        val content = ContentFactory.getInstance().createContent(
            webViewPanel.getContent(),
            "",
            false
        )
        webViewPanel.initialize(toolWindow)
        toolWindow.contentManager.addContent(content)
        Disposer.register(toolWindow.disposable, webViewPanel)
    }
}
