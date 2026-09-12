package cc.shinemoon.datumabase.model.data

import kotlinx.serialization.Serializable

@Serializable
data class MassPropertyRules(
    // Volume limits in mm³
    val minVolume: Double? = null,
    val maxVolume: Double? = null,

    // Surface Area limits in mm²
    val minSurfaceArea: Double? = null,
    val maxSurfaceArea: Double? = null,

    // Total Edge Length limits in mm
    val minTotalEdgeLength: Double? = null,
    val maxTotalEdgeLength: Double? = null
)