package com.github.schmosbyy.mobtimer

import com.intellij.openapi.Disposable
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import java.awt.Component

class CustomStatusBarWidget : StatusBarWidget, StatusBarWidget.TextPresentation, Disposable {
    private var text: String = "Driver: "
    private var statusBar: StatusBar? = null

    override fun ID(): String = "CustomStatusBarWidget"

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun getText(): String = text

    override fun getTooltipText(): String = "Current Driver Name and Time Remaining"

    override fun getAlignment(): Float = Component.CENTER_ALIGNMENT

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
    }

    override fun dispose() {
        statusBar = null // Clear reference to avoid memory leaks
    }

    fun updateText(newText: String) {
        text = newText
        statusBar?.updateWidget(ID()) // Notify the status bar to refresh the widget
    }
}