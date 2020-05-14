# D&D Master

## Development

This application heavily uses the Firebase Firestore. The best way to work on this project is to
 start your own Firestore emulator and use it instead of production database.

There are just a few steps:
1) [Install Firebase CLI](https://firebase.google.com/docs/cli#install_the_firebase_cli)
2) Start Firestore emulator using `firebase emulators:exec --only firestore`
3) Set `dev.firestoreEmulatorUrl` variable in local.properties to `<Host IP accessible from device>:8080`\*

---

\* You may need to expose port 8080 on your machine. Both your Android device and your host must be on same network,
or you need to use software such as [ngrok](https://ngrok.com/). 