# Day-night theme widget mode
---------------------

Learning how to apply day/night theme to widgets

* It's always sunshine! Widgets never get the night theme assigned.
* `nightcolors.xml` attributes have 'night' as prefix instead of 'color'. Values are identical to `colors.xml` in night mode.
* `styles.xml` has a new theme `NightWidget` which makes use of `nightcolors.xml` attributes
* `MainActivity` uses an intent to notify WidgetProvider` for an update
* `WidgetProvider.isDayTime()` tells us what if the app is or would be using day theme. That info tells us what layout to inflate for each widget.
* This demo is set to update widgets every half an hour by looking at `xml/provider_widget.xml`. That should update to day/night theme when set to auto.