package cz.muni.fi.rpg.ui

import android.os.Bundle
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.platform.setContent
import androidx.navigation.compose.*
import cz.frantisekmasa.wfrp_master.combat.ui.ActiveCombatScreen
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumImportScreen
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumScreen
import cz.muni.fi.rpg.ui.character.CharacterDetailScreen
import cz.muni.fi.rpg.ui.character.edit.CharacterEditScreen
import cz.muni.fi.rpg.ui.characterCreation.CharacterCreationScreen
import cz.muni.fi.rpg.ui.common.AboutScreen
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.ui.gameMaster.GameMasterScreen
import cz.muni.fi.rpg.ui.gameMaster.encounters.EncounterDetailScreen
import cz.muni.fi.rpg.ui.gameMaster.encounters.NpcCreationScreen
import cz.muni.fi.rpg.ui.gameMaster.encounters.NpcDetailScreen
import cz.muni.fi.rpg.ui.partyList.PartyListScreen
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.frantisekmasa.wfrp_master.core.auth.AmbientUser
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity
import cz.muni.fi.rpg.ui.joinParty.InvitationScannerScreen
import cz.muni.fi.rpg.ui.partySettings.PartySettingsScreen
import cz.muni.fi.rpg.ui.settings.SettingsScreen
import cz.muni.fi.rpg.ui.shell.AmbientOnBackPressedDispatcher
import cz.muni.fi.rpg.ui.shell.AmbientSystemUiController
import cz.muni.fi.rpg.ui.shell.DrawerShell
import cz.muni.fi.rpg.ui.shell.rememberSystemUiController
import cz.muni.fi.rpg.viewModels.provideAuthenticationViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.setupKoinFragmentFactory

class MainActivity : AuthenticatedActivity() {

    private val adManager: AdManager by inject()

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()

        super.onCreate(savedInstanceState)

        adManager.initialize()

        setContent {
            Providers(
                AmbientActivity provides this,
                AmbientSystemUiController provides rememberSystemUiController(window),
                AmbientOnBackPressedDispatcher provides onBackPressedDispatcher
            ) {
                val navController = rememberNavController()
                val lifecycleOwner = AmbientLifecycleOwner.current
                val onBackPressedDispatcher = AmbientOnBackPressedDispatcher.current

                onCommit(navController, lifecycleOwner, onBackPressedDispatcher) {
                    navController.setLifecycleOwner(lifecycleOwner)
                    navController.setOnBackPressedDispatcher(onBackPressedDispatcher)
                }

                val auth = provideAuthenticationViewModel()
                val user = auth.user.collectAsState(null).value ?: return@Providers

                Providers(AmbientUser provides user) {
                    DrawerShell(navController) {
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
                                NpcDetailScreen(
                                    Routing(
                                        Route.NpcDetail.fromEntry(it),
                                        navController
                                    )
                                )
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
                                CompendiumScreen(
                                    Routing(
                                        Route.Compendium.fromEntry(it),
                                        navController
                                    )
                                )
                            }

                            composable(Route.CompendiumImport.toString()) {
                                CompendiumImportScreen(
                                    Routing(Route.CompendiumImport.fromEntry(it), navController)
                                )
                            }

                            composable(Route.InvitationScanner.toString()) {
                                InvitationScannerScreen(
                                    Routing(
                                        Route.InvitationScanner,
                                        navController
                                    )
                                )
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
    }
}