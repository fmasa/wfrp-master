package cz.muni.fi.rpg.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cz.frantisekmasa.wfrp_master.combat.ui.ActiveCombatScreen
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumImportScreen
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumScreen
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.ui.character.CharacterDetailScreen
import cz.muni.fi.rpg.ui.character.edit.CharacterEditScreen
import cz.muni.fi.rpg.ui.characterCreation.CharacterCreationScreen
import cz.muni.fi.rpg.ui.common.AboutScreen
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.ui.gameMaster.GameMasterScreen
import cz.muni.fi.rpg.ui.gameMaster.encounters.EncounterDetailScreen
import cz.muni.fi.rpg.ui.gameMaster.encounters.NpcCreationScreen
import cz.muni.fi.rpg.ui.gameMaster.encounters.NpcDetailScreen
import cz.muni.fi.rpg.ui.joinParty.InvitationLinkScreen
import cz.muni.fi.rpg.ui.joinParty.InvitationScannerScreen
import cz.muni.fi.rpg.ui.partyList.PartyListScreen
import cz.muni.fi.rpg.ui.partySettings.PartySettingsScreen
import cz.muni.fi.rpg.ui.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController, adManager: AdManager) {
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

        composable(
            Route.InvitationLink.toString(),
            deepLinks = Route.InvitationLink.deepLinks(),
        ) {
            InvitationLinkScreen(Routing(Route.InvitationLink.fromEntry(it), navController))
        }

        composable(Route.ActiveCombat.toString()) {
            ActiveCombatScreen(
                Routing(Route.ActiveCombat.fromEntry(it), navController)
            )
        }
    }
}