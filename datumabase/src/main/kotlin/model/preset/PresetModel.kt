package cc.shinemoon.datumabase.model.preset

import cc.shinemoon.datumabase.model.data.GeometryRules
import cc.shinemoon.datumabase.model.data.MassPropertyRules
import cc.shinemoon.datumabase.model.data.ToleranceRules
import cc.shinemoon.datumabase.model.data.TopologyRules
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