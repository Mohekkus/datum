package cc.shinemoon.datum.utility.preset

import cc.shinemoon.datum.model.preset.GeometryRules
import cc.shinemoon.datum.model.preset.MassPropertyRules
import cc.shinemoon.datum.model.preset.ToleranceRules
import cc.shinemoon.datum.model.preset.TopologyRules
import cc.shinemoon.datum.model.preset.PresetModel
import cc.shinemoon.datum.model.preset.RuleCheck
import cc.shinemoon.datum.types.preset.MetricStatus
import cc.shinemoon.datum.types.preset.RuleGroup
import cc.shinemoon.datum.model.occt.MassProperties
import cc.shinemoon.datum.model.occt.OcctInspectionData
import cc.shinemoon.datum.model.occt.ToleranceStatistics

object PresetEvaluator {

    fun evaluate(preset: PresetModel, data: OcctInspectionData): PresetEvaluation {
        val checks = mutableListOf<RuleCheck>()
        evaluateTopology(preset.topologyRules, data, checks)
        evaluateMassProperties(preset.massPropertyRules, data.massProperties, checks)
        evaluateTolerances(preset.toleranceRules, data.tolerances, checks)
        val (geometryChecks, faceStatuses, edgeStatuses) = evaluateGeometry(preset.geometryRules, data)
        checks += geometryChecks

        return PresetEvaluation(
            presetName = preset.name,
            checks = checks,
            edgeStatuses = edgeStatuses,
            faceStatuses = faceStatuses
        )
    }

    private fun evaluateTopology(rules: TopologyRules, data: OcctInspectionData, out: MutableList<RuleCheck>) {
        val state = data.topologicalState
        val topo = data.topology

        if (rules.requireValidBRep) {
            out += RuleCheck("topology.validBRep", RuleGroup.TOPOLOGY, "Valid B-Rep",
                "isBRepValid=${state.isBRepValid}",
                if (state.isBRepValid) MetricStatus.PASS else MetricStatus.FAIL)
        }
        if (rules.requireClosedSolid) {
            out += RuleCheck("topology.closed", RuleGroup.TOPOLOGY, "Closed Solid",
                "isClosed=${state.isClosed}",
                if (state.isClosed) MetricStatus.PASS else MetricStatus.FAIL)
        }
        rules.maxOpenBoundaryEdges?.let { max ->
            out += RuleCheck("topology.openEdges", RuleGroup.TOPOLOGY, "Open Boundary Edges",
                "count=${state.openBoundaryEdges}, max=$max",
                if (state.openBoundaryEdges <= max) MetricStatus.PASS else MetricStatus.FAIL)
        }
        rules.maxNonManifoldEdges?.let { max ->
            out += RuleCheck("topology.nonManifoldEdges", RuleGroup.TOPOLOGY, "Non-Manifold Edges",
                "count=${state.nonManifoldEdges}, max=$max",
                if (state.nonManifoldEdges <= max) MetricStatus.PASS else MetricStatus.FAIL)
        }
        intRangeStatus(rules.minSolids, rules.maxSolids, topo.solids)?.let {
            out += RuleCheck("topology.solids", RuleGroup.TOPOLOGY, "Solid Count",
                "count=${topo.solids}, range=[${rules.minSolids ?: "-"}, ${rules.maxSolids ?: "-"}]", it)
        }
        intRangeStatus(rules.minFaces, rules.maxFaces, topo.faces)?.let {
            out += RuleCheck("topology.faces", RuleGroup.TOPOLOGY, "Face Count",
                "count=${topo.faces}, range=[${rules.minFaces ?: "-"}, ${rules.maxFaces ?: "-"}]", it)
        }
    }

    private fun evaluateMassProperties(rules: MassPropertyRules, mass: MassProperties, out: MutableList<RuleCheck>) {
        doubleRangeStatus(rules.minVolume, rules.maxVolume, mass.volume)?.let {
            out += RuleCheck("mass.volume", RuleGroup.MASS_PROPERTIES, "Volume",
                "value=%.2f mm³, range=[${rules.minVolume ?: "-"}, ${rules.maxVolume ?: "-"}]".format(mass.volume), it)
        }
        doubleRangeStatus(rules.minSurfaceArea, rules.maxSurfaceArea, mass.surfaceArea)?.let {
            out += RuleCheck("mass.surfaceArea", RuleGroup.MASS_PROPERTIES, "Surface Area",
                "value=%.2f mm², range=[${rules.minSurfaceArea ?: "-"}, ${rules.maxSurfaceArea ?: "-"}]".format(mass.surfaceArea), it)
        }
        doubleRangeStatus(rules.minTotalEdgeLength, rules.maxTotalEdgeLength, mass.totalEdgeLength)?.let {
            out += RuleCheck("mass.edgeLength", RuleGroup.MASS_PROPERTIES, "Total Edge Length",
                "value=%.2f mm, range=[${rules.minTotalEdgeLength ?: "-"}, ${rules.maxTotalEdgeLength ?: "-"}]".format(mass.totalEdgeLength), it)
        }
    }

