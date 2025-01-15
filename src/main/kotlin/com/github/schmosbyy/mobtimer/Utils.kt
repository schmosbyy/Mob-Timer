package com.github.schmosbyy.mobtimer

import com.intellij.icons.AllIcons
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.jcef.JBCefBrowser
import org.cef.browser.CefBrowser
import java.awt.EventQueue
import java.util.*

object Utils {
    private val LOG = Logger.getInstance(Utils::class.java)

    fun triggerNotification(message: String, type: NotificationType = NotificationType.INFORMATION,duration: Long = 1800) {
        val notification = Notification(
            "ballonNotificationGroup",
            message,
            "",
            type
        )
        ApplicationManager.getApplication().invokeLater {
            Notifications.Bus.notify(notification)
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                ApplicationManager.getApplication().invokeLater {
                    notification.expire()
                }
            }
        }, duration)
    }

    fun getProgressCircle(toolWindow: ToolWindow) {
        try {
            val iconAnimated = AnimatedIcon(
                50,
                AllIcons.Process.FS.Step_1,
                AllIcons.Process.FS.Step_2,
                AllIcons.Process.FS.Step_3,
                AllIcons.Process.FS.Step_4,
                AllIcons.Process.FS.Step_5,
                AllIcons.Process.FS.Step_6,
                AllIcons.Process.FS.Step_7,
                AllIcons.Process.FS.Step_8,
                AllIcons.Process.FS.Step_9,
                AllIcons.Process.FS.Step_10,
                AllIcons.Process.FS.Step_11,
                AllIcons.Process.FS.Step_12,
                AllIcons.Process.FS.Step_13,
                AllIcons.Process.FS.Step_14,
                AllIcons.Process.FS.Step_15,
                AllIcons.Process.FS.Step_16,
                AllIcons.Process.FS.Step_17,
                AllIcons.Process.FS.Step_18
            )

            val scalableIcon = ScalableAnimatedIcon(iconAnimated)

            EventQueue.invokeLater {
                try {
                    toolWindow.setIcon(scalableIcon)
                } catch (e: Exception) {
                    LOG.error("Failed to set icon in EventQueue", e)
                    println("Failed to set icon in EventQueue: ${e.message}")
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            LOG.error("Error in getProgressCircle", e)
            println("Error in getProgressCircle: ${e.message}")
            e.printStackTrace()
        }
    }
    fun executeGenerateDriverName(browser: CefBrowser?) {
        val jsCode = """
            document.querySelectorAll('li.mb-2').forEach(item => {
            const divs = item.querySelectorAll('div');
            if (divs.length > 1 && divs[0].textContent.trim() === 'Driver') {
                const secondDivValue = divs[1].textContent.trim();
                console.log('Driver Name: ' + secondDivValue); // This will output the value of the second div (e.g., "s")
            }
        });
        """
        browser?.executeJavaScript(jsCode, browser.url, 0)
    }
    fun executeGenerateNextDriverName(browser: CefBrowser?) {
        val jsCode = """
        function getNextDriverName() {
            const processedValues = new Set();
            let nextDriver = '';
            const flexDivs = document.querySelectorAll('.flex.flex-row');
            for (const div of flexDivs) {
                const label = div.querySelector('div > div:nth-child(1)')?.textContent.trim().toLowerCase();
                const value = div.querySelector('div > div:nth-child(2)')?.textContent.trim().toLowerCase();
                if (!label || !value || processedValues.has(value)) {
                    continue; // Skip invalid or duplicate values
                }
                processedValues.add(value);
                if (label.includes('member')) {
                    nextDriver = value;
                    break; // Exit loop immediately after finding 'member'
                } else if (label.includes('navigator')) {
                    nextDriver = value; // Continue checking for 'member' if 'navigator' is found
                }
            }
            console.log('Next Driver: ', nextDriver);
        }
        getNextDriverName(); 
        """
        browser?.executeJavaScript(jsCode, browser.url, 0)
    }

    fun executeDisplayNone(browser: CefBrowser?) {
        val jsCode = """
                    const buttonsToHide = Array.from(document.querySelectorAll('button')).filter(button => {
            const text = button.textContent.trim();
            return text === 'Start' || text === 'Pause';
        });
        
        buttonsToHide.forEach(button => {
            button.style.display = 'none';
        });
        """
        browser?.executeJavaScript(jsCode, browser.url, 0)
    }
    fun executeisSkipTriggered(browser: CefBrowser?) {
        println("executeisSkipTriggered")
        val jsCode = """
        document.querySelectorAll('button').forEach(button => {
        const text = button.textContent.trim();
        if (text === 'Skip') {
            console.log('skipTriggered');
        }
        });
        """
        browser?.executeJavaScript(jsCode, browser.url, 0)
    }

    fun getBrowserInstance(toolWindow: ToolWindow?): JBCefBrowser? {
        val content = toolWindow?.contentManager?.getContent(0)
        return if (content?.component is JBCefBrowser.MyPanel) {
            (content.component as JBCefBrowser.MyPanel).jbCefBrowser
        } else {
            println("Component is not JBCefBrowser.MyPanel!")
            null
        }
    }

}
