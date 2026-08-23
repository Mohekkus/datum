package cc.shinemoon.occt

import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

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


object OcctDllResolver {
    private const val RESOURCE_PATH = "/native/libOcctStatic.dll"

    fun resolve(): Path {
        System.getProperty("occt.dll.path")?.let { return Path.of(it) }

        val devPath = Path.of("native/libOcctStatic.dll")
        if (Files.isRegularFile(devPath)) return devPath

        return extractFromResources()
    }

    private fun extractFromResources(): Path {
        val bytes = OcctDllResolver::class.java.getResourceAsStream(RESOURCE_PATH)
            ?.use { it.readBytes() }
            ?: error("DLL resource not found on classpath: $RESOURCE_PATH")

        val hash = MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { "%02x".format(it) }
            .take(16)

        val cacheDir = Path.of(System.getProperty("java.io.tmpdir"), "occt-bridge-$hash")
        val cachedDll = cacheDir.resolve("libOcctStatic.dll")

        if (!Files.isRegularFile(cachedDll)) {
            Files.createDirectories(cacheDir)
            Files.write(cachedDll, bytes)
        }
        return cachedDll
    }
}

/** Direct DLL-backed API for embedding in an IntelliJ/Kotlin application. */
class OcctInspectionSession(nativeDll: Path) : Closeable {
    private var handle: Long

    init {
        require(Files.isRegularFile(nativeDll)) {
            "Native OCCT bridge DLL does not exist: ${nativeDll.toAbsolutePath()}"
        }
        DirectOcctNative.load(nativeDll)
        handle = DirectOcctNative.createContext()
        check(handle != 0L) { "OCCT native context creation failed" }
    }

    val isOpen: Boolean
        get() = handle != 0L

    fun inspect(stepFile: Path, performTessellation: Boolean = true): RawOcctModel {
        check(isOpen) { "OCCT inspection session is closed" }
        require(Files.isRegularFile(stepFile)) {
            "STEP file does not exist: ${stepFile.toAbsolutePath()}"
        }

        val status = DirectOcctNative.loadStep(handle, stepFile.toAbsolutePath().toString())
        check(status == 1) { "Failed to load STEP file (IFSelect status: $status)" }
        check(DirectOcctNative.transferRoots(handle) > 0 && DirectOcctNative.hasTransferredShape(handle)) {
            "STEP file did not produce a transferable shape"
        }

        if (performTessellation) {
            DirectOcctNative.tessellate(handle, 0.1, 0.5)
        }

        return RawOcctModel(
            stepMetadata = DirectOcctNative.getStepMetadata(handle) ?: emptyArray(),
            unitName = DirectOcctNative.getModelUnitName(handle),
            unitScaleToMm = DirectOcctNative.getModelUnitScale(),
            shapeType = DirectOcctNative.getTransferredShapeType(handle),
            topologyCounts = DirectOcctNative.getTopologyCounts(handle) ?: IntArray(0),
            shapeValid = DirectOcctNative.checkShapeValidity(handle),
            shapeClosed = DirectOcctNative.isShapeClosed(handle),
            manifoldStats = DirectOcctNative.getEdgeManifoldStats(handle) ?: IntArray(0),
            shapeFaults = DirectOcctNative.getShapeFaults(handle) ?: emptyArray(),
            boundingBox = DirectOcctNative.getBoundingBox(handle) ?: DoubleArray(0),
            placement = DirectOcctNative.getModelPlacement(handle) ?: DoubleArray(0),
            volume = DirectOcctNative.getVolume(handle),
            surfaceArea = DirectOcctNative.getSurfaceArea(handle),
            totalEdgeLength = DirectOcctNative.getTotalEdgeLength(handle),
            centerOfMass = DirectOcctNative.getCenterOfMass(handle),
            inertiaProperties = DirectOcctNative.getInertiaProperties(handle),
            vertexRecords = DirectOcctNative.getVertexRecords(handle) ?: DoubleArray(0),
            edgeRecords = DirectOcctNative.getDetailedEdgeRecords(handle) ?: DoubleArray(0),
            faceRecords = DirectOcctNative.getDetailedFaceRecords(handle) ?: DoubleArray(0),
            faceBoundaryEdgeMap = DirectOcctNative.getFaceBoundaryEdgeMap(handle) ?: IntArray(0),
            tolerances = DirectOcctNative.getTolerances(handle) ?: DoubleArray(0),
            shapeTreeRecords = DirectOcctNative.getShapeTreeRecords(handle) ?: IntArray(0),
            meshVertices = if (performTessellation) DirectOcctNative.getMeshVertices(handle) else null,
            meshNormals = if (performTessellation) DirectOcctNative.getMeshNormals(handle) else null,
            meshTriangles = if (performTessellation) DirectOcctNative.getMeshTriangles(handle) else null
        )
    }

    override fun close() {
        if (handle != 0L) {
            DirectOcctNative.destroyContext(handle)
            handle = 0L
        }
    }
}

private val DirectOcctNative = OcctBridge

private object OcctBridge {
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
