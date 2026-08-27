package cc.shinemoon.datum.ui.main.rules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cc.shinemoon.datum.model.raw.MassPropertyRules
import cc.shinemoon.datum.ui.ReusableRowNumberField
import compose.icons.FeatherIcons
import compose.icons.feathericons.Package

@Composable
fun MassPropertyRulesScreen(
    rules: MassPropertyRules,
    onRulesChanged: (MassPropertyRules) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = !expanded })
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(FeatherIcons.Package, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text("Mass Properties", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        }

        if (expanded) {
            Spacer(Modifier.height(8.dp))
            ReusableRowNumberField("Min Volume", rules.minVolume, "mm³") { onRulesChanged(rules.copy(minVolume = it)) }
            ReusableRowNumberField("Max Volume", rules.maxVolume, "mm³") { onRulesChanged(rules.copy(maxVolume = it)) }
            ReusableRowNumberField("Min Surface Area", rules.minSurfaceArea, "mm²") { onRulesChanged(rules.copy(minSurfaceArea = it)) }
            ReusableRowNumberField("Max Surface Area", rules.maxSurfaceArea, "mm²") { onRulesChanged(rules.copy(maxSurfaceArea = it)) }
            ReusableRowNumberField("Min Total Edge Length", rules.minTotalEdgeLength, "mm") { onRulesChanged(rules.copy(minTotalEdgeLength = it)) }
            ReusableRowNumberField("Max Total Edge Length", rules.maxTotalEdgeLength, "mm") { onRulesChanged(rules.copy(maxTotalEdgeLength = it)) }
        }
    }
}