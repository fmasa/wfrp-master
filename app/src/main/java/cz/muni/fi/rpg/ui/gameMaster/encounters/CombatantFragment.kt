package cz.muni.fi.rpg.ui.gameMaster.encounters

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.armour.Armor
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.model.domain.encounter.Combatant
import cz.muni.fi.rpg.model.domain.encounter.CombatantNotFound
import cz.muni.fi.rpg.model.domain.encounter.Wounds
import cz.muni.fi.rpg.ui.common.PartyScopedFragment
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.ui.views.TextInput
import cz.muni.fi.rpg.viewModels.EncounterDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class CombatantFragment : PartyScopedFragment(R.layout.fragment_combatant),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val args: CombatantFragmentArgs by navArgs()

    private val viewModel: EncounterDetailViewModel by viewModel { parametersOf(args.encounterId) }

    private lateinit var form: Form

    override fun getPartyId(): UUID = args.encounterId.partyId

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        setTitle(
            getString(
                if (args.combatantId == null)
                    R.string.title_combatant_add
                else R.string.title_combatant_edit
            )
        )

        val combatantId = args.combatantId

        if (combatantId == null) {
            form = initializeForm(null)
        } else {
            launch {
                try {
                    val combatant = viewModel.getCombatant(combatantId)
                    withContext(Dispatchers.Main) { form = initializeForm(combatant) }
                } catch (e: CombatantNotFound) {
                    withContext(Dispatchers.Main) {
                        toast(R.string.message_combatant_not_found, Toast.LENGTH_LONG)
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.form_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != R.id.actionSave) {
            return super.onOptionsItemSelected(item)
        }

        if (!form.validate()) {
            return false
        }

        item.isEnabled = false

        val name = requireView().findViewById<TextInput>(R.id.nameInput).getValue()
        val note = requireView().findViewById<TextInput>(R.id.noteInput).getValue()
        val maxWounds = requireView().findViewById<TextInput>(R.id.maxWoundsInput).getValue().toInt()
        val stats = buildStats()
        val armor = buildArmor()
        val enemy = requireView().findViewById<CheckBox>(R.id.enemyCheckbox).isChecked
        val alive = requireView().findViewById<CheckBox>(R.id.aliveCheckbox).isChecked

        val combatantId = args.combatantId

        launch {
            if (combatantId == null) {
                viewModel.addCombatant(
                    name,
                    note,
                    Wounds.fromMax(maxWounds),
                    stats,
                    armor,
                    enemy,
                    alive,
                    listOf(), // TODO: Add trappings and traits editation
                    listOf()
                )
            } else {
                viewModel.updateCombatant(
                    combatantId,
                    name,
                    note,
                    maxWounds,
                    stats,
                    armor,
                    enemy,
                    alive,
                    listOf(), // TODO: Add trappings and traits editation
                    listOf()
                )
            }

            withContext(Dispatchers.Main) {
                toast(R.string.message_combatant_saved)
                findNavController().popBackStack()
            }
        }

        return false
    }

    private fun buildStats(): Stats {
        return Stats(
            weaponSkill = requireView().findViewById<TextInput>(R.id.weaponSkillInput).getValue().toInt(),
            ballisticSkill = requireView().findViewById<TextInput>(R.id.ballisticSkillInput).getValue().toInt(),
            strength = requireView().findViewById<TextInput>(R.id.strengthInput).getValue().toInt(),
            agility = requireView().findViewById<TextInput>(R.id.agilityInput).getValue().toInt(),
            intelligence = requireView().findViewById<TextInput>(R.id.intelligenceInput).getValue().toInt(),
            initiative = requireView().findViewById<TextInput>(R.id.initiativeInput).getValue().toInt(),
            dexterity = requireView().findViewById<TextInput>(R.id.dexterityInput).getValue().toInt(),
            willPower = requireView().findViewById<TextInput>(R.id.willPowerInput).getValue().toInt(),
            fellowship = requireView().findViewById<TextInput>(R.id.fellowshipInput).getValue().toInt(),
            toughness = requireView().findViewById<TextInput>(R.id.toughnessInput).getValue().toInt()
        )
    }

    private fun buildArmor(): Armor {
        return Armor(
            head = requireView().findViewById<TextInput>(R.id.armorHead).getValue().toInt(),
            body = requireView().findViewById<TextInput>(R.id.armorBody).getValue().toInt(),
            shield = requireView().findViewById<TextInput>(R.id.armorShield).getValue().toInt(),
            leftArm = requireView().findViewById<TextInput>(R.id.armorLeftArm).getValue().toInt(),
            rightArm = requireView().findViewById<TextInput>(R.id.armorRightArm).getValue().toInt(),
            leftLeg = requireView().findViewById<TextInput>(R.id. armorLeftLeg).getValue().toInt(),
            rightLeg = requireView().findViewById<TextInput>(R.id.armorRightLeg).getValue().toInt()
        )
    }

    private fun initializeForm(combatant: Combatant?) = Form(requireContext()).apply {
        initializeBasics(this, combatant)
        initializeStats(this, combatant?.stats)
        initializeArmor(this, combatant?.armor)

        requireView().findViewById<View>(R.id.progress).toggleVisibility(false)
        requireView().findViewById<View>(R.id.mainView).toggleVisibility(true)
    }

    private fun initializeBasics(form: Form, combatant: Combatant?) {
        form.addTextInput(requireView().findViewById<TextInput>(R.id.nameInput)).apply {
            setMaxLength(Combatant.NAME_MAX_LENGTH, false)
            setNotBlank(getString(R.string.error_name_blank))
            setDefaultValue(combatant?.name ?: "")
        }

        form.addTextInput(requireView().findViewById<TextInput>(R.id.noteInput)).apply {
            setMaxLength(Combatant.NOTE_MAX_LENGTH, false)
            setDefaultValue(combatant?.note ?: "")
        }

        form.addTextInput(requireView().findViewById<TextInput>(R.id.maxWoundsInput)).apply {
            setMaxLength(2, false)
            setNotBlank(getString(R.string.error_required))
            addLiveRule(R.string.error_value_is_0) { it.toString().toInt() > 0 }
            setDefaultValue(combatant?.wounds?.max?.toString() ?: "")
        }

        requireView().findViewById<CheckBox>(R.id.enemyCheckbox).isChecked = combatant?.enemy ?: true
        requireView().findViewById<CheckBox>(R.id.aliveCheckbox).isChecked = combatant?.alive ?: true
    }

    private fun initializeStats(form: Form, stats: Stats?) {
        mapOf(
            requireView().findViewById<TextInput>(R.id.weaponSkillInput) to stats?.weaponSkill,
            requireView().findViewById<TextInput>(R.id.ballisticSkillInput) to stats?.ballisticSkill,
            requireView().findViewById<TextInput>(R.id.strengthInput) to stats?.strength,
            requireView().findViewById<TextInput>(R.id.toughnessInput) to stats?.toughness,
            requireView().findViewById<TextInput>(R.id.agilityInput) to stats?.agility,
            requireView().findViewById<TextInput>(R.id.intelligenceInput) to stats?.intelligence,
            requireView().findViewById<TextInput>(R.id.willPowerInput) to stats?.willPower,
            requireView().findViewById<TextInput>(R.id.fellowshipInput) to stats?.fellowship,
            requireView().findViewById<TextInput>(R.id.initiativeInput) to stats?.initiative,
            requireView().findViewById<TextInput>(R.id.dexterityInput) to stats?.dexterity
        ).forEach { (statInput, currentValue) ->
            form.addTextInput(statInput).apply {
                setShowErrorInEditText()
                setMaxLength(3, false)
                addLiveRule(R.string.error_required) { !it.isNullOrBlank() }
                addLiveRule(R.string.error_value_over_100) { it.toString().toInt() <= 100 }
                setDefaultValue((currentValue ?: 0).toString())
            }
        }
    }

    private fun initializeArmor(form: Form, armor: Armor?) {
        mapOf(
            requireView().findViewById<TextInput>(R.id.armorHead) to armor?.head,
            requireView().findViewById<TextInput>(R.id.armorShield) to armor?.shield,
            requireView().findViewById<TextInput>(R.id.armorBody) to armor?.body,
            requireView().findViewById<TextInput>(R.id.armorLeftArm) to armor?.leftArm,
            requireView().findViewById<TextInput>(R.id.armorRightArm) to armor?.rightArm,
            requireView().findViewById<TextInput>(R.id.armorLeftLeg) to armor?.leftLeg,
            requireView().findViewById<TextInput>(R.id.armorRightLeg) to armor?.rightLeg
        ).forEach { (armorInput, currentValue) ->
            form.addTextInput(armorInput).apply {
                setShowErrorInEditText()
                setMaxLength(2, false)
                addLiveRule(R.string.error_required) { !it.isNullOrBlank() }
                setDefaultValue((currentValue ?: 0).toString())
            }
        }
    }
}