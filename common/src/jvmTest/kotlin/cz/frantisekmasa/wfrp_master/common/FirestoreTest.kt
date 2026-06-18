package cz.frantisekmasa.wfrp_master.common

import android.app.Application
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.initialize
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test

class FirestoreTest {
    @Test
    fun test() = runTest {
        FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
            val storage = mutableMapOf<String, String>()
            override fun store(key: String, value: String) = storage.set(key, value)
            override fun retrieve(key: String) = storage[key]
            override fun clear(key: String) {
                storage.remove(key)
            }

            override fun log(msg: String) = println(msg)
            override fun getDatabasePath(name: String) = File("./build/$name")
        })

        val options = FirebaseOptions(
            projectId = "my-firebase-project",
            applicationId = "1:27992087142:android:ce3b6448250083d1",
            apiKey = "AIzaSyADUe90ULnQDuGShD9W23RDP0xmeDc6Mvw",
        )

        val app = Firebase.initialize(Application(), options)

        val auth = Firebase.auth(app).apply {
            useEmulator("localhost", 9099)
        }

        val firestore = Firebase.firestore(app).apply {
            useEmulator("localhost", 8080)
        }

        val user = Firebase.auth.signInAnonymously().user ?: error("Could not sign in")
    }
}