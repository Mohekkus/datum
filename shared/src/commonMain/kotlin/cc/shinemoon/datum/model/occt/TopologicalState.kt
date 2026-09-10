package cc.shinemoon.datum.model.occt

data class TopologicalState(
    val isBRepValid: Boolean,
    val faults: List<String>,
    val isClosed: Boolean,
    val openBoundaryEdges: Int,
    val nonManifoldEdges: Int
)
