package cc.shinemoon.datum.model.preset

import kotlinx.serialization.Serializable

@Serializable
data class ToleranceRules(
    // Maximum allowed tolerances. If the model's max tolerance exceeds this, it fails.
    val maxVertexTolerance: Double? = null,
    val maxEdgeTolerance: Double? = null,
    val maxFaceTolerance: Double? = null
)
