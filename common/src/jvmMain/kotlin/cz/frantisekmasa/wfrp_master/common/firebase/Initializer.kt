package cz.frantisekmasa.wfrp_master.common.firebase

import android.app.Application
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import io.github.aakira.napier.Napier
import java.util.prefs.Preferences

fun initializeFirebase() {
    FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
        val storage = Preferences.userRoot().node("wfrp-master").node("firebase")
        override fun store(key: String, value: String) = storage.put(key, value)
        override fun retrieve(key: String) = storage.get(key, null)
        override fun clear(key: String) { storage.remove(key) }
        override fun log(msg: String) = Napier.d(msg)
    })

    Firebase.initialize(
        Application(),
        FirebaseOptions(
            apiKey = "AIzaSyDO4Y4wWcY4HdYcsp8zcLMpMjwUJ_9q3Fw",
            projectId = "dnd-master-58fca",
            applicationId = "dnd-master-58fca",
        )
    )
}
