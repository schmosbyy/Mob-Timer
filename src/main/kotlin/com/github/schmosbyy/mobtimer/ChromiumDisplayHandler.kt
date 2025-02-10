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
import com.intellij.openapi.wm.StatusBarWidgetFactory
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
    private var driverName: String? = ""

    init {
        println("ChromiumDisplayHandler initialized")
    }

    private var currentState: TimerState = TimerState.PAUSED
    private val playPauseToolWindow by lazy {
        ToolWindowManager.getInstance(project).getToolWindow("PlayPauseTimer")
    }

    override fun onConsoleMessage(cefBrowser: CefBrowser, logSeverity: CefSettings.LogSeverity, message: String, source: String, line: Int): Boolean {
        if(message.contains("skipTriggered")){
            handleTimerEnded(cefBrowser);
            TimerStateManager.isTimerPaused = true
        }
        if(message.contains("Driver :")){
            driverName = message
        }
        if(message.contains("Next Driver:")){
            updateStatusBar("Timer Up! [$message]");
        }

        return false // No action needed on console message
    }
    private fun updateStatusBar(message: String) {
        ApplicationManager.getApplication().invokeLater {
            if (project.isDisposed) return@invokeLater

            val statusBar = WindowManager.getInstance().getStatusBar(project)
            val widgetId = CustomStatusBarWidgetFactory.WIDGET_ID
            val existingWidget = statusBar.getWidget(widgetId)
            if (existingWidget is CustomStatusBarWidget) {
                existingWidget.updateText(message)
                statusBar.updateWidget(widgetId)
            } else {
                StatusBarWidgetFactory.EP_NAME.extensionList
                    .find { it.id == widgetId }
                    ?.let { factory ->
                        if (factory.isAvailable(project)) {
                            if (existingWidget != null) {
                                factory.disposeWidget(existingWidget)
                            }
                            factory.createWidget(project)
                        }
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
            if (title?.matches(".*\\d+.*mobtime.*".toRegex())== true){
                updateStatusBar("" +
                        "| "+driverName+" ["+lastTitleTime+"] |")
            }
            // If we see the ended state, handle it regardless of pause state
            if (title == "mobtime") {
                println("mobtime handleTimerEnded")
                handleTimerEnded(browser)
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

    private fun handleTimerEnded(browser: CefBrowser?) {
        if (currentState != TimerState.ENDED) {
            currentState = TimerState.ENDED
            ApplicationManager.getApplication().invokeLater {
                Utils.executeGenerateNextDriverName(browser)
                toolWindow.setIcon(AllIcons.Actions.ProfileRed)
                playPauseToolWindow?.setIcon(AllIcons.Actions.Resume)
                if(isMacOS()){
                    showMacOSNotification("Mob Timer", "The mob timer is up!")
                }else{
                    Utils.triggerNotification("The mob timer is up!")
                }
            }
        }
    }

    private fun showMacOSNotification(title: String, message: String) {
        val command = arrayOf("osascript", "-e", "display notification \"$message\" with title \"$title\" sound name \"Glass\"")
        Runtime.getRuntime().exec(command)
    }
    private fun isMacOS(): Boolean {
        return System.getProperty("os.name").contains("Mac", ignoreCase = true)
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
