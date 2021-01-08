package cz.frantisekmasa.wfrp_master.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.core.domain.Stats

@Composable
fun CharacteristicsTable(characteristics: Stats) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CharacteristicItem(Characteristic.WEAPON_SKILL, characteristics.weaponSkill)
            CharacteristicItem(Characteristic.AGILITY, characteristics.agility)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CharacteristicItem(Characteristic.BALLISTIC_SKILL, characteristics.ballisticSkill)
            CharacteristicItem(Characteristic.DEXTERITY, characteristics.dexterity)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CharacteristicItem(Characteristic.STRENGTH, characteristics.strength)
            CharacteristicItem(Characteristic.INTELLIGENCE, characteristics.intelligence)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CharacteristicItem(Characteristic.TOUGHNESS, characteristics.toughness)
            CharacteristicItem(Characteristic.WILL_POWER, characteristics.willPower)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CharacteristicItem(Characteristic.INITIATIVE, characteristics.initiative)
            CharacteristicItem(Characteristic.FELLOWSHIP, characteristics.fellowship)
        }
    }
}

@Composable
private fun CharacteristicItem(characteristic: Characteristic, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(characteristic.getShortcutName(), style = MaterialTheme.typography.subtitle1)
        Text(
            value.toString(),
            Modifier.padding(vertical = 12.dp),
            style = MaterialTheme.typography.h5
        )
    }
}
