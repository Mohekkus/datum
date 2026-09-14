package cc.shinemoon.datum.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import cc.shinemoon.datum.types.preset.MetricStatus
import compose.icons.feathericons.AlertTriangle

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

@Composable
fun TextInputDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Enter Information")
        },
        text = {
            Column {
                Text(text = "Please type your data below:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Input") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(textInput)
                },
                enabled = textInput.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

enum class PresetConfirmationAction {
    POSITIVE, NEGATIVE
}

data class PresetConfirmationModel(
    var title: String = "",
    var subtitle: String = "",
    var action: PresetConfirmationAction = PresetConfirmationAction.NEGATIVE,
    var actionButtonString: String = ""
)

@Composable
fun PresetConfirmationDialog(
    model: PresetConfirmationModel = PresetConfirmationModel(),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = FeatherIcons.AlertTriangle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp),
                )

                Text(
                    text = model.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    text = model.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColors(),
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                            when (model.action) {
                                PresetConfirmationAction.NEGATIVE -> MaterialTheme.colorScheme.error
                                PresetConfirmationAction.POSITIVE -> MaterialTheme.colorScheme.primary
                            },
                            contentColor =
                            when (model.action) {
                                PresetConfirmationAction.NEGATIVE -> MaterialTheme.colorScheme.onError
                                PresetConfirmationAction.POSITIVE -> MaterialTheme.colorScheme.onPrimaryContainer
                            },
                        ),
                    ) {
                        Text(model.actionButtonString)
                    }
                }
            }
        }
    }
}
