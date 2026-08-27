package cc.shinemoon.datum.model.raw

import kotlinx.serialization.Serializable

@Serializable
data class TopologyRules(
    val requireValidBRep: Boolean = false,
    val requireClosedSolid: Boolean = false,

    // Maximum allowed open boundary edges (0 means strictly watertight)
    val maxOpenBoundaryEdges: Int? = null,

    // Maximum allowed non-manifold edges
    val maxNonManifoldEdges: Int? = null,

    // Expected range for the number of solids. Null means "don't check".
    val minSolids: Int? = null,
    val maxSolids: Int? = null,

    // Expected range for the number of faces
    val minFaces: Int? = null,
    val maxFaces: Int? = null
)
