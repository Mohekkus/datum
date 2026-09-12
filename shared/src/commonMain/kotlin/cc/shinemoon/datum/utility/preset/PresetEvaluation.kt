package cc.shinemoon.datum.utility.preset

import cc.shinemoon.datum.model.preset.RuleCheck
import cc.shinemoon.datum.types.preset.MetricStatus
import cc.shinemoon.datum.types.preset.RuleGroup

data class PresetEvaluation(
    val presetName: String,
    val checks: List<RuleCheck>,
    val edgeStatuses: Map<Int, MetricStatus> = emptyMap(),
    val faceStatuses: Map<Int, MetricStatus> = emptyMap(),
) {
    val passedCount: Int get() = checks.count { it.status == MetricStatus.PASS }
    val overall: MetricStatus
        get() =
        if (checks.any { it.status == MetricStatus.FAIL }) MetricStatus.FAIL else MetricStatus.PASS
    fun groupChecks(group: RuleGroup): List<RuleCheck> = checks.filter { it.group == group }
    fun groupStatus(group: RuleGroup): MetricStatus? {
        val g = groupChecks(group)
        if (g.isEmpty()) return null
        return if (g.any { it.status == MetricStatus.FAIL }) MetricStatus.FAIL else MetricStatus.PASS
    }
    fun checkStatus(id: String): MetricStatus? = checks.firstOrNull { it.id == id }?.status
}