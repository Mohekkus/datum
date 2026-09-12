package cc.shinemoon.datum.model.preset

import contracts.PresetModelAlias
import kotlinx.serialization.Serializable

@Serializable
data class PresetModel(
    var id: String = "",
    var name: String = "",
    val description: String = "",
    val category: String = "Custom",
    val topologyRules: TopologyRules = TopologyRules(),
    val massPropertyRules: MassPropertyRules = MassPropertyRules(),
    val toleranceRules: ToleranceRules = ToleranceRules(),
    val geometryRules: GeometryRules = GeometryRules()
): PresetModelAlias