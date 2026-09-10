package cc.shinemoon.datum.model.occt

data class MassProperties(
    val volume: Double,
    val surfaceArea: Double,
    val totalEdgeLength: Double,
    val centerOfMass: Triple<Double, Double, Double>?,
    val inertia: InertiaTensor?
)
