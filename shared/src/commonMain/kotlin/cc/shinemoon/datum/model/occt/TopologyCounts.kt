package cc.shinemoon.datum.model.occt

data class TopologyCounts(
    val solids: Int,
    val shells: Int,
    val faces: Int,
    val wires: Int,
    val edges: Int,
    val vertices: Int,
    val compounds: Int
)
