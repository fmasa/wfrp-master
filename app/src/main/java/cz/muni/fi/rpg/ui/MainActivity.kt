package cz.muni.fi.rpg.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.drawerlayout.widget.DrawerLayout
import com.github.zsoltk.compose.backpress.AmbientBackPressHandler
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.github.zsoltk.compose.router.BackStack
import com.github.zsoltk.compose.router.Router
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumImportScreen
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumScreen
import cz.frantisekmasa.wfrp_master.core.ui.buttons.AmbientHamburgerButtonHandler
import cz.frantisekmasa.wfrp_master.core.ui.shell.KoinScope
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.character.CharacterDetailScreen
import cz.muni.fi.rpg.ui.character.edit.CharacterEditScreen
import cz.muni.fi.rpg.ui.characterCreation.CharacterCreationScreen
import cz.muni.fi.rpg.ui.common.AboutScreen
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.ui.gameMaster.GameMasterScreen
import cz.muni.fi.rpg.ui.gameMaster.encounters.EncounterDetailScreen
import cz.muni.fi.rpg.ui.gameMaster.encounters.NpcCreationScreen
import cz.muni.fi.rpg.ui.gameMaster.encounters.NpcDetailScreen
import cz.muni.fi.rpg.ui.partyList.PartyListScreen
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.ui.common.composables.AmbientUser
import cz.muni.fi.rpg.ui.joinParty.InvitationScannerScreen
import cz.muni.fi.rpg.ui.settings.SettingsScreen
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.setupKoinFragmentFactory

class MainActivity : AuthenticatedActivity(R.layout.activity_main) {

    private val adManager: AdManager by inject()

    private val backPressHandler = BackPressHandler()

    private lateinit var backStack: BackStack<Route>

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()

        super.onCreate(savedInstanceState)

        adManager.initialize()

        findViewById<ComposeView>(R.id.compose).setContent {
            val auth: AuthenticationViewModel by viewModel()
            val user = auth.user.collectAsState(null).value ?: return@setContent

            Providers(
                AmbientBackPressHandler provides backPressHandler,
                AmbientHamburgerButtonHandler provides { openDrawer() },
                AmbientUser provides user
            ) {
                Theme {
                    Router<Route>(defaultRouting = Route.PartyList) { backStack ->
                        this.backStack = backStack
                        val route = backStack.last()

                        KoinScope(route) {
                            @Suppress("UNUSED_VARIABLE") // This val is there just to force `when` to be exhaustive
                            val nothing = when (route) {
                                is Route.PartyList -> {
                                    PartyListScreen(Routing(route, backStack))
                                }
                                is Route.GameMaster -> {
                                    GameMasterScreen(Routing(route, backStack), adManager)
                                }
                                is Route.About -> {
                                    AboutScreen(Routing(route, backStack))
                                }
                                is Route.CharacterCreation -> {
                                    CharacterCreationScreen(Routing(route, backStack))
                                }
                                is Route.CharacterDetail -> {
                                    CharacterDetailScreen(Routing(route, backStack), adManager)
                                }
                                is Route.CharacterEdit -> {
                                    CharacterEditScreen(Routing(route, backStack))
                                }
                                is Route.EncounterDetail -> {
                                    EncounterDetailScreen(Routing(route, backStack))
                                }
                                is Route.NpcDetail -> {
                                    NpcDetailScreen(Routing(route, backStack))
                                }
                                is Route.NpcCreation -> {
                                    NpcCreationScreen(Routing(route, backStack))
                                }
                                is Route.Settings -> {
                                    SettingsScreen(Routing(route, backStack))
                                }
                                is Route.Compendium -> {
                                    CompendiumScreen(Routing(route, backStack))
                                }
                                is Route.CompendiumImport -> {
                                    CompendiumImportScreen(Routing(route, backStack))
                                }
                                is Route.InvitationScanner -> {
                                    InvitationScannerScreen(Routing(route, backStack))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!backPressHandler.handle()) {
            super.onBackPressed()
        }
    }

    private fun openDrawer() {
        findViewById<DrawerLayout>(R.id.drawer_layout)?.open()
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

    @Suppress("UNUSED_PARAMETER")
    fun openAbout(item: MenuItem) {
        if (backStack.last() != Route.About) {
            backStack.push(Route.About)
        }

        findViewById<DrawerLayout>(R.id.drawer_layout).close()
    }

    @Suppress("UNUSED_PARAMETER")
    fun openSettings(item: MenuItem) {
        if (backStack.last() != Route.Settings) {
            backStack.push(Route.Settings)
        }

        findViewById<DrawerLayout>(R.id.drawer_layout).close()
    }
}