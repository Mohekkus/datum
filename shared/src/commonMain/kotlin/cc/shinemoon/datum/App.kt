package cc.shinemoon.datum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.shinemoon.datum.ui.launcher.DropZone
import cc.shinemoon.datum.ui.preview.InspectionScreen
import cc.shinemoon.datum.uistate.AppScreenStatus
import cc.shinemoon.datum.viewmodel.OcctViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        var screen by remember { mutableStateOf<Screen>(Screen.DropZone) }
        val viewmodel = viewModel { OcctViewModel() }

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .fillMaxSize()
        ) {
            when (screen) {
                is Screen.DropZone -> DropZone {
                    viewmodel.addInspectionData(it)
                    screen = Screen.Results
                }
                is Screen.Results -> {
                    viewmodel.getInspectionData()?.let {
                        InspectionScreen(
                            it
                        ) {
                           viewmodel.purgeInspectionData()
                           screen = Screen.DropZone
                        }
                    }
                }
            }

        }


        val state = viewmodel.uiState.collectAsState()
        if (state.value.screenStatus != AppScreenStatus.IDLE) {
            Box(
                Modifier
                    .background(Color.DarkGray.copy(alpha = 0.65f))
                    .fillMaxSize()
                ,
                contentAlignment = Alignment.Center
            ) {
                when (state.value.screenStatus) {
                    AppScreenStatus.LOADING ->
                        CircularProgressIndicator()
                    else -> {}
                }
            }
        }
    }
}