    private fun evaluateTolerances(rules: ToleranceRules, tol: ToleranceStatistics, out: MutableList<RuleCheck>) {
        rules.maxVertexTolerance?.let { limit ->
            out += RuleCheck("tolerance.vertex", RuleGroup.TOLERANCE, "Vertex Tolerance",
                "max=%.2e mm, limit=%.2e mm".format(tol.maxVertexTol, limit),
                if (tol.maxVertexTol <= limit) MetricStatus.PASS else MetricStatus.FAIL)
        }
        rules.maxEdgeTolerance?.let { limit ->
            out += RuleCheck("tolerance.edge", RuleGroup.TOLERANCE, "Edge Tolerance",
                "max=%.2e mm, limit=%.2e mm".format(tol.maxEdgeTol, limit),
                if (tol.maxEdgeTol <= limit) MetricStatus.PASS else MetricStatus.FAIL)
        }
        rules.maxFaceTolerance?.let { limit ->
            out += RuleCheck("tolerance.face", RuleGroup.TOLERANCE, "Face Tolerance",
                "max=%.2e mm, limit=%.2e mm".format(tol.maxFaceTol, limit),
                if (tol.maxFaceTol <= limit) MetricStatus.PASS else MetricStatus.FAIL)
        }
    }

    private data class GeometryResult(
        val checks: List<RuleCheck>,
        val faceStatuses: Map<Int, MetricStatus>,
        val edgeStatuses: Map<Int, MetricStatus>
    )

    private fun evaluateGeometry(rules: GeometryRules, data: OcctInspectionData): GeometryResult {
        val checks = mutableListOf<RuleCheck>()
        val faceStatuses = mutableMapOf<Int, MetricStatus>()
        val edgeStatuses = mutableMapOf<Int, MetricStatus>()

        rules.allowedSurfaceTypes?.let { allowed ->
            var failed = 0
            data.faces.forEach { face ->
                val status = if (face.surfaceType.name in allowed) MetricStatus.PASS else MetricStatus.FAIL
                faceStatuses[face.faceId] = status
                if (status == MetricStatus.FAIL) failed++
            }
            checks += RuleCheck("geometry.surfaceTypes", RuleGroup.GEOMETRY, "Allowed Surface Types",
                "$failed/${data.faces.size} faces outside {${allowed.joinToString()}}",
                if (failed == 0) MetricStatus.PASS else MetricStatus.FAIL)
        }

        rules.allowedCurveTypes?.let { allowed ->
            var failed = 0
            data.edges.forEach { edge ->
                val status = if (edge.curveType.name in allowed) MetricStatus.PASS else MetricStatus.FAIL
                edgeStatuses[edge.edgeId] = status
                if (status == MetricStatus.FAIL) failed++
            }
            checks += RuleCheck("geometry.curveTypes", RuleGroup.GEOMETRY, "Allowed Curve Types",
                "$failed/${data.edges.size} edges outside {${allowed.joinToString()}}",
                if (failed == 0) MetricStatus.PASS else MetricStatus.FAIL)
        }

        return GeometryResult(checks, faceStatuses, edgeStatuses)
    }

    private fun intRangeStatus(min: Int?, max: Int?, value: Int): MetricStatus? {
        if (min == null && max == null) return null
        val ok = (min == null || value >= min) && (max == null || value <= max)
        return if (ok) MetricStatus.PASS else MetricStatus.FAIL
    }

    private fun doubleRangeStatus(min: Double?, max: Double?, value: Double): MetricStatus? {
        if (min == null && max == null) return null
        val ok = (min == null || value >= min) && (max == null || value <= max)
        return if (ok) MetricStatus.PASS else MetricStatus.FAIL
    }
}