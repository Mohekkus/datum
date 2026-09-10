package cc.shinemoon.datum.ui.main.rules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
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
import cc.shinemoon.datum.types.occt.CurveType
import cc.shinemoon.datum.types.occt.SurfaceType
import cc.shinemoon.datum.ui.MetricStatusChip
import cc.shinemoon.datumabase.model.data.GeometryRules
import cc.shinemoon.datumabase.model.preset.PresetEvaluation
import cc.shinemoon.datumabase.model.utility.RuleGroup
import compose.icons.FeatherIcons
import compose.icons.feathericons.Hexagon

@Composable
fun GeometryRulesScreen(
    rules: GeometryRules,
    onRulesChanged: (GeometryRules) -> Unit,
    evaluation: PresetEvaluation? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = !expanded })
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(FeatherIcons.Hexagon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text("Geometry", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            evaluation?.groupStatus(RuleGroup.GEOMETRY)?.let {
                MetricStatusChip(it)
            }
        }

        if (expanded) {
            Spacer(Modifier.height(8.dp))

            TypeRestrictionRow(
                label = "Restrict Surface Types",
                allEntries = SurfaceType.entries.map { it.name },
                selected = rules.allowedSurfaceTypes,
                onSelectedChanged = { onRulesChanged(rules.copy(allowedSurfaceTypes = it)) }
            )
            Spacer(Modifier.height(8.dp))
            TypeRestrictionRow(
                label = "Restrict Curve Types",
                allEntries = CurveType.entries.map { it.name },
                selected = rules.allowedCurveTypes,
                onSelectedChanged = { onRulesChanged(rules.copy(allowedCurveTypes = it)) }
            )
        }
    }
}

@Composable
private fun TypeRestrictionRow(
    label: String,
    allEntries: List<String>,
    selected: List<String>?,
    onSelectedChanged: (List<String>?) -> Unit,
) {
    Column(Modifier.padding(horizontal = 8.dp)) {
        ReusableRowSwitch(
            title = label,
            checked = selected != null,
        ) { enabled ->
            onSelectedChanged(if (enabled) allEntries else null)
        }

        if (selected != null) {
            Spacer(Modifier.height(4.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                allEntries.forEach { entry ->
                    FilterChip(
                        selected = entry in selected,
                        onClick = {
                            val updated = if (entry in selected) selected - entry else selected + entry
                            onSelectedChanged(updated)
                        },
                        label = { Text(entry, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    }
}