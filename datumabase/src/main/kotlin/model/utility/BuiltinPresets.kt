package cc.shinemoon.datumabase.model.utility

import cc.shinemoon.datumabase.model.data.GeometryRules
import cc.shinemoon.datumabase.model.data.ToleranceRules
import cc.shinemoon.datumabase.model.data.TopologyRules
import cc.shinemoon.datumabase.model.preset.PresetModel

object BuiltinPresets {

    val analyticSolid: PresetModel = PresetModel(
        id = "builtin-analytic-solid",
        name = "Analytic solid check",
        description = "Watertight, valid solids built from analytic surfaces only. A good starting point for machined parts.",
        category = "Built-in",
        topologyRules = TopologyRules(
            requireValidBRep = true,
            requireClosedSolid = true,
            maxOpenBoundaryEdges = 0,
            maxNonManifoldEdges = 0,
            minSolids = 1,
        ),
        toleranceRules = ToleranceRules(
            maxVertexTolerance = 0.01,
            maxEdgeTolerance = 0.01,
            maxFaceTolerance = 0.05,
        ),
        geometryRules = GeometryRules(
            allowedSurfaceTypes = listOf("PLANE", "CYLINDER", "CONE", "SPHERE", "TORUS"),
            allowedCurveTypes = listOf("LINE", "CIRCLE"),
        ),
    )

    val basicValidity: PresetModel = PresetModel(
        id = "builtin-basic-validity",
        name = "Basic validity",
        description = "Only requires a valid B-Rep with no non-manifold edges.",
        category = "Built-in",
        topologyRules = TopologyRules(
            requireValidBRep = true,
            maxNonManifoldEdges = 0,
        ),
    )

    val all: List<PresetModel> = listOf(analyticSolid, basicValidity)
}