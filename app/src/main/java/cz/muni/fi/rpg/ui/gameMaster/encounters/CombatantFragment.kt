package cz.muni.fi.rpg.ui.gameMaster.encounters

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
import cz.muni.fi.rpg.viewModels.EncounterDetailViewModel
import kotlinx.android.synthetic.main.fragment_combatant.*
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

        val name = nameInput.getValue()
        val note = noteInput.getValue()
        val maxWounds = maxWoundsInput.getValue().toInt()
        val stats = buildStats()
        val armor = buildArmor()
        val enemy = enemyCheckbox.isChecked
        val alive = aliveCheckbox.isChecked

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
            weaponSkill = weaponSkillInput.getValue().toInt(),
            ballisticSkill = ballisticSkillInput.getValue().toInt(),
            strength = strengthInput.getValue().toInt(),
            agility = agilityInput.getValue().toInt(),
            intelligence = intelligenceInput.getValue().toInt(),
            initiative = initiativeInput.getValue().toInt(),
            dexterity = dexterityInput.getValue().toInt(),
            willPower = willPowerInput.getValue().toInt(),
            fellowship = fellowshipInput.getValue().toInt(),
            toughness = toughnessInput.getValue().toInt()
        )
    }

    private fun buildArmor(): Armor {
        return Armor(
            head = armorHead.getValue().toInt(),
            body = armorBody.getValue().toInt(),
            shield = armorShield.getValue().toInt(),
            leftArm = armorLeftArm.getValue().toInt(),
            rightArm = armorRightArm.getValue().toInt(),
            leftLeg =  armorLeftLeg.getValue().toInt(),
            rightLeg = armorRightLeg.getValue().toInt()
        )
    }

    private fun initializeForm(combatant: Combatant?) = Form(requireContext()).apply {
        initializeBasics(this, combatant)
        initializeStats(this, combatant?.stats)
        initializeArmor(this, combatant?.armor)

        progress.toggleVisibility(false)
        mainView.toggleVisibility(true)
    }

    private fun initializeBasics(form: Form, combatant: Combatant?) {
        form.addTextInput(nameInput).apply {
            setMaxLength(Combatant.NAME_MAX_LENGTH, false)
            setNotBlank(getString(R.string.error_name_blank))
            setDefaultValue(combatant?.name ?: "")
        }

        form.addTextInput(noteInput).apply {
            setMaxLength(Combatant.NOTE_MAX_LENGTH, false)
            setDefaultValue(combatant?.note ?: "")
        }

        form.addTextInput(maxWoundsInput).apply {
            setMaxLength(2, false)
            setNotBlank(getString(R.string.error_required))
            addLiveRule(R.string.error_value_is_0) { it.toString().toInt() > 0 }
            setDefaultValue(combatant?.wounds?.max?.toString() ?: "")
        }

        enemyCheckbox.isChecked = combatant?.enemy ?: true
        aliveCheckbox.isChecked = combatant?.alive ?: true
    }

    private fun initializeStats(form: Form, stats: Stats?) {
        mapOf(
            weaponSkillInput to stats?.weaponSkill,
            ballisticSkillInput to stats?.ballisticSkill,
            strengthInput to stats?.strength,
            toughnessInput to stats?.toughness,
            agilityInput to stats?.agility,
            intelligenceInput to stats?.intelligence,
            willPowerInput to stats?.willPower,
            fellowshipInput to stats?.fellowship,
            initiativeInput to stats?.initiative,
            dexterityInput to stats?.dexterity
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
            armorHead to armor?.head,
            armorShield to armor?.shield,
            armorBody to armor?.body,
            armorLeftArm to armor?.leftArm,
            armorRightArm to armor?.rightArm,
            armorLeftLeg to armor?.leftLeg,
            armorRightLeg to armor?.rightLeg
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