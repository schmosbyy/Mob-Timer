package com.github.schmosbyy.mobtimer

import com.github.schmosbyy.mobtimer.settings.VehiklPluginSettings
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBuilder
import com.intellij.util.messages.MessageBusConnection
import javax.swing.JComponent

@Service(Service.Level.PROJECT)
class WebViewPanel(private val project: Project) : Disposable {
    private val settings = VehiklPluginSettings.getInstance(project)
    private var displayHandler: ChromiumDisplayHandler? = null
    private val browser: JBCefBrowser by lazy { createBrowser() }
    private val messageBusConnection: MessageBusConnection = project.messageBus.connect()
    private var toolWindow: ToolWindow? = null

    init {
        println("WebViewPanel init started")
        setupMessageBus()
        println("WebViewPanel init completed")
    }

    fun initialize(toolWindow: ToolWindow) {
        this.toolWindow = toolWindow
        setupBrowserHandlers(toolWindow)
        refreshWebView()
        Disposer.register(toolWindow.disposable, this)
    }

    private fun createBrowser(): JBCefBrowser {
        return JBCefBrowserBuilder()
            .setUrl("${settings.mobUrl}/${settings.timerName}")
            //.setWindowlessFramerate(10)
            .build()
    }

    fun reloadWebView(newUrl: String) {
        ApplicationManager.getApplication().invokeLater {
            browser.loadURL("$newUrl/${settings.timerName}")
        }
    }

    fun getContent(): JComponent = browser.component

    override fun dispose() {
        messageBusConnection.disconnect()
        browser.jbCefClient.dispose()
        browser.dispose()
    }

    private fun setupMessageBus() {
        messageBusConnection.subscribe(VehiklPluginSettings.SettingsChangeNotifier.TOPIC, object : VehiklPluginSettings.SettingsChangeNotifier {
            override fun settingsChanged() {
                refreshWebView()
            }
        })
    }

    private fun setupBrowserHandlers(toolWindow: ToolWindow) {
        displayHandler = ChromiumDisplayHandler(toolWindow, project)
        
        try {
            browser.jbCefClient.addDisplayHandler(displayHandler!!, browser.cefBrowser)
            browser.jbCefClient.addLoadHandler(displayHandler!!, browser.cefBrowser)
        } catch (e: Exception) {
            println("Failed to add handlers: ${e.message}")
            e.printStackTrace()
        }

        System.setProperty("jcef.force.gpu.acceleration", "true")
        System.setProperty("jcef.force.gpu", "true")
        System.setProperty("jcef.log.level", "ALL")

        Disposer.register(toolWindow.disposable, displayHandler!!)
    }

    private fun refreshWebView() {
        browser.loadURL("${settings.mobUrl}/${settings.timerName}")
    }
}
