package cc.shinemoon.datum.model.occt

data class ModelPlacement(
    val translation: Triple<Double, Double, Double>,
    val scaleFactor: Double,
    val rotationMatrix: DoubleArray
)
