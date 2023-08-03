<p align="center">
 <img src="https://raw.githubusercontent.com/fmasa/wfrp-master/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="logo"/>
</p> 

<h1 align="center">WFRP Master</h1>

<p align="center">
 <a href="https://play.google.com/store/apps/details?id=cz.frantisekmasa.dnd" target="_blank">
  <img align="center" src="https://play.google.com/intl/en_gb/badges/static/images/badges/en_badge_web_generic.png" width="240"/></a>
 <br>
 <a href="https://ko-fi.com/fmasa" target="_blank">
  <img align="center" src="https://github.com/fmasa/wfrp-master/assets/5658260/063c58ef-3204-407c-8de2-adcb824da4ba" width="240"/></a>
</p>

## User documentation

For both players and GMs there is a [documentation](https://github.com/fmasa/wfrp-master/wiki) for various features and mechanics used in the app. 

## Development

Development is done using Android Studio 2021.2.1+.

This application heavily uses the Firebase Firestore. The best way to work on this project is to
 start your own Firestore emulator and use it instead of production database.

There are just a few steps:
1) [Install Firebase CLI](https://firebase.google.com/docs/cli#install_the_firebase_cli)
2) Start Firestore emulator using `firebase emulators:exec --only firestore`
3) Set `dev.firestoreEmulatorUrl` variable in local.properties to `<Host IP accessible from device>:8080`\*

---

\* You may need to expose port 8080 on your machine. Both your Android device and your host must be on same network,
or you need to use software such as [ngrok](https://ngrok.com/). 

## Attribution

This application is unofficial companion app and as such is not affiliated, associated, authorized,
endorsed by, or in any way way affiliated with GW or Cubicle7.

This app uses third-party content such as icons or sounds, I care about proper attribution
in these cases. If you think that your content is used in the app without attribution,
please [raise an Issue](https://github.com/fmasa/wfrp-master/issues/new), so I can fix it.
