# AR-View from URL
# Add these to project AndroidManifest.xml file
**- Permissions:**
```ruby
<uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.CAMERA"/>

    <!--    these permissions are for AR view-->
    <uses-feature android:name="android.hardware.camera.ar" android:required="true"/>
    <uses-feature android:glEsVersion="0x00020000" android:required="true" />
```
**- Meta Data:**
```ruby
<meta-data
            android:name="com.google.ar.core"
            android:value="required" />
```
# Add these to your Build.gradle (Module level)
**- minSdk 24**
```ruby
 //Augmented Reality core
    implementation 'com.google.ar:core:1.35.0'

    //sceneform for Augmented Reality
    implementation 'com.google.ar.sceneform:core:1.17.1'
    implementation 'com.google.ar.sceneform.ux:sceneform-ux:1.17.1'
    implementation 'com.google.ar.sceneform:assets:1.17.1'
    implementation 'com.google.ar.sceneform:animation:1.17.1'
    implementation 'com.google.gms:google-services:4.3.15'
```
**- For testing purposes I'm adding my own URL from where we can test the 3d Model.**
```ruby
https://cybermart-prod.s3.me-south-1.amazonaws.com/chair_4.glb
```
**- Important Note**

If you find duplicate class error , you need to add this line in your Gradle.properties file

- android.enableJetifier=true
