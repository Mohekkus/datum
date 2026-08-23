package cc.shinemoon.occt.model

data class Metadata(
    val schema: String,
    val fileName: String,
    val timestamp: String,
    val author: String,
    val organization: String,
    val preprocessor: String,
    val originatingSystem: String,
    val authorization: String,
    val unitName: String,
    val unitScaleToMm: Double
)
