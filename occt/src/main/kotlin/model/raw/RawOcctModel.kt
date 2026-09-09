package cc.shinemoon.occt.model.raw

data class RawOcctModel(
    val stepMetadata: Array<String>,
    val unitName: String,
    val unitScaleToMm: Double,
    val shapeType: Int,
    val topologyCounts: IntArray,
    val shapeValid: Boolean,
    val shapeClosed: Boolean,
    val manifoldStats: IntArray,
    val shapeFaults: Array<String>,
    val boundingBox: DoubleArray,
    val placement: DoubleArray,
    val volume: Double,
    val surfaceArea: Double,
    val totalEdgeLength: Double,
    val centerOfMass: DoubleArray?,
    val inertiaProperties: DoubleArray?,
    val vertexRecords: DoubleArray,
    val edgeRecords: DoubleArray,
    val faceRecords: DoubleArray,
    val faceBoundaryEdgeMap: IntArray,
    val tolerances: DoubleArray,
    val shapeTreeRecords: IntArray,
    val meshVertices: FloatArray?,
    val meshNormals: FloatArray?,
    val meshTriangles: IntArray?
)