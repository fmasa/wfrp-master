package cz.muni.fi.rpg.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.compose.*
import cz.frantisekmasa.wfrp_master.combat.ui.ActiveCombatScreen
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumImportScreen
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumScreen
import cz.frantisekmasa.wfrp_master.core.ui.buttons.AmbientHamburgerButtonHandler
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
import cz.frantisekmasa.wfrp_master.core.auth.AmbientUser
import cz.frantisekmasa.wfrp_master.core.ui.shell.AmbientSystemUiController
import cz.frantisekmasa.wfrp_master.core.ui.shell.rememberSystemUiController
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity
import cz.muni.fi.rpg.ui.joinParty.InvitationScannerScreen
import cz.muni.fi.rpg.ui.partySettings.PartySettingsScreen
import cz.muni.fi.rpg.ui.settings.SettingsScreen
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.setupKoinFragmentFactory

class MainActivity : AuthenticatedActivity(R.layout.activity_main) {

    private val adManager: AdManager by inject()

    private var navigateTo: (Route) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()

        super.onCreate(savedInstanceState)

        adManager.initialize()

        findViewById<ComposeView>(R.id.compose).setContent {
            val navController = rememberNavController()

            onCommit {
                navigateTo = {
                    navController.navigate(it.toString()) {
                        launchSingleTop = true
                    }
                }
            }

            onDispose {
                navigateTo = {}
            }

            val auth: AuthenticationViewModel by viewModel()
            val user = auth.user.collectAsState(null).value ?: return@setContent

            Providers(
                AmbientHamburgerButtonHandler provides { openDrawer() },
                AmbientUser provides user,
                AmbientSystemUiController provides rememberSystemUiController(window),
                AmbientActivity provides this
            ) {
                Theme {
                    NavHost(navController, startDestination = Route.PartyList.toString()) {
                        composable(Route.PartyList.toString()) {
                            PartyListScreen(Routing(Route.PartyList, navController))
                        }

                        composable(Route.GameMaster.toString()) {
                            GameMasterScreen(
                                Routing(Route.GameMaster.fromEntry(it), navController),
                                adManager
                            )
                        }

                        composable(Route.PartySettings.toString()) {
                            PartySettingsScreen(
                                Routing(
                                    Route.PartySettings.fromEntry(it),
                                    navController
                                )
                            )
                        }

                        composable(Route.About.toString()) {
                            AboutScreen(Routing(Route.About, navController))
                        }

                        composable(Route.CharacterCreation.toString()) {
                            CharacterCreationScreen(
                                Routing(
                                    Route.CharacterCreation.fromEntry(it),
                                    navController
                                )
                            )
                        }

                        composable(Route.CharacterDetail.toString()) {
                            CharacterDetailScreen(
                                Routing(Route.CharacterDetail.fromEntry(it), navController),
                                adManager,
                            )
                        }

                        composable(Route.CharacterEdit.toString()) {
                            CharacterEditScreen(
                                Routing(Route.CharacterEdit.fromEntry(it), navController)
                            )
                        }

                        composable(Route.EncounterDetail.toString()) {
                            EncounterDetailScreen(
                                Routing(Route.EncounterDetail.fromEntry(it), navController)
                            )
                        }

                        composable(Route.NpcDetail.toString()) {
                            NpcDetailScreen(Routing(Route.NpcDetail.fromEntry(it), navController))
                        }

                        composable(Route.NpcCreation.toString()) {
                            NpcCreationScreen(
                                Routing(Route.NpcCreation.fromEntry(it), navController),
                            )
                        }

                        composable(Route.Settings.toString()) {
                            SettingsScreen(Routing(Route.Settings, navController))
                        }

                        composable(Route.Compendium.toString()) {
                            CompendiumScreen(Routing(Route.Compendium.fromEntry(it), navController))
                        }

                        composable(Route.CompendiumImport.toString()) {
                            CompendiumImportScreen(
                                Routing(Route.CompendiumImport.fromEntry(it), navController)
                            )
                        }

                        composable(Route.InvitationScanner.toString()) {
                            InvitationScannerScreen(Routing(Route.InvitationScanner, navController))
                        }

                        composable(Route.ActiveCombat.toString()) {
                            ActiveCombatScreen(
                                Routing(Route.ActiveCombat.fromEntry(it), navController)
                            )
                        }
                    }
                }
            }
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
        navigateTo(Route.About)

        findViewById<DrawerLayout>(R.id.drawer_layout).close()
    }

    @Suppress("UNUSED_PARAMETER")
    fun openSettings(item: MenuItem) {
        navigateTo(Route.Settings)

        findViewById<DrawerLayout>(R.id.drawer_layout).close()
    }
}