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
   permissionHandler = PermissionHandler(this, this.packageName, permissionOptions) {
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
