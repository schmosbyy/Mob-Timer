package com.github.schmosbyy.mobtimer

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.WindowManager
import com.github.schmosbyy.mobtimer.TimerStateManager.isToggleTriggered
import org.cef.CefSettings
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefDisplayHandlerAdapter
import org.cef.handler.CefLoadHandler
import org.cef.network.CefRequest

class ChromiumDisplayHandler(
    private val toolWindow: ToolWindow,
    private val project: Project
) : CefDisplayHandlerAdapter(), CefLoadHandler, Disposable {
    private val LOG = Logger.getInstance(ChromiumDisplayHandler::class.java)
    @Volatile
    private var isPaused: Boolean = false
    private var lastTitleTime: String? = "00:60"

    init {
        println("ChromiumDisplayHandler initialized")
    }

    private var currentState: TimerState = TimerState.PAUSED
    private val playPauseToolWindow by lazy {
        ToolWindowManager.getInstance(project).getToolWindow("PlayPauseTimer")
    }

    override fun onConsoleMessage(cefBrowser: CefBrowser, logSeverity: CefSettings.LogSeverity, message: String, source: String, line: Int): Boolean {
        if(message.contains("skipTriggered")){
            handleTimerEnded();
            TimerStateManager.isTimerPaused = true
        }
        if(message.contains("Driver Name: ")){
            updateStatusBar(message);
        }
        return false // No action needed on console message
    }
    private fun updateStatusBar(message: String) {
        ApplicationManager.getApplication().invokeLater {
            val statusBar = WindowManager.getInstance().getStatusBar(project)
            val existingWidget = statusBar.getWidget("CustomStatusBarWidget")
            if (existingWidget != null) {
                (existingWidget as? CustomStatusBarWidget)?.updateText(message)
                statusBar.updateWidget(existingWidget.ID())
            } else {
                if (!project.isDisposed) {
                    val customWidget = CustomStatusBarWidget()
                    statusBar.addWidget(customWidget, "before PositionWidget", project)
                    customWidget.updateText(message)
                    statusBar.updateWidget(customWidget.ID())
                }
            }
        }
    }


    override fun onTitleChange(browser: CefBrowser?, title: String?) {
        if (lastTitleTime!!.contains(':') && lastTitleTime!!.split(":").let { it[0].toInt() * 60 + it[1].toInt() } >= 2
            && title == "mobtime") {
            if(isToggleTriggered){
                isToggleTriggered=false
            }else{
                Utils.executeisSkipTriggered(browser)
            }
            return
        }
        lastTitleTime = title?.split('-')?.get(0).toString().trim()

        ApplicationManager.getApplication().invokeLater {
            toolWindow.title = title ?: ""

            // If we see a timer value in the title and we're not paused, ensure we're showing running state
            if (!isPaused && title?.matches(".*\\d+.*mobtime.*".toRegex()) == true) {
                if (currentState != TimerState.RUNNING) {
                    handleTimerRunning(browser)
                }
            }
            // If we see the ended state, handle it regardless of pause state
            if (title == "mobtime") {
                handleTimerEnded()
            }
        }
    }

    private fun handleTimerRunning(browser: CefBrowser?) {
        if (currentState != TimerState.RUNNING) {
            currentState = TimerState.RUNNING
            isPaused = false
            
            ApplicationManager.getApplication().invokeLater {
                try {
                    Utils.getProgressCircle(toolWindow)
                    Utils.executeGenerateDriverName(browser)
                    playPauseToolWindow?.setIcon(AllIcons.Actions.Pause)
                } catch (e: Exception) {
                    println("Failed to set spinning icon: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    private fun handleTimerEnded() {
        if (currentState != TimerState.ENDED) {
            currentState = TimerState.ENDED
            
            ApplicationManager.getApplication().invokeLater {
                toolWindow.setIcon(AllIcons.Actions.ProfileRed)
                playPauseToolWindow?.setIcon(AllIcons.Actions.Resume)
                Utils.triggerNotification("The mob timer is up!")
                updateStatusBar("Timer Up!");
            }
        }
    }

    override fun dispose() {
        LOG.info("Disposing ChromiumDisplayHandler")
    }

    override fun onLoadingStateChange(browser: CefBrowser?, isLoading: Boolean, canGoBack: Boolean, canGoForward: Boolean) {
        Utils.executeDisplayNone(browser)
    }

    override fun onLoadStart(browser: CefBrowser?, frame: CefFrame?, transitionType: CefRequest.TransitionType?) {
        // Not needed, but required by interface
    }

    override fun onLoadEnd(browser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {
        // Not needed, but required by interface
    }

    override fun onLoadError(browser: CefBrowser?, frame: CefFrame?, errorCode: CefLoadHandler.ErrorCode?, errorText: String?, failedUrl: String?) {
        // Not needed, but required by interface
    }
}

