package cz.muni.fi.rpg.partyList

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import cz.muni.fi.rpg.R
import kotlinx.android.synthetic.main.activity_party_list.*

class PartyListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Firebase.firestore;
        val auth = FirebaseAuth.getInstance();

        val user = auth.currentUser;

        if (user == null) {
            startActivity(Intent(this, PartyListActivity::class.java))

            Toast.makeText(applicationContext, "You have been logged out", Toast.LENGTH_LONG)
                .show()

            finish()

            return
        }

        setContentView(R.layout.activity_party_list)

        partyListRecycler.layoutManager = LinearLayoutManager(applicationContext);

        val adapter = PartyRecyclerAdapter(db, user.uid);
        adapter.startListening();
        partyListRecycler.adapter = adapter;
    }
}
