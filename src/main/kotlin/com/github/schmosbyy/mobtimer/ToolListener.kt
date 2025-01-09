package com.github.schmosbyy.mobtimer

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import com.intellij.ui.jcef.JBCefBrowser
import com.github.schmosbyy.mobtimer.TimerStateManager.isTimerPaused
import com.github.schmosbyy.mobtimer.TimerStateManager.isToggleTriggered
import com.github.schmosbyy.mobtimer.TimerStateManager.webViewOpenedOnce
import org.jetbrains.annotations.NotNull

class ToolListener(private val project: Project) : ToolWindowManagerListener {
    override fun stateChanged(@NotNull toolWindowManager: ToolWindowManager) {
        val toolWindow = toolWindowManager.getToolWindow(toolWindowManager.activeToolWindowId) ?: return
        if(toolWindow.id == "MobTimer" && !webViewOpenedOnce){
            webViewOpenedOnce=true
        }
        if(webViewOpenedOnce){
            if (toolWindow.id == "PlayPauseTimer" && toolWindow.isVisible) {
                toggleTimer(toolWindowManager, toolWindow)
            }
        }
    }

    private fun toggleTimer(toolWindowManager: ToolWindowManager, toolWindow: ToolWindow) {
        val browser = getBrowserInstance(toolWindowManager.getToolWindow("MobTimer"))
        isToggleTriggered=true
        if (isTimerPaused) {
            unPauseTimer(toolWindowManager, toolWindow, browser)
        } else {
            pauseTimer(toolWindowManager,toolWindow, browser)
        }
    }

    private fun unPauseTimer(toolWindowManager: ToolWindowManager, toolWindow: ToolWindow, browser: JBCefBrowser?) {
        toolWindow.isAvailable = false
        isTimerPaused = false
        Utils.triggerNotification("Mob Timer started!")
        executeClickAction(browser)
        ApplicationManager.getApplication().invokeLater {
            Utils.getProgressCircle(toolWindowManager.getToolWindow("MobTimer")!!)
            toolWindow.setIcon(AllIcons.Actions.Pause)
            Utils.executeGenerateDriverName(browser?.cefBrowser)
        }
        toolWindow.isAvailable = true
    }

    private fun pauseTimer(toolWindowManager: ToolWindowManager, toolWindow: ToolWindow, browser: JBCefBrowser?) {
        toolWindow.isAvailable = false // Set tool window as unavailable during the pause state
        Utils.triggerNotification("Mob Timer paused!")
            isTimerPaused = true
        ApplicationManager.getApplication().invokeLater {
            executeClickAction(browser)
            toolWindow.setIcon(AllIcons.Actions.Pause)
            toolWindowManager.getToolWindow("MobTimer")?.setIcon(AllIcons.Actions.ProfileRed)
            Utils.executeGenerateDriverName(browser?.cefBrowser)
        }
        toolWindow.isAvailable = true
    }

    private fun getBrowserInstance(toolWindow: ToolWindow?): JBCefBrowser? {
        val content = toolWindow?.contentManager?.getContent(0)
        return if (content?.component is JBCefBrowser.MyPanel) {
            (content.component as JBCefBrowser.MyPanel).jbCefBrowser
        } else {
            println("Component is not JBCefBrowser.MyPanel!")
            null
        }
    }

    private fun executeClickAction(browser: JBCefBrowser?) {
        val jsCode = """
            var startButton = document.querySelector('button.mr-1.px-2.py-1.border.border-slate-700:not(.bg-slate-200):not(.text-black):not(.cursor-not-allowed)');
            if (startButton) {
                startButton.click();
            }
        """
        browser?.cefBrowser?.executeJavaScript(jsCode, browser.cefBrowser.url, 0)
    }

}
