package com.github.schmosbyy.mobtimer

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import com.github.schmosbyy.mobtimer.TimerStateManager.isTimerPaused
import com.github.schmosbyy.mobtimer.TimerStateManager.isToggleTriggered
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.jcef.JBCefBrowser

class ToggleMobTimerPauseAction : AnAction() {

    // Store ToolListener instance as a class property to ensure it's initialized only once
    private var toolListener: ToolListener? = null

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val toolWindowManager = ToolWindowManager.getInstance(project)

        // Retrieve both tool windows once and store them in variables
        val playPauseToolWindow = toolWindowManager.getToolWindow("PlayPauseTimer")
        val mobTimerToolWindow = toolWindowManager.getToolWindow("MobTimer")

        // Only get the browser instance if the MobTimer tool window is valid
        val browser = mobTimerToolWindow?.let { Utils.getBrowserInstance(it) }
        if (browser == null) return // Avoid further actions if browser could not be created

        // Initialize ToolListener once if it's not already initialized
        if (toolListener == null) {
            toolListener = ToolListener(project)
        }

        // If the playPauseToolWindow is not visible, show it and toggle the timer
        playPauseToolWindow?.apply {
            if (!isVisible) {
                show(null)
                isToggleTriggered = true
                toggleTimer(toolWindowManager, this, browser, project)
            }
        }
    }

    private fun toggleTimer(toolWindowManager: ToolWindowManager, toolWindow: ToolWindow, browser: JBCefBrowser?, project: Project) {
        // Use the already initialized toolListener
        toolListener?.let {
            if (isTimerPaused) {
                it.unPauseTimer(toolWindowManager, toolWindow, browser)
            } else {
                it.pauseTimer(toolWindowManager, toolWindow, browser)
            }
        }
    }
}