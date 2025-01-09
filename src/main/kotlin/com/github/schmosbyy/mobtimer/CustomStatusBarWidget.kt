package com.github.schmosbyy.mobtimer

import com.intellij.openapi.Disposable
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import java.awt.Component


class CustomStatusBarWidget() : StatusBarWidget, StatusBarWidget.TextPresentation, Disposable {
    private var text: String = "Driver: Null"
    override fun ID(): String = "CustomStatusBarWidget"

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun getText(): String = text

    override fun getTooltipText(): String = "Tooltip : Current Driver Name"

    override fun getAlignment(): Float = Component.CENTER_ALIGNMENT

    override fun install(statusBar: StatusBar) {
        // Perform any setup if needed
    }

    override fun dispose() {
        // Perform cleanup if necessary
    }
    fun updateText(newText: String) {
        text = newText
    }
}
