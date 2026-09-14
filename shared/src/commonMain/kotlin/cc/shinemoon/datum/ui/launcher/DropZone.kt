package cc.shinemoon.datum.ui.launcher

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.FilePlus
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.EventQueue
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import java.net.URI

private const val STEP_HEADER_PREFIX = "ISO-10303-21"
private const val MAX_FILE_SIZE_LABEL = "100 MB"
private const val ERROR_DISPLAY_DELAY_MILLIS = 4_000L
private val MAX_FILE_SIZE_BYTES = 100L * 1024 * 1024

@Composable
fun DropZone(
    onFileAccepted: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnFileAccepted by rememberUpdatedState(onFileAccepted)
    var isDragOver by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val errorDismissJob = remember { mutableStateOf<Job?>(null) }

    fun showError(message: String) {
        error = message
        errorDismissJob.value?.cancel()
        errorDismissJob.value = scope.launch {
            delay(ERROR_DISPLAY_DELAY_MILLIS)
            error = null
        }
    }

    fun accept(file: File) {
        val problem = validate(file)
        if (problem == null) currentOnFileAccepted(file) else showError(problem)
    }

    val filePicker = rememberFilePickerLauncher(
        type = FileKitType.File(
            extensions = setOf("step", "stp")
        )
    ) { file ->
        file?.file?.let(::accept)
    }

    val dragTarget = remember {
        object : DragAndDropTarget {
            override fun onStarted(event: DragAndDropEvent) {
                println("onStart")
                isDragOver = true
            }

            override fun onEnded(event: DragAndDropEvent) {
                isDragOver = false
            }

            @OptIn(ExperimentalComposeUiApi::class)
            override fun onDrop(event: DragAndDropEvent): Boolean {
                println("onDrop")
                isDragOver = false
                val file = droppedFile(event) ?: return false
                accept(file)
                return true
            }
        }
    }

    val borderColor by animateColorAsState(
        targetValue = if (isDragOver) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        label = "drop-zone-border-color",
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isDragOver) 3.dp else 2.dp,
        label = "drop-zone-border-width",
    )
    val contentScale by animateFloatAsState(
        targetValue = if (isDragOver) 1.04f else 1f,
        label = "drop-zone-content-scale",
    )

    Box(
        modifier = modifier
            .padding(24.dp)
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = if (isDragOver) 0.06f else 0.02f),
                shape = RoundedCornerShape(24.dp),
            )
            .dashedBorder(
                width = borderWidth,
                color = borderColor,
                cornerRadius = 24.dp,
                dashLength = 20.dp,
                gapLength = 12.dp,
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                filePicker.launch()
//                browseForStepFile { file -> file?.let(::accept) }
            }
            .dragAndDropTarget(
                shouldStartDragAndDrop = { true },
                target = dragTarget,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = contentScale
                    scaleY = contentScale
                }
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = FeatherIcons.FilePlus,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Drop STEP File Here",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "or click to browse your computer (.step / .stp)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Maximum file size: $MAX_FILE_SIZE_LABEL",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )

            error?.let { message ->
                Spacer(Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun droppedFile(event: DragAndDropEvent): File? {
    val paths = (event.dragData() as? DragData.FilesList)?.readFiles() ?: return null
    return paths.firstOrNull()?.let { path ->
        runCatching { File(URI(path)) }.getOrNull()
    }
}

private fun validate(file: File): String? = when {
    !file.exists() -> "That file no longer exists."
    file.length() > MAX_FILE_SIZE_BYTES -> "File exceeds the $MAX_FILE_SIZE_LABEL limit."
    !hasValidStepHeader(file) -> "Not a valid STEP file (missing ISO-10303-21 header)."
    else -> null
}

private fun hasValidStepHeader(file: File): Boolean = runCatching {
    file.inputStream().use { stream ->
        stream.readNBytes(64).toString(Charsets.US_ASCII).trimStart().startsWith(STEP_HEADER_PREFIX)
    }
}.getOrDefault(false)

private fun browseForStepFile(onSelected: (File?) -> Unit) {
    EventQueue.invokeLater {
        val dialog = FileDialog(null as Frame?, "Open STEP file", FileDialog.LOAD)
        dialog.filenameFilter = FilenameFilter { _, name ->
            name.endsWith(".step", ignoreCase = true) || name.endsWith(".stp", ignoreCase = true)
        }
        dialog.isVisible = true
        onSelected(dialog.files.singleOrNull())
    }
}

@Preview
@Composable
private fun DropZonePreview() {
    MaterialTheme {
        DropZone(onFileAccepted = {})
    }
}
