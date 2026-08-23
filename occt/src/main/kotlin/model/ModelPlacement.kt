package cc.shinemoon.occt.model

data class ModelPlacement(
    val translation: Triple<Double, Double, Double>,
    val scaleFactor: Double,
    val rotationMatrix: DoubleArray // 3x3 matrix [R11, R12, R13, R21, R22, R23, R31, R32, R33]
)
