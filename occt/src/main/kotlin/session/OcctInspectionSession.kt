package session

import bridge.OcctDllResolver
import cc.shinemoon.occt.OcctBridge
import model.RawOcctModel
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Singleton

private val DirectOcctNative = OcctBridge

/** Direct DLL-backed API for embedding in an IntelliJ/Kotlin application. */
@Singleton
internal class OcctInspectionSession @Inject constructor(
    private val dllResolver: OcctDllResolver
) : Closeable {
    private var handle: Long = 0L
    private val nativeDll: Path
        get() {
            return dllResolver.resolve()
        }

    fun open() {
        require(Files.isRegularFile(nativeDll)) {
            "Native OCCT bridge DLL does not exist: ${nativeDll.toAbsolutePath()}"
        }
        DirectOcctNative.load(nativeDll)
        handle = DirectOcctNative.createContext()
        check(handle != 0L) { "OCCT native context creation failed" }
    }

    val isOpen: Boolean
        get() = handle != 0L

    private fun IntArray.requireSizeOrEmpty(field: String, expected: Int): IntArray {
        if (isNotEmpty() && size != expected) throw OcctDataException(field, expected, size)
        return this
    }

    private fun DoubleArray.requireSizeOrEmpty(field: String, expected: Int): DoubleArray {
        if (isNotEmpty() && size != expected) throw OcctDataException(field, expected, size)
        return this
    }

    fun inspect(stepFile: Path, performTessellation: Boolean = true): RawOcctModel {
        if(!isOpen) open()

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
            topologyCounts = (DirectOcctNative.getTopologyCounts(handle) ?: IntArray(0))
                .requireSizeOrEmpty("topologyCounts", 7),
            shapeValid = DirectOcctNative.checkShapeValidity(handle),
            shapeClosed = DirectOcctNative.isShapeClosed(handle),
            manifoldStats = (DirectOcctNative.getEdgeManifoldStats(handle) ?: IntArray(0))
                .requireSizeOrEmpty("manifoldStats", 2),
            shapeFaults = DirectOcctNative.getShapeFaults(handle) ?: emptyArray(),
            boundingBox = (DirectOcctNative.getBoundingBox(handle) ?: DoubleArray(0))
                .requireSizeOrEmpty("boundingBox", 6),
            placement = (DirectOcctNative.getModelPlacement(handle) ?: DoubleArray(0))
                .requireSizeOrEmpty("placement", 13),
            volume = DirectOcctNative.getVolume(handle),
            surfaceArea = DirectOcctNative.getSurfaceArea(handle),
            totalEdgeLength = DirectOcctNative.getTotalEdgeLength(handle),
            centerOfMass = DirectOcctNative.getCenterOfMass(handle)
                ?.also { if (it.size != 3) throw OcctDataException("centerOfMass", 3, it.size) },
            inertiaProperties = DirectOcctNative.getInertiaProperties(handle)
                ?.also { if (it.size != 21) throw OcctDataException("inertiaProperties", 21, it.size) },
            vertexRecords = (DirectOcctNative.getVertexRecords(handle) ?: DoubleArray(0))
                .also { if (it.size % 5 != 0) throw OcctDataException("vertexRecords", it.size - (it.size % 5), it.size) },
            edgeRecords = (DirectOcctNative.getDetailedEdgeRecords(handle) ?: DoubleArray(0))
                .also { if (it.size % 19 != 0) throw OcctDataException("edgeRecords", it.size - (it.size % 19), it.size) },
            faceRecords = (DirectOcctNative.getDetailedFaceRecords(handle) ?: DoubleArray(0))
                .also { if (it.size % 20 != 0) throw OcctDataException("faceRecords", it.size - (it.size % 20), it.size) },
            faceBoundaryEdgeMap = DirectOcctNative.getFaceBoundaryEdgeMap(handle) ?: IntArray(0),
            tolerances = (DirectOcctNative.getTolerances(handle) ?: DoubleArray(0))
                .requireSizeOrEmpty("tolerances", 9),
            shapeTreeRecords = (DirectOcctNative.getShapeTreeRecords(handle) ?: IntArray(0))
                .also { if (it.size % 4 != 0) throw OcctDataException("shapeTreeRecords", it.size - (it.size % 4), it.size) },
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
