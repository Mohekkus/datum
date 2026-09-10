package cc.shinemoon.datum.model.occt

data class InertiaTensor(
    val ixx: Double, val iyy: Double, val izz: Double,
    val ixy: Double, val ixz: Double, val iyz: Double,
    val principalMoments: Triple<Double, Double, Double>,
    val principalAxes: List<Triple<Double, Double, Double>>,
    val radiusOfGyration: Triple<Double, Double, Double>
)
