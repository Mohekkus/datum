package cc.shinemoon.datum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.shinemoon.datum.ui.launcher.DropZone
import cc.shinemoon.datum.ui.main.MainScreen
import cc.shinemoon.datum.viewmodel.OcctViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.X

@Composable
fun App() {
    MaterialTheme {
        val viewmodel = viewModel { OcctViewModel() }
        val state by viewmodel.uiState.collectAsState()
        var screen by remember { mutableStateOf<Screen>(Screen.DropZone) }

        LaunchedEffect(state.inspectionData) {
            if (state.inspectionData != null) screen = Screen.Results
        }

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            when (screen) {
                Screen.DropZone -> DropZone(viewmodel::inspect)

                Screen.Results -> state.inspectionData?.let { data ->
                    MainScreen(
                        data = data,
                        onClear = {
                            viewmodel.clearResults()
                            screen = Screen.DropZone
                        },
                    )
                }
            }
        }

        if (state.isLoading) {
            LoadingOverlay()
        }

        state.error?.let { message ->
            ErrorBanner(
                message = message,
                onDismiss = viewmodel::dismissError,
            )
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.65f))
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {},
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Inspecting model…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseSurface,
            )
        }
    }
}

@Composable
private fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Surface(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.errorContainer,
            tonalElevation = 6.dp,
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = message,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
                IconButton(onClick = onDismiss) {
                    Icon(FeatherIcons.X, contentDescription = "Dismiss")
                }
            }
        }
    }
}
