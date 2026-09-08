package cc.shinemoon.datumabase.model.preset

import cc.shinemoon.datumabase.model.utility.MetricStatus
import cc.shinemoon.datumabase.model.utility.RuleGroup

data class RuleCheck(
    val id: String,
    val group: RuleGroup,
    val label: String,
    val detail: String,
    val status: MetricStatus
)