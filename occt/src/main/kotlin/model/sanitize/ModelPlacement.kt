package cc.shinemoon.occt.model.sanitize

data class ModelPlacement(
    val translation: Triple<Double, Double, Double>,
    val scaleFactor: Double,
    val rotationMatrix: DoubleArray
)
