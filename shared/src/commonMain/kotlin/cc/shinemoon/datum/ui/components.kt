package cc.shinemoon.datum.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

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