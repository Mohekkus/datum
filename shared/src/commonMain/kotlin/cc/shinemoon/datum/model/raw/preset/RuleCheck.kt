package cc.shinemoon.datum.model.raw.preset

data class RuleCheck(
    val id: String,
    val group: RuleGroup,
    val label: String,
    val detail: String,
    val status: MetricStatus
)