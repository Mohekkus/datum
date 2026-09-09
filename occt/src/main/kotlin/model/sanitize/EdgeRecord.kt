package cc.shinemoon.occt.model.sanitize

import cc.shinemoon.occt.classifier.CurveType

data class EdgeRecord(
    val edgeId: Int, // 1..N
    val curveType: CurveType,
    val length: Double,
    val tolerance: Double,
    val startVertexId: Int, // 1..N, or 0 if none
    val endVertexId: Int,   // 1..N, or 0 if none
    val isDegenerated: Boolean,
    val isOrientationReversed: Boolean,
    val adjacentFaceIds: List<Int>, // Topological faces sharing this edge
    val origin: Triple<Double, Double, Double>,
    val direction: Triple<Double, Double, Double>,
    val radius: Double, // For Circle / Ellipse
    val degree: Int,    // For BSpline
    val poleCount: Int,
    val knotCount: Int,
    val isClosed: Boolean
)