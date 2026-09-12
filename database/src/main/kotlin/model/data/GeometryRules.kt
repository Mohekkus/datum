package cc.shinemoon.datumabase.model.data

import kotlinx.serialization.Serializable

@Serializable
data class GeometryRules(
    // Fillet/Radius limits in mm
//    val minFilletRadius: Double? = null,
//    val maxFilletRadius: Double? = null,

    // Allowed surface types (e.g., ["PLANE", "CYLINDER", "CONE"]).
    // Null means all surface types are allowed.
    val allowedSurfaceTypes: List<String>? = null,

    // Allowed curve types for edges (e.g., ["LINE", "CIRCLE", "BSPLINE_CURVE"])
    val allowedCurveTypes: List<String>? = null
)