package cc.shinemoon.datum.model.occt

data class VertexRecord(
    val vertexId: Int, // 1..N
    val x: Double,
    val y: Double,
    val z: Double,
    val tolerance: Double
)