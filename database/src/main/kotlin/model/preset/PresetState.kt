package cc.shinemoon.datumabase.model.preset

data class PresetState(
    val preset: PresetModel = PresetModel(),
    val evaluation: PresetEvaluation? = null,
)
