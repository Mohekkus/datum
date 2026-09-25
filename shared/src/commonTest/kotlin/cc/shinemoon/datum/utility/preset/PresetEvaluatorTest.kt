package cc.shinemoon.datum.utility.preset

import cc.shinemoon.datum.model.occt.EdgeRecord
import cc.shinemoon.datum.model.occt.FaceRecord
import cc.shinemoon.datum.model.preset.*
import cc.shinemoon.datum.testutil.sampleInspectionData
import cc.shinemoon.datum.types.occt.CurveType
import cc.shinemoon.datum.types.occt.SurfaceType
import cc.shinemoon.datum.types.preset.MetricStatus
import cc.shinemoon.datum.types.preset.RuleGroup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PresetEvaluatorTest {

    @Test
    fun validBRep_passes_and_invalidBRep_fails() {
        val preset = PresetModel(
            name = "BRep Check",
            topologyRules = TopologyRules(requireValidBRep = true)
        )

        val passData = sampleInspectionData(isBRepValid = true)
        val passEval = PresetEvaluator.evaluate(preset, passData)
        assertEquals(MetricStatus.PASS, passEval.checkStatus("topology.validBRep"))
        assertEquals(MetricStatus.PASS, passEval.overall)

        val failData = sampleInspectionData(isBRepValid = false)
        val failEval = PresetEvaluator.evaluate(preset, failData)
        assertEquals(MetricStatus.FAIL, failEval.checkStatus("topology.validBRep"))
        assertEquals(MetricStatus.FAIL, failEval.overall)
    }

    @Test
    fun closedSolid_passes_whenClosed_fails_whenNotClosed() {
        val preset = PresetModel(
            name = "Closed Solid Check",
            topologyRules = TopologyRules(requireClosedSolid = true)
        )

        val passEval = PresetEvaluator.evaluate(preset, sampleInspectionData(isClosed = true))
        assertEquals(MetricStatus.PASS, passEval.checkStatus("topology.closed"))

        val failEval = PresetEvaluator.evaluate(preset, sampleInspectionData(isClosed = false))
        assertEquals(MetricStatus.FAIL, failEval.checkStatus("topology.closed"))
    }

    @Test
    fun openBoundaryEdges_evaluates_threshold() {
        val preset = PresetModel(
            name = "Open Edges",
            topologyRules = TopologyRules(maxOpenBoundaryEdges = 2)
        )

        val passEval = PresetEvaluator.evaluate(preset, sampleInspectionData(openBoundaryEdges = 2))
        assertEquals(MetricStatus.PASS, passEval.checkStatus("topology.openEdges"))

        val failEval = PresetEvaluator.evaluate(preset, sampleInspectionData(openBoundaryEdges = 3))
        assertEquals(MetricStatus.FAIL, failEval.checkStatus("topology.openEdges"))
    }

    @Test
    fun nonManifoldEdges_evaluates_threshold() {
        val preset = PresetModel(
            name = "Non-Manifold",
            topologyRules = TopologyRules(maxNonManifoldEdges = 0)
        )

        val passEval = PresetEvaluator.evaluate(preset, sampleInspectionData(nonManifoldEdges = 0))
        assertEquals(MetricStatus.PASS, passEval.checkStatus("topology.nonManifoldEdges"))

        val failEval = PresetEvaluator.evaluate(preset, sampleInspectionData(nonManifoldEdges = 1))
        assertEquals(MetricStatus.FAIL, failEval.checkStatus("topology.nonManifoldEdges"))
    }

    @Test
    fun solidAndFaceCounts_evaluate_ranges() {
        val preset = PresetModel(
            name = "Count Ranges",
            topologyRules = TopologyRules(
                minSolids = 1,
                maxSolids = 2,
                minFaces = 4,
                maxFaces = 10
            )
        )

        val inRange = sampleInspectionData(solids = 1, facesCount = 6)
        val inRangeEval = PresetEvaluator.evaluate(preset, inRange)
        assertEquals(MetricStatus.PASS, inRangeEval.checkStatus("topology.solids"))
        assertEquals(MetricStatus.PASS, inRangeEval.checkStatus("topology.faces"))

        val outOfRange = sampleInspectionData(solids = 3, facesCount = 2)
        val outRangeEval = PresetEvaluator.evaluate(preset, outOfRange)
        assertEquals(MetricStatus.FAIL, outRangeEval.checkStatus("topology.solids"))
        assertEquals(MetricStatus.FAIL, outRangeEval.checkStatus("topology.faces"))
    }

    @Test
    fun massProperties_ranges_evaluate_correctly() {
        val preset = PresetModel(
            name = "Mass Rules",
            massPropertyRules = MassPropertyRules(
                minVolume = 500.0,
                maxVolume = 1500.0,
                minSurfaceArea = 300.0,
                maxSurfaceArea = 800.0,
                minTotalEdgeLength = 50.0,
                maxTotalEdgeLength = 200.0
            )
        )

        val passData = sampleInspectionData(volume = 1000.0, surfaceArea = 600.0, totalEdgeLength = 120.0)
        val passEval = PresetEvaluator.evaluate(preset, passData)
        assertEquals(MetricStatus.PASS, passEval.checkStatus("mass.volume"))
        assertEquals(MetricStatus.PASS, passEval.checkStatus("mass.surfaceArea"))
        assertEquals(MetricStatus.PASS, passEval.checkStatus("mass.edgeLength"))

        val failData = sampleInspectionData(volume = 2000.0, surfaceArea = 200.0, totalEdgeLength = 300.0)
        val failEval = PresetEvaluator.evaluate(preset, failData)
        assertEquals(MetricStatus.FAIL, failEval.checkStatus("mass.volume"))
        assertEquals(MetricStatus.FAIL, failEval.checkStatus("mass.surfaceArea"))
        assertEquals(MetricStatus.FAIL, failEval.checkStatus("mass.edgeLength"))
    }

    @Test
    fun toleranceRules_evaluate_max_limits() {
        val preset = PresetModel(
            name = "Tolerance Rules",
            toleranceRules = ToleranceRules(
                maxVertexTolerance = 1e-3,
                maxEdgeTolerance = 1e-3,
                maxFaceTolerance = 1e-3
            )
        )

        val passData = sampleInspectionData(maxVertexTol = 1e-4, maxEdgeTol = 1e-4, maxFaceTol = 1e-4)
        val passEval = PresetEvaluator.evaluate(preset, passData)
        assertEquals(MetricStatus.PASS, passEval.checkStatus("tolerance.vertex"))
        assertEquals(MetricStatus.PASS, passEval.checkStatus("tolerance.edge"))
        assertEquals(MetricStatus.PASS, passEval.checkStatus("tolerance.face"))

        val failData = sampleInspectionData(maxVertexTol = 1e-2, maxEdgeTol = 1e-2, maxFaceTol = 1e-2)
        val failEval = PresetEvaluator.evaluate(preset, failData)
        assertEquals(MetricStatus.FAIL, failEval.checkStatus("tolerance.vertex"))
        assertEquals(MetricStatus.FAIL, failEval.checkStatus("tolerance.edge"))
        assertEquals(MetricStatus.FAIL, failEval.checkStatus("tolerance.face"))
    }

    @Test
    fun geometryRules_evaluate_allowed_surface_and_curve_types() {
        val preset = PresetModel(
            name = "Geometry Whitelist",
            geometryRules = GeometryRules(
                allowedSurfaceTypes = listOf(SurfaceType.PLANE.name, SurfaceType.CYLINDER.name),
                allowedCurveTypes = listOf(CurveType.LINE.name)
            )
        )

        val faces = listOf(
            FaceRecord(1, SurfaceType.PLANE, 100.0, 0.0, 1.0, 0.0, 1.0, 1e-4, false, Triple(0.0,0.0,1.0), Triple(0.0,0.0,0.0), 0.0, 0.0, 1, 1, 1, listOf(1)),
            FaceRecord(2, SurfaceType.TORUS, 50.0, 0.0, 1.0, 0.0, 1.0, 1e-4, false, Triple(0.0,0.0,1.0), Triple(0.0,0.0,0.0), 0.0, 0.0, 1, 1, 1, listOf(2))
        )
        val edges = listOf(
            EdgeRecord(1, CurveType.LINE, 10.0, 1e-4, 1, 2, false, false, listOf(1), Triple(0.0,0.0,0.0), Triple(1.0,0.0,0.0), 0.0, 1, 2, 2, false),
            EdgeRecord(2, CurveType.BSPLINE_CURVE, 15.0, 1e-4, 2, 3, false, false, listOf(2), Triple(0.0,0.0,0.0), Triple(0.0,1.0,0.0), 0.0, 3, 4, 4, false)
        )

        val data = sampleInspectionData(faces = faces, edges = edges)
        val evaluation = PresetEvaluator.evaluate(preset, data)

        assertEquals(MetricStatus.FAIL, evaluation.checkStatus("geometry.surfaceTypes"))
        assertEquals(MetricStatus.FAIL, evaluation.checkStatus("geometry.curveTypes"))
        assertEquals(MetricStatus.PASS, evaluation.faceStatuses[1])
        assertEquals(MetricStatus.FAIL, evaluation.faceStatuses[2])
        assertEquals(MetricStatus.PASS, evaluation.edgeStatuses[1])
        assertEquals(MetricStatus.FAIL, evaluation.edgeStatuses[2])
    }

    @Test
    fun unconfiguredRules_doNotProduceChecks() {
        val emptyPreset = PresetModel(name = "Empty Preset")
        val evaluation = PresetEvaluator.evaluate(emptyPreset, sampleInspectionData())

        assertEquals(0, evaluation.checks.size)
        assertEquals(0, evaluation.passedCount)
        assertNull(evaluation.checkStatus("topology.validBRep"))
        assertNull(evaluation.groupStatus(RuleGroup.TOPOLOGY))
    }

    @Test
    fun evaluationHelperMethods_workAsExpected() {
        val preset = PresetModel(
            name = "Mixed",
            topologyRules = TopologyRules(requireValidBRep = true, requireClosedSolid = true)
        )
        val mixedData = sampleInspectionData(isBRepValid = true, isClosed = false)
        val evaluation = PresetEvaluator.evaluate(preset, mixedData)

        assertEquals(2, evaluation.checks.size)
        assertEquals(1, evaluation.passedCount)
        assertEquals(MetricStatus.FAIL, evaluation.overall)
        assertEquals(MetricStatus.FAIL, evaluation.groupStatus(RuleGroup.TOPOLOGY))
        assertNull(evaluation.groupStatus(RuleGroup.MASS_PROPERTIES))
    }
}
