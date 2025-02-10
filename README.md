# Mob-Timer

[![Version](https://img.shields.io/jetbrains/plugin/v/26317.svg)](https://plugins.jetbrains.com/plugin/26317)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/26317.svg)](https://plugins.jetbrains.com/plugin/26317)

## Overview
The Mob-Timer plugin is designed to facilitate collaborative work sessions by providing a timer and status updates for teams. It integrates with the IntelliJ platform and provides a user-friendly interface for managing timers.

## Key Components
- **ChromiumDisplayHandler**: Manages the display of content in a tool window using a Chromium-based browser.
- **CustomStatusBarWidget**: Displays the current driver's name and time remaining in the status bar.
- **PlayPauseToolWindowFactory**: Creates the content for the tool window that allows users to control the timer.
- **ToggleMobTimerAction**: Toggles the visibility of the Mob Timer tool window.
- **ToggleMobTimerPauseAction**: Manages the pause and resume functionality of the timer.
- **ToolListener**: Listens for changes in the tool window state and manages the timer's state.
- **UpdateTimerName & UpdateTimerUrl**: Actions to update the timer's name and URL respectively.
- **WebViewPanel**: Manages the web view panel for displaying relevant content.
- **WebViewToolWindowFactory**: Creates the web view tool window content.

<!-- Plugin description -->
## Installation
- Using the IDE built-in plugin system:
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Mob-Timer"</kbd> > <kbd>Install</kbd>

- Using JetBrains Marketplace:
  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/26317) and install it by clicking the <kbd>Install to ...</kbd> button.

- Manually:
  Download the [latest release](https://github.com/schmosbyy/Mob-Timer/releases/latest) and install it manually using <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Usage Instructions
1. Open the Mob Timer Plugin Tool windows by clicking the Clock Icon with the play button.
2. Configure the Mob Timer by clicking "Your Session" -> "Show Member Form". This will allow you to add all members of your team.
3. Once you have added the team members and maybe the Goals for the day, click the Icon on the left sidebar for the "PlayPauseTimer" in the Tool Window.
4. This will allow the team to Mob out the goals and use the PlayPauseTimer to toggle the timer while mobbing.

## Important Instructions
- **Enabling Notifications**: To receive Mac OS system-wide notifications when the timer is up, ensure that notifications are enabled for the IntelliJ IDE in your system settings.
- **Enabling Notifications During Screen Sharing**: If you are using AnyDesk or Parsec for screen sharing, make sure to enable "Allow notifications when mirroring or sharing the display."
- **Keyboard Shortcuts**:
  - **Cmd + Shift + P**: Toggle the plugin to pause or unpause the timer.
  - **Cmd + Shift + J**: Toggle the tool window view to see the Mob Timer screen or not.
<!-- Plugin description end -->

## Acknowledgments
This plugin is inspired by and uses some implementations from the [mobtime-plugin](https://github.com/JStruk/mobtime-plugin) repository by Justin Struk.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---
Plugin based on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).