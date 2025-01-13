package com.github.schmosbyy.mobtimer

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class CustomStatusBarWidgetFactory : StatusBarWidgetFactory {
    companion object {
        const val WIDGET_ID = "CustomStatusBarWidget"
    }
    override fun getId(): String = WIDGET_ID


    override fun getDisplayName(): String = "Custom Status Bar Widget"

    override fun isAvailable(project: Project): Boolean = true

    override fun createWidget(project: Project): StatusBarWidget {
        return CustomStatusBarWidget()
    }

    override fun disposeWidget(widget: StatusBarWidget) {
        widget.dispose()
    }

    override fun canBeEnabledOn(statusBar: com.intellij.openapi.wm.StatusBar): Boolean = true

}