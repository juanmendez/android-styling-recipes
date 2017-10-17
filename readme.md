# Day Night Theme
---------------------

Learning how to work with Night Day theme. This time the demo will user AndroidAnnotations.

* `MyApp` and `MainActivity` are flavored with [@AndroidAnnotations](https://github.com/androidannotations/androidannotations/)
* [We extracted our theme colors from this link](https://www.materialpalette.com/light-green/green)
* `colors.xml` are now flavored as `nonight`, `night` 
*  `MyApp`, when app starts we apply the last saved theme choice or default one through `AppCompatDelegate.setDefaultNightMode( MODE )`
* `SecondActivity` is our starting activity. Because it lacks any functionality we can make sure the choice made is in place.
* `MainActivity` we update the `radioButton` which matches the last or default theme choice.
    * when checking another `radioButton` we save `sharedPreference` in our `ThemePrefs`. [Read more about Android-Annotations SharedPreference](https://github.com/androidannotations/androidannotations/wiki/SharedPreferencesHelpers) 
    * We call `getDelegate().setLocalNightMode( MODE )`, this is the way to do updates from Activity level. The activity is recreated!
* Successfully the app can be closed and opened again, reflecting the changes thanks to `MyApp'
