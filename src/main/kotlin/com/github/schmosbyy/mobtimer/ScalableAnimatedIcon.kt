package com.github.schmosbyy.mobtimer

import com.intellij.ui.AnimatedIcon
import com.intellij.openapi.util.ScalableIcon
import javax.swing.Icon
import java.awt.Graphics
import java.awt.Component

class ScalableAnimatedIcon(private val animatedIcon: AnimatedIcon) : ScalableIcon {

    // Default scale factor (1.0f means no scaling)
    private var scaleFactor: Float = 1.0f

    override fun getIconWidth(): Int = (animatedIcon.iconWidth * scaleFactor).toInt()

    override fun getIconHeight(): Int = (animatedIcon.iconHeight * scaleFactor).toInt()

    override fun getScale(): Float = scaleFactor

    override fun scale(scaleFactor: Float): Icon {
        this.scaleFactor = scaleFactor
        return this
    }

    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        animatedIcon.paintIcon(c, g, x, y)
    }
} 