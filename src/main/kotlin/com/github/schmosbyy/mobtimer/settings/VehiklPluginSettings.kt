package com.github.schmosbyy.mobtimer.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.messages.Topic
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "VehiklPluginSettings",
    storages = [Storage("vehiklPlugin.xml")]
)
class VehiklPluginSettings : PersistentStateComponent<VehiklPluginSettings> {
    private var _timerName: String = "default"
    var timerName: String
        get() = _timerName
        set(value) {
            if (_timerName != value) {
                _timerName = value
                project?.messageBus?.syncPublisher(SettingsChangeNotifier.TOPIC)?.settingsChanged()
            }
        }

    private var _mobUrl: String = "https://mobti.me" // Default URL
    var mobUrl: String
        get() = _mobUrl
        set(value) {
            if (_mobUrl != value) {
                _mobUrl = value
                project?.messageBus?.syncPublisher(SettingsChangeNotifier.TOPIC)?.settingsChanged()
            }
        }

    private var project: Project? = null

    override fun getState(): VehiklPluginSettings = this

    override fun loadState(state: VehiklPluginSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    interface SettingsChangeNotifier {
        fun settingsChanged()

        companion object {
            val TOPIC = Topic.create("VehiklPluginSettingsChanged", SettingsChangeNotifier::class.java)
        }
    }

    companion object {
        fun getInstance(project: Project): VehiklPluginSettings {
            val settings = project.getService(VehiklPluginSettings::class.java)
            settings.project = project
            return settings
        }
    }
} 