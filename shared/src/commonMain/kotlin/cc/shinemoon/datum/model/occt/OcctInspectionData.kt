package cc.shinemoon.datum.model.occt

import cc.shinemoon.datum.types.occt.ShapeType


data class OcctInspectionData(
    val rootShapeType: ShapeType,
    val metadata: Metadata,
    val placement: ModelPlacement,
    val topology: TopologyCounts,
    val topologicalState: TopologicalState,
    val boundingBox: BoundingBox,
    val massProperties: MassProperties,
    val tolerances: ToleranceStatistics,
    val vertices: List<VertexRecord>,
    val edges: List<EdgeRecord>,
    val faces: List<FaceRecord>,
    val hierarchy: List<ShapeHierarchyNode>,
    val mesh: TriangleMesh?
)