package cc.shinemoon.occt.model.sanitize

data class ToleranceStatistics(
    val minVertexTol: Double,
    val maxVertexTol: Double,
    val avgVertexTol: Double,
    val minEdgeTol: Double,
    val maxEdgeTol: Double,
    val avgEdgeTol: Double,
    val minFaceTol: Double,
    val maxFaceTol: Double,
    val avgFaceTol: Double
)