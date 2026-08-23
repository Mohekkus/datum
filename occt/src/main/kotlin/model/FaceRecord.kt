package cc.shinemoon.occt.model

import cc.shinemoon.occt.classifier.SurfaceType

data class FaceRecord(
    val faceId: Int, // 1..N
    val surfaceType: SurfaceType,
    val area: Double,
    val uMin: Double, val uMax: Double,
    val vMin: Double, val vMax: Double,
    val tolerance: Double,
    val isOrientationReversed: Boolean,
    val axisDirection: Triple<Double, Double, Double>,
    val origin: Triple<Double, Double, Double>,
    val primaryParameter: Double,   // Radius (Cylinder/Sphere/Torus), Semi-Angle rad (Cone)
    val secondaryParameter: Double, // Minor Radius (Torus)
    val uDegree: Int,
    val vDegree: Int,
    val wireCount: Int,
    val boundaryEdgeIds: List<Int> // Boundary edge IDs (1..N)
)