package cc.shinemoon.datum.model.preset

import cc.shinemoon.datum.types.preset.MetricStatus
import cc.shinemoon.datum.types.preset.RuleGroup

data class RuleCheck(
    val id: String,
    val group: RuleGroup,
    val label: String,
    val detail: String,
    val status: MetricStatus
)