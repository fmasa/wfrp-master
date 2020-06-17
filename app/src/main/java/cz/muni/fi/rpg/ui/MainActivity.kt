package cz.muni.fi.rpg.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import cz.muni.fi.rpg.R
import org.koin.androidx.fragment.android.setupKoinFragmentFactory


class MainActivity : AuthenticatedActivity(R.layout.activity_main) {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()

        super.onCreate(savedInstanceState)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, _, _ ->
            supportActionBar?.subtitle = null
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_party_list, R.id.nav_character, R.id.nav_game_master),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Setup AdMob
        MobileAds.initialize(this) { }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @Suppress("UNUSED_PARAMETER")
    fun reportIssue(item: MenuItem) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "plain/text"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.issue_email_address)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.issue_email_subject))
        }
        startActivity(Intent.createChooser(intent, ""))
    }

    @Suppress("UNUSED_PARAMETER")
    fun rateApp(item: MenuItem) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(getString(R.string.store_listing_url))
            setPackage("com.android.vending")
        }

        startActivity(intent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun openPrivacyPolicy(item: MenuItem) {
        val urlString = getString(R.string.privacy_policy_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivity(intent)
    }
}