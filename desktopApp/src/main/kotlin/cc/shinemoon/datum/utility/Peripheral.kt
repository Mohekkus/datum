package cc.shinemoon.datum.utility

import model.RawOcctModel
import cc.shinemoon.datum.model.occt.BoundingBox
import cc.shinemoon.datum.model.occt.EdgeRecord
import cc.shinemoon.datum.model.occt.FaceRecord
import cc.shinemoon.datum.model.occt.InertiaTensor
import cc.shinemoon.datum.model.occt.MassProperties
import cc.shinemoon.datum.model.occt.Metadata
import cc.shinemoon.datum.model.occt.ModelPlacement
import cc.shinemoon.datum.model.occt.OcctInspectionData
import cc.shinemoon.datum.model.occt.ShapeHierarchyNode
import cc.shinemoon.datum.model.occt.ToleranceStatistics
import cc.shinemoon.datum.model.occt.TopologicalState
import cc.shinemoon.datum.model.occt.TopologyCounts
import cc.shinemoon.datum.model.occt.TriangleMesh
import cc.shinemoon.datum.model.occt.VertexRecord
import cc.shinemoon.datum.types.occt.CurveType
import cc.shinemoon.datum.types.occt.ShapeType
import cc.shinemoon.datum.types.occt.SurfaceType

// Alias for semantic clarity
typealias OcctModel = OcctInspectionData

fun RawOcctModel.toStructuredModel(): OcctModel =
    OcctModel(
        ShapeType.fromValue(
            shapeType
        ),
        stepMetadata.toMetadata(
            unitName, unitScaleToMm
        ),
        this.placement.toModelPlacement(),
        this.topologyCounts.toTopologyCount(),
        TopologicalState(
            isBRepValid = shapeValid,
            faults = shapeFaults.toList(),
            isClosed = shapeClosed,
            openBoundaryEdges = manifoldStats[0],
            nonManifoldEdges = manifoldStats[1],
        ),
        this.boundingBox.toBoundingBox(),
        toMassProperties(
            volume, surfaceArea, totalEdgeLength, centerOfMass?.toCenterOfMass(), inertiaProperties?.toInertiaTensor()
        ),
        tolerances = tolerances.toToleranceStatistics(),
        vertexRecords.toListOfVertexRecord(),
        edgeRecords.toListOfEdgeRecord(
            faceBoundaryEdgeMap.toPerEdgeData()
        ),
        faceRecords.toFaceRecord(
            faceBoundaryEdgeMap.toPerFaceData()
        ),
        shapeTreeRecords.toShapeHierarchy(),
        toTessellationMesh(
            meshVertices, meshNormals, meshTriangles, meshTriangleFaceIds
        )
    )

private fun Array<String>.toMetadata(
    unitName: String,
    unitScale: Double
): Metadata =
    Metadata(
        schema = getOrElse(0) { "" },
        fileName = getOrElse(1) { "" }
            .substringAfterLast('/')
            .substringBeforeLast('.'),
        timestamp = getOrElse(2) { "" },
        author = getOrElse(3) { "" },
        organization = getOrElse(4) { "" },
        preprocessor = getOrElse(5) { "" },
        originatingSystem = getOrElse(6) { "" },
        authorization = getOrElse(7) { "" },
        unitName = unitName,
        unitScaleToMm = unitScale
    )

private fun DoubleArray.toModelPlacement(): ModelPlacement =
    ModelPlacement(
        translation = Triple(this[0], this[1], this[2]),
        scaleFactor = this[3],
        rotationMatrix = sliceArray(4..12)
    )

private fun IntArray.toTopologyCount(): TopologyCounts =
    TopologyCounts(
        solids = get(0),
        shells = get(1),
        faces = get(2),
        wires = get(3),
        edges = get(4),
        vertices = get(5),
        compounds = get(6)
    )

private fun DoubleArray.toBoundingBox(): BoundingBox = BoundingBox(
    get(0), get(1), get(2), get(3), get(4), get(5),
)

private fun DoubleArray.toCenterOfMass(): Triple<Double, Double, Double> =
    Triple(get(0), get(1), get(2))

private fun DoubleArray.toInertiaTensor(): InertiaTensor =
    InertiaTensor(
        ixx = get(0),
        iyy = get(1),
        izz = get(2),
        ixy = get(3),
        ixz = get(4),
        iyz = get(5),
        principalMoments = Triple(get(6), get(7), get(8)),
        principalAxes = listOf(
            Triple(get(9), get(10), get(11)),
            Triple(get(12), get(13), get(14)),
            Triple(get(15), get(16), get(17)),
        ),
        radiusOfGyration = Triple(get(18), get(19), get(20)),
    )

private fun toMassProperties(
    volume: Double,
    surfaceArea: Double,
    totalEdgeLength: Double,
    centerOfMass: Triple<Double, Double, Double>?,
    inertiaTensor: InertiaTensor?,

    ): MassProperties =
    MassProperties(
        volume = volume,
        surfaceArea = surfaceArea,
        totalEdgeLength = totalEdgeLength,
        centerOfMass = centerOfMass,
        inertia = inertiaTensor
    )

private fun DoubleArray.toToleranceStatistics(): ToleranceStatistics =
    ToleranceStatistics(
        minVertexTol = get(0),
        maxVertexTol = get(1),
        avgVertexTol = get(2),
        minEdgeTol = get(3),
        maxEdgeTol = get(4),
        avgEdgeTol = get(5),
        minFaceTol = get(6),
        maxFaceTol = get(7),
        avgFaceTol = get(8)
    )

