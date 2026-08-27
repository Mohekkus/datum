package cc.shinemoon.datum.ui.launcher

import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cc.shinemoon.occt.OcctDllResolver
import cc.shinemoon.occt.OcctInspectionSession
import cc.shinemoon.occt.model.OcctInspectionData
import cc.shinemoon.occt.toStructuredModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.FilePlus
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path

interface OnDragListener {
    fun onStart()
    fun onEnded()
    fun onValidFile(file: File)
    fun onInvalidFile()
}

@Preview
@Composable
fun DropZone(
    onInspectionData: (OcctInspectionData) -> Unit = {},
) {
    Box(
        Modifier
            .padding(24.dp)
            .dashedBorder(
                8.dp,
                color = Color.Gray,
                cornerRadius = 24.dp,
                dashLength = 24.dp,
                gapLength = 10.dp,
            )
            .fillMaxSize()
            .dragAndDropTarget(
                // With "true" as the value of shouldStartDragAndDrop,
                // drag-and-drop operations are enabled unconditionally.
                shouldStartDragAndDrop = { true },
                target = operation(
                    object : OnDragListener {
                        override fun onStart() {
                            println("onStart")
                        }

                        override fun onEnded() {
                            println("onEnded")
                        }

                        override fun onValidFile(file: File) {
                            println("onValidFile")
                            OcctInspectionSession(OcctDllResolver.resolve()).use {
                                val path = Path.of(file.absolutePath)

                                if (Files.exists(path) && Files.isReadable(path)) {
                                    println("File is ready to be loaded.");
                                } else {
                                    System.err.println("File not found or cannot be read.");
                                }

                                val inspectionData = it.inspect(path).toStructuredModel()
                                onInspectionData(inspectionData)
                            }
                        }

                        override fun onInvalidFile() {
                            println("onInvalidFile")
                        }
                    }
                )
            )
    ) { boxContent() }
}

@Composable
private fun boxContent() =
    Column(
        Modifier.fillMaxSize().padding(62.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = FeatherIcons.FilePlus,
            contentDescription = null,
            Modifier.size(64.dp),
            tint = Color.LightGray
        )

        Spacer(Modifier.size(16.dp))

        Text(
            "Drop STEP File Here",
            fontSize = TextUnit(36f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif
        )

        Text(
            "or click to browse local storage",
            fontSize = TextUnit(14f, TextUnitType.Sp)
        )

        Text(
            "Maximum file size: 100MB",
            fontSize = TextUnit(14f, TextUnitType.Sp)
        )
    }

/**
 * The one non-bug worth flagging: you're still constructing a brand-new anonymous OnDragListener object inline at every call site in example(), so remember(listener) never actually skips the rebuild — the key changes identity every recomposition, so operation() reconstructs the DragAndDropTarget every time anyway. Harmless at this scale (drag targets aren't hot-path), but if example() recomposes frequently for unrelated reasons, you're doing pointless allocation. Not worth fixing unless you're chasing a real perf problem — just flagging so it's not a surprise later
 */
@Composable
fun operation(listener: OnDragListener): DragAndDropTarget {
    return remember(listener) {
        object : DragAndDropTarget {
            // Highlights the border of a potential drop target
            override fun onStarted(event: DragAndDropEvent) {
                listener.onStart()
            }

            override fun onEnded(event: DragAndDropEvent) {
                listener.onEnded()
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                val verdict = onDropOperation(event) {
                    listener.onValidFile(it)
                }
                if (!verdict) listener.onInvalidFile()
                return verdict
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun onDropOperation(
    event: DragAndDropEvent,
    onValidFile: (File) -> Unit
): Boolean {
    val dragdata = event.dragData()

    return if (dragdata is DragData.FilesList) {
        val file = dragdata.readFiles()
            .map { File(URI(it)) }
            .firstOrNull() ?: return false

        if (isValidStepHeader(file)) {
            onValidFile(file)
            true
        } else false
    } else false
}

private fun isValidStepHeader(file: File): Boolean {
    val head = file.inputStream().use { it.readNBytes(64) }
        .toString(Charsets.US_ASCII)
        .trimStart()
    return head.startsWith("ISO-10303-21")
}