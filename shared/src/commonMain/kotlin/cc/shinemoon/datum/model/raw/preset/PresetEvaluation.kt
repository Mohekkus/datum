package cc.shinemoon.datum.model.raw.preset

data class PresetEvaluation(
    val presetName: String,
    val checks: List<RuleCheck>,
    val edgeStatuses: Map<Int, MetricStatus> = emptyMap(),
    val faceStatuses: Map<Int, MetricStatus> = emptyMap(),
) {
    val passedCount: Int get() = checks.count { it.status == MetricStatus.PASS }
    val overall: MetricStatus get() =
        if (checks.any { it.status == MetricStatus.FAIL }) MetricStatus.FAIL else MetricStatus.PASS
    fun groupChecks(group: RuleGroup): List<RuleCheck> = checks.filter { it.group == group }
    fun checkStatus(id: String): MetricStatus? = checks.firstOrNull { it.id == id }?.status
}
