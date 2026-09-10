package cc.shinemoon.datum.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cc.shinemoon.datum.ui.MetricStatusChip
import cc.shinemoon.datumabase.model.preset.PresetEvaluation
import cc.shinemoon.datumabase.model.preset.PresetModel
import cc.shinemoon.datumabase.model.utility.MetricStatus
import cc.shinemoon.datum.model.occt.BoundingBox
import cc.shinemoon.datum.model.occt.EdgeRecord
import cc.shinemoon.datum.model.occt.FaceRecord
import cc.shinemoon.datum.model.occt.MassProperties
import cc.shinemoon.datum.model.occt.ModelPlacement
import cc.shinemoon.datum.model.occt.OcctInspectionData
import cc.shinemoon.datum.model.occt.ToleranceStatistics
import cc.shinemoon.datum.model.occt.TopologicalState
import cc.shinemoon.datum.model.occt.TopologyCounts
import compose.icons.FeatherIcons
import compose.icons.feathericons.AlertTriangle
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Box
import compose.icons.feathericons.Check
import compose.icons.feathericons.CheckCircle
import compose.icons.feathericons.Hexagon
import compose.icons.feathericons.Layers
import compose.icons.feathericons.Maximize
import compose.icons.feathericons.MinusCircle
import compose.icons.feathericons.Move
import compose.icons.feathericons.Package
import compose.icons.feathericons.Plus
import compose.icons.feathericons.Target
import compose.icons.feathericons.X
import compose.icons.feathericons.XCircle

interface InspectionInterface {
    fun onClear()
    fun onToggleSidebar()
    fun onPresetModified(preset: PresetModel)
    fun savedPresetList(): List<String>
    fun onLoadPreset(name: String)
    fun onDeletePreset(name: String)
}

