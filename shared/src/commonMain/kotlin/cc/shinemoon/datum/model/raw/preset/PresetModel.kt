package cc.shinemoon.datum.model.raw.preset

import cc.shinemoon.datum.model.raw.GeometryRules
import cc.shinemoon.datum.model.raw.MassPropertyRules
import cc.shinemoon.datum.model.raw.ToleranceRules
import cc.shinemoon.datum.model.raw.TopologyRules
import kotlinx.serialization.Serializable

@Serializable
data class PresetModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "Custom",
    val topologyRules: TopologyRules = TopologyRules(),
    val massPropertyRules: MassPropertyRules = MassPropertyRules(),
    val toleranceRules: ToleranceRules = ToleranceRules(),
    val geometryRules: GeometryRules = GeometryRules()
)