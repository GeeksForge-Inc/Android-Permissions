# Android-Permissions

Dependency:
-----------

**Gradle (MavenCentral)**
```
implementation 'io.github.geeksforge-inc:androidpermissions:1.0.0'
```

Usage:
------

First declare your permissions in the manifest.
Example:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

**Customized permissions request:**
   1. create permissions options
```kotlin
 val settingsDialogData = DialogData(
            getString(R.string.permission_needed),
            getString(R.string.permission_denied_permanently_message),
            getString(R.string.not_now),
            getString(R.string.settings)
        )
        val rationaleDialogData = DialogData(
            getString(R.string.permission_needed),
            getString(R.string.permission_denied_once_message),
            getString(R.string.later),
            getString(R.string.allow_now)
        )
        val permissionOptions = DialogPermissionOptions(
            rationaleDialogData = rationaleDialogData,
            settingDialogData = settingsDialogData
        )
```
2. create permissionhandler by passing permission options
```kotlin
  val permissionHandler = PermissionHandler(this, this.packageName, permissionOptions) {
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_LONG).show()
        }
```
4. Attach permission handler to the lifecycle of activity or fragment
```kotlin
 lifecycle.addObserver(permissionHandler)
```
5. All set! Call request permissions method from permissions handler
```kotlin
      binding.button.setOnClickListener {
            permissionHandler.requestPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
```


License
-------

   Copyright 2021 Nirmal Jeffrey

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