@Composable
fun InspectionScreen(
    data: OcctInspectionData,
    evaluation: PresetEvaluation?,
    modifier: Modifier = Modifier,
    listener: InspectionInterface
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { listener.onClear() },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(FeatherIcons.ArrowLeft, contentDescription = "Back", modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("New File")
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = data.metadata.fileName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${data.metadata.originatingSystem} · ${data.metadata.timestamp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val savedPresetList = listener.savedPresetList()
            if (savedPresetList.isEmpty()) {
                Spacer(Modifier.weight(1f))
                PresetButton { listener.onToggleSidebar() }
            } else {
                var isExpanded by remember { mutableStateOf(false) }

                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuBox(
                    modifier = Modifier.weight(1f),
                    expanded = isExpanded,
                    onExpandedChange = { },
                ) {
                    Row {
                        Spacer(Modifier.weight(1f))
                        PresetButton { isExpanded = !isExpanded }
                    }

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false },
                    ) {
                        savedPresetList.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Text(text = item)
                                        IconButton(
                                            onClick = {

                                            }
                                        ) {

                                        }
                                    }
                                },
                                onClick = {
                                    listener.onLoadPreset(item)
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Row {
            reusableCard(
                FeatherIcons.Maximize
            ) {
                Column {
                    Text(
                        text = data.metadata.unitName.ifBlank { "UNKNOWN" },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Scale: ${String.format("%.4f", data.metadata.unitScaleToMm)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            data.mesh?.triangles?.let {
                if (it.isEmpty()) return@let
                reusableCard(
                    FeatherIcons.Maximize
                ) {
                    Column {
                        Text(
                            text = "Tessellation",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${it.size / 3} triangles · ${it.size / 3} vertices",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            ValidityStatusCard(data.topologicalState)
        }

        Spacer(Modifier.height(16.dp))
        BoundingBoxCard(data.boundingBox)
        Spacer(Modifier.height(16.dp))

        evaluation?.overall?.let { status ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                MetricStatusChip(status)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${evaluation.presetName} · ${evaluation.passedCount}/${evaluation.checks.size} checks passed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(8.dp))
        } ?: run {
            Text(
                text = "Objective facts extracted — no preset thresholds applied yet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // LEFT COLUMN: Aggregates
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TopologyCard(data.topology)
                PlacementCard(data.placement)
                MassPropertiesCard(data.massProperties)
            }

            // RIGHT COLUMN: Samples
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                EdgeSamplesCard(data.edges, evaluation)
                FaceSamplesCard(data.faces, evaluation)
                ToleranceCard(data.tolerances, evaluation)
            }
        }
    }
}

@Composable
fun PresetButton(
    listener: () -> Unit
) {
    OutlinedCard(
        shape = RoundedCornerShape(24.dp),
        onClick = (listener)
    ) {
        Row(
            Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = FeatherIcons.Plus,
                contentDescription = "Add preset",
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.size(8.dp))
            Text("Preset")
        }
    }
}

@Composable
fun ValidityStatusCard(state: TopologicalState) {
    val isValid = state.isBRepValid && state.faults.isEmpty()
    reusableCard(
        if (isValid) FeatherIcons.CheckCircle else FeatherIcons.XCircle,

    ) {
        Column {
            Text(
                text = if (isValid) "B-Rep Valid" else "B-Rep Invalid",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isValid) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = buildString {
                    append("Closed: ${if (state.isClosed) "Yes" else "No"}")
                    if (state.openBoundaryEdges > 0) append(" · Open Edges: ${state.openBoundaryEdges}")
                    if (state.nonManifoldEdges > 0) append(" · Non-Manifold: ${state.nonManifoldEdges}")
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // Add this inside ValidityStatusCard, below the existing Column:
        if (!isValid && state.faults.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
            Spacer(Modifier.height(8.dp))

            Text("Detected Faults:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(4.dp))

            // Show up to 3 faults to avoid flooding the UI
            state.faults.take(3).forEach { fault ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(FeatherIcons.AlertTriangle, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(6.dp))
                    Text(fault, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                }
                Spacer(Modifier.height(4.dp))
            }

            if (state.faults.size > 3) {
                Text("+ ${state.faults.size - 3} more faults", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun BoundingBoxCard(bbox: BoundingBox) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(FeatherIcons.Box, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("BOUNDING BOX", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))

            // Prominent Dimensions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                DimensionItem("X (Width)", bbox.dx)
                DimensionItem("Y (Depth)", bbox.dy)
                DimensionItem("Z (Height)", bbox.dz)
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))

            // Detailed Coordinates
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Min: (${String.format("%.4f", bbox.minX)}, ${String.format("%.4f", bbox.minY)}, ${String.format("%.4f", bbox.minZ)})", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Max: (${String.format("%.4f", bbox.maxX)}, ${String.format("%.4f", bbox.maxY)}, ${String.format("%.4f", bbox.maxZ)})", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Diagonal: ${String.format("%.2f", bbox.diagonal)} mm", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DimensionItem(label: String, value: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(String.format("%.2f", value), style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Text("mm", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun TopologyCard(topology: TopologyCounts) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(FeatherIcons.Layers, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("TOPOLOGY", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            MetricRow("Solids", topology.solids.toString())
            MetricRow("Shells", topology.shells.toString())
            MetricRow("Faces", topology.faces.toString())
            MetricRow("Edges", topology.edges.toString())
            MetricRow("Vertices", topology.vertices.toString())
            MetricRow("Compounds", topology.compounds.toString())
        }
    }
}

@Composable
fun PlacementCard(placement: ModelPlacement) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(FeatherIcons.Move, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("MODEL PLACEMENT", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))

            val (tx, ty, tz) = placement.translation
            MetricRow("Translation", "X: ${String.format("%.2f", tx)}, Y: ${String.format("%.2f", ty)}, Z: ${String.format("%.2f", tz)}")
            MetricRow("Scale Factor", String.format("%.4f", placement.scaleFactor))
        }
    }
}

@Composable
fun MassPropertiesCard(mass: MassProperties) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(FeatherIcons.Package, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("MASS PROPERTIES", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))

            MetricRow("Volume", String.format("%.2f mm³", mass.volume))
            MetricRow("Surface Area", String.format("%.2f mm²", mass.surfaceArea))
            MetricRow("Total Edge Length", String.format("%.2f mm", mass.totalEdgeLength))

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))

            mass.centerOfMass?.let {
                val (x, y, z) = it
                Text("Center of Mass", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text("X: ${String.format("%.4f", x)}   Y: ${String.format("%.4f", y)}   Z: ${String.format("%.4f", z)}", style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)
            } ?: Text("Center of Mass: Unavailable", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)

            mass.inertia?.let {
                Spacer(Modifier.height(12.dp))
                val (rx, ry, rz) = it.radiusOfGyration
                Text("Radius of Gyration", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text("X: ${String.format("%.4f", rx)}   Y: ${String.format("%.4f", ry)}   Z: ${String.format("%.4f", rz)}", style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun ToleranceCard(tolerances: ToleranceStatistics, evaluation: PresetEvaluation?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(FeatherIcons.Target, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("TOLERANCE STATISTICS", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))

            ToleranceRow("Vertex", tolerances.maxVertexTol, tolerances.avgVertexTol, evaluation?.checkStatus("tolerance.vertex"))
            ToleranceRow("Edge", tolerances.maxEdgeTol, tolerances.avgEdgeTol, evaluation?.checkStatus("tolerance.edge"))
            ToleranceRow("Face", tolerances.maxFaceTol, tolerances.avgFaceTol, evaluation?.checkStatus("tolerance.face"))
        }
    }
}

@Composable
fun EdgeSamplesCard(edges: List<EdgeRecord>, evaluation: PresetEvaluation?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(FeatherIcons.MinusCircle, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("PER-EDGE SAMPLES", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))

            // Show edges with a radius (fillets) first, then fallback to first 4 edges
            val samples = edges.filter { it.radius > 0.001 }.take(4)
                .ifEmpty { edges.take(4) }

            samples.forEach { edge ->
                val desc = if (edge.radius > 0.001) "fillet r=${String.format("%.2f", edge.radius)} mm"
                else "${edge.curveType.name.lowercase()} L=${String.format("%.2f", edge.length)} mm"

                SampleRow(
                    id = "edge_${edge.edgeId.toString().padStart(4, '0')}",
                    value = desc,
                    status = evaluation?.edgeStatuses?.get(edge.edgeId)
                )
            }
        }
    }
}

@Composable
fun FaceSamplesCard(faces: List<FaceRecord>, evaluation: PresetEvaluation?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(FeatherIcons.Hexagon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("PER-FACE SAMPLES", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))

            faces.take(5).forEach { face ->
                val desc = "${face.surfaceType.name.lowercase()} · ${String.format("%.1f", face.area)} mm²"

                SampleRow(
                    id = "face_${face.faceId.toString().padStart(4, '0')}",
                    value = desc,
                    subValue = "tol ${String.format("%.1e", face.tolerance)}",
                    status = evaluation?.faceStatuses?.get(face.faceId)
                )
            }
        }
    }
}

// ============================================================================
// REUSABLE ROW HELPERS
// ============================================================================

@Composable
private fun MetricRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ToleranceRow(type: String, maxTol: Double, avgTol: Double, status: MetricStatus?) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(type, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(horizontalAlignment = Alignment.End) {
                Text("Avg", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(String.format("%.2e", avgTol), style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Max", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        String.format("%.2e", maxTol),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = status?.let { if (it == MetricStatus.PASS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error } ?: MaterialTheme.colorScheme.onSurface
                    )
                    status?.let {
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = if (it == MetricStatus.PASS) FeatherIcons.Check else FeatherIcons.X,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (it == MetricStatus.PASS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SampleRow(id: String, value: String, subValue: String? = null, status: MetricStatus?) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(id, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = status?.let { if (it == MetricStatus.PASS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error } ?: MaterialTheme.colorScheme.onSurface
                )
                status?.let {
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = if (it == MetricStatus.PASS) FeatherIcons.Check else FeatherIcons.X,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (it == MetricStatus.PASS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
            if (subValue != null) {
                Text(
                    text = subValue,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun reusableCard(
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            content()
        }
    }
}