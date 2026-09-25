package cc.shinemoon.datum.testutil

import cc.shinemoon.datum.model.occt.*
import cc.shinemoon.datum.types.occt.CurveType
import cc.shinemoon.datum.types.occt.ShapeType
import cc.shinemoon.datum.types.occt.SurfaceType

fun sampleInspectionData(
    isBRepValid: Boolean = true,
    isClosed: Boolean = true,
    openBoundaryEdges: Int = 0,
    nonManifoldEdges: Int = 0,
    solids: Int = 1,
    facesCount: Int = 6,
    volume: Double = 1000.0,
    surfaceArea: Double = 600.0,
    totalEdgeLength: Double = 120.0,
    maxVertexTol: Double = 1e-4,
    maxEdgeTol: Double = 1e-4,
    maxFaceTol: Double = 1e-4,
    faces: List<FaceRecord> = listOf(
        FaceRecord(
            faceId = 1,
            surfaceType = SurfaceType.PLANE,
            area = 100.0,
            uMin = 0.0, uMax = 10.0,
            vMin = 0.0, vMax = 10.0,
            tolerance = 1e-4,
            isOrientationReversed = false,
            axisDirection = Triple(0.0, 0.0, 1.0),
            origin = Triple(0.0, 0.0, 0.0),
            primaryParameter = 0.0,
            secondaryParameter = 0.0,
            uDegree = 1,
            vDegree = 1,
            wireCount = 1,
            boundaryEdgeIds = listOf(1, 2, 3, 4)
        )
    ),
    edges: List<EdgeRecord> = listOf(
        EdgeRecord(
            edgeId = 1,
            curveType = CurveType.LINE,
            length = 10.0,
            tolerance = 1e-4,
            startVertexId = 1,
            endVertexId = 2,
            isDegenerated = false,
            isOrientationReversed = false,
            adjacentFaceIds = listOf(1),
            origin = Triple(0.0, 0.0, 0.0),
            direction = Triple(1.0, 0.0, 0.0),
            radius = 0.0,
            degree = 1,
            poleCount = 2,
            knotCount = 2,
            isClosed = false
        )
    )
): OcctInspectionData = OcctInspectionData(
    rootShapeType = ShapeType.SOLID,
    metadata = Metadata(
        schema = "AP214",
        fileName = "test.step",
        timestamp = "2026-09-25T00:00:00",
        author = "Test",
        organization = "TestOrg",
        preprocessor = "TestPreprocessor",
        originatingSystem = "TestSystem",
        authorization = "TestAuth",
        unitName = "MM",
        unitScaleToMm = 1.0
    ),
    placement = ModelPlacement(
        translation = Triple(0.0, 0.0, 0.0),
        scaleFactor = 1.0,
        rotationMatrix = DoubleArray(9) { if (it % 4 == 0) 1.0 else 0.0 }
    ),
    topology = TopologyCounts(
        solids = solids,
        shells = 1,
        faces = facesCount,
        wires = 6,
        edges = 12,
        vertices = 8,
        compounds = 0
    ),
    topologicalState = TopologicalState(
        isBRepValid = isBRepValid,
        faults = emptyList(),
        isClosed = isClosed,
        openBoundaryEdges = openBoundaryEdges,
        nonManifoldEdges = nonManifoldEdges
    ),
    boundingBox = BoundingBox(0.0, 0.0, 0.0, 10.0, 10.0, 10.0),
    massProperties = MassProperties(
        volume = volume,
        surfaceArea = surfaceArea,
        totalEdgeLength = totalEdgeLength,
        centerOfMass = Triple(5.0, 5.0, 5.0),
        inertia = null
    ),
    tolerances = ToleranceStatistics(
        minVertexTol = 1e-5,
        maxVertexTol = maxVertexTol,
        avgVertexTol = 5e-5,
        minEdgeTol = 1e-5,
        maxEdgeTol = maxEdgeTol,
        avgEdgeTol = 5e-5,
        minFaceTol = 1e-5,
        maxFaceTol = maxFaceTol,
        avgFaceTol = 5e-5
    ),
    vertices = emptyList(),
    edges = edges,
    faces = faces,
    hierarchy = emptyList(),
    mesh = null
)
