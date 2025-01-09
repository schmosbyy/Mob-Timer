package com.github.schmosbyy.mobtimer


object TimerStateManager {
    @Volatile
    var isTimerPaused: Boolean = true
    @Volatile
    var isToggleTriggered: Boolean = false
}