package cc.shinemoon.datum.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import compose.icons.FeatherIcons
import compose.icons.feathericons.Check
import compose.icons.feathericons.XCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cc.shinemoon.datumabase.model.utility.MetricStatus

@Composable
fun ReusableRowNumberField(
    title: String,
    value: Double?,
    unit: String = "",
    onValueChange: (Double?) -> Unit,
) {
    var text by remember(value) { mutableStateOf(value?.toString() ?: "") }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = text,
            onValueChange = { input ->
                text = input
                val parsed = input.toDoubleOrNull()
                onValueChange(if (input.isBlank()) null else parsed)
            },
            modifier = Modifier.width(120.dp).scale(.8f),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall,
            suffix = if (unit.isNotBlank()) {
                { Text(unit, style = MaterialTheme.typography.labelSmall) }
            } else null,
            isError = text.isNotBlank() && text.toDoubleOrNull() == null,
        )
    }
}

@Composable
fun MetricStatusChip(status: MetricStatus, modifier: Modifier = Modifier) {
    val passed = status == MetricStatus.PASS
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (passed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = if (passed) FeatherIcons.Check else FeatherIcons.XCircle,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = if (passed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = status.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (passed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}