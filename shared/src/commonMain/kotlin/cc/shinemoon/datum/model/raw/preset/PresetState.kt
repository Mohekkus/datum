package cc.shinemoon.datum.model.raw.preset

data class PresetState(
    val preset: PresetModel = PresetModel(),
    val evaluation: PresetEvaluation? = null,
)
