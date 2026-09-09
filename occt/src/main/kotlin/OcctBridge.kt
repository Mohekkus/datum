import java.nio.file.Path


internal object OcctBridge {
    private var loadedDll: String? = null

    fun load(nativeDll: Path) {
        val absolutePath = nativeDll.toAbsolutePath().toString()
        if (loadedDll == null) {
            System.load(absolutePath)
            loadedDll = absolutePath
        } else {
            check(loadedDll == absolutePath) {
                "A different OCCT bridge DLL is already loaded: $loadedDll"
            }
        }
    }

    @JvmStatic external fun createContext(): Long
    @JvmStatic external fun destroyContext(handle: Long)
    @JvmStatic external fun loadStep(handle: Long, path: String): Int
    @JvmStatic external fun transferRoots(handle: Long): Int
    @JvmStatic external fun hasTransferredShape(handle: Long): Boolean
    @JvmStatic external fun getTransferredShapeType(handle: Long): Int
    @JvmStatic external fun getStepMetadata(handle: Long): Array<String>?
    @JvmStatic external fun getModelUnitName(handle: Long): String
    @JvmStatic external fun getModelUnitScale(): Double
    @JvmStatic external fun getTopologyCounts(handle: Long): IntArray?
    @JvmStatic external fun checkShapeValidity(handle: Long): Boolean
    @JvmStatic external fun isShapeClosed(handle: Long): Boolean
    @JvmStatic external fun getEdgeManifoldStats(handle: Long): IntArray?
    @JvmStatic external fun getShapeFaults(handle: Long): Array<String>?
    @JvmStatic external fun getBoundingBox(handle: Long): DoubleArray?
    @JvmStatic external fun getModelPlacement(handle: Long): DoubleArray?
    @JvmStatic external fun getVolume(handle: Long): Double
    @JvmStatic external fun getSurfaceArea(handle: Long): Double
    @JvmStatic external fun getTotalEdgeLength(handle: Long): Double
    @JvmStatic external fun getCenterOfMass(handle: Long): DoubleArray?
    @JvmStatic external fun getInertiaProperties(handle: Long): DoubleArray?
    @JvmStatic external fun getVertexRecords(handle: Long): DoubleArray?
    @JvmStatic external fun getDetailedEdgeRecords(handle: Long): DoubleArray?
    @JvmStatic external fun getDetailedFaceRecords(handle: Long): DoubleArray?
    @JvmStatic external fun getFaceBoundaryEdgeMap(handle: Long): IntArray?
    @JvmStatic external fun getTolerances(handle: Long): DoubleArray?
    @JvmStatic external fun getShapeTreeRecords(handle: Long): IntArray?
    @JvmStatic external fun tessellate(handle: Long, linearDeflection: Double, angularDeflection: Double): Boolean
    @JvmStatic external fun getMeshVertices(handle: Long): FloatArray?
    @JvmStatic external fun getMeshNormals(handle: Long): FloatArray?
    @JvmStatic external fun getMeshTriangles(handle: Long): IntArray?
}