private fun DoubleArray.toListOfVertexRecord(): List<VertexRecord> {
    val vertexList = mutableListOf<VertexRecord>()
    val vStride = 5
    for (i in 0 until (this.size / vStride)) {
        val offset = i * vStride
        vertexList.add(
            VertexRecord(
                vertexId = this[offset].toInt(),
                x = this[offset + 1],
                y = this[offset + 2],
                z = this[offset + 3],
                tolerance = this[offset + 4],
            )
        )
    }
    return vertexList
}

private fun IntArray.toPerEdgeData(): Map<Int, List<Int>> {
    val map = mutableMapOf<Int, List<Int>>()
    var cursor = 0
    while (cursor < size) {
        val fId = this[cursor++]
        val count = this[cursor++]
        repeat(count) {
            val eId = this[cursor++]
            map[eId] = (map[eId].orEmpty() + fId)
        }
    }
    return map
}

private fun DoubleArray.toListOfEdgeRecord(
    faceBoundaryEdgeMap: Map<Int, List<Int>>
): List<EdgeRecord> {
    val edgeList = mutableListOf<EdgeRecord>()
    val eStride = 19
    for (i in 0 until (this.size / eStride)) {
        val offset = i * eStride
        val eId = this[offset].toInt()
        edgeList.add(
            EdgeRecord(
                edgeId = eId,
                curveType = CurveType.fromValue(this[offset + 1].toInt()),
                length = this[offset + 2],
                tolerance = this[offset + 3],
                startVertexId = this[offset + 4].toInt(),
                endVertexId = this[offset + 5].toInt(),
                isDegenerated = this[offset + 6] > 0.5,
                isOrientationReversed = this[offset + 7] > 0.5,
                adjacentFaceIds = faceBoundaryEdgeMap[eId] ?: emptyList(),
                origin = Triple(this[offset + 8], this[offset + 9], this[offset + 10]),
                direction = Triple(this[offset + 11], this[offset + 12], this[offset + 13]),
                radius = this[offset + 14],
                degree = this[offset + 15].toInt(),
                poleCount = this[offset + 16].toInt(),
                knotCount = this[offset + 17].toInt(),
                isClosed = this[offset + 18] > 0.5
            )
        )
    }

    return edgeList
}

private fun IntArray.toPerFaceData(): Map<Int, List<Int>> {
    val map = mutableMapOf<Int, List<Int>>()
    var cursor = 0
    while (cursor < this.size) {
        val fId = this[cursor++]
        val count = this[cursor++]
        val edges = mutableListOf<Int>()
        for (k in 0 until count) {
            edges.add(this[cursor++])
        }
        map[fId] = edges
    }
    return map
}

private fun DoubleArray.toFaceRecord(toPerFaceData: Map<Int, List<Int>>):  MutableList<FaceRecord> {
    val faceList = mutableListOf<FaceRecord>()
    val fStride = 20
    for (i in 0 until (this.size / fStride)) {
       val offset = i * fStride
       val fId = this[offset].toInt()
        faceList.add(
            FaceRecord(
                faceId = fId,
                surfaceType = SurfaceType.fromValue(this[offset + 1].toInt()),
                area = this[offset + 2],
                uMin = this[offset + 3],
                uMax = this[offset + 4],
                vMin = this[offset + 5],
                vMax = this[offset + 6],
                tolerance = this[offset + 7],
                isOrientationReversed = this[offset + 8] > 0.5,
                axisDirection = Triple(this[offset + 9], this[offset + 10], this[offset + 11]),
                origin = Triple(this[offset + 12], this[offset + 13], this[offset + 14]),
                primaryParameter = this[offset + 15],
                secondaryParameter = this[offset + 16],
                uDegree = this[offset + 17].toInt(),
                vDegree = this[offset + 18].toInt(),
                wireCount = this[offset + 19].toInt(),
                boundaryEdgeIds = toPerFaceData[fId] ?: emptyList(),
            )
        )
    }

    return faceList
}

private fun IntArray.toShapeHierarchy(): List<ShapeHierarchyNode> {
    val nodeMap = mutableMapOf<Int, ShapeHierarchyNode>()
    val roots = mutableListOf<ShapeHierarchyNode>()
    val stride = 4
    val nodeCount = size / stride

    for (i in 0 until nodeCount) {
        val off = i * stride
        val treeNodeId = this[off]
        val parentTreeNodeId = this[off + 1]
        val type = ShapeType.fromValue(this[off + 2])
        val topologyIndex = this[off + 3]
        nodeMap[treeNodeId] = ShapeHierarchyNode(treeNodeId, parentTreeNodeId, type, topologyIndex)
    }

    for (node in nodeMap.values) {
        if (node.parentTreeNodeId == -1) roots.add(node)
        else nodeMap[node.parentTreeNodeId]?.children?.add(node)
    }
    return roots
}

fun RawOcctModel.toTessellationMesh(
    vertices: FloatArray?,
    normals: FloatArray?,
    triangles: IntArray?,
    triangleFaceIds: IntArray?
): TriangleMesh? =
    if (vertices != null && normals != null && triangles != null)
        TriangleMesh(
            vertices = vertices,
            normals = normals,
            triangles = triangles,
            triangleFaceIds = triangleFaceIds ?: intArrayOf()
        )
    else null