package cc.shinemoon.datumabase

import cc.shinemoon.datumabase.database.AppDatabase
import cc.shinemoon.datumabase.database.DatabaseEngine
import cc.shinemoon.datumabase.database.dao.PresetDao
import cc.shinemoon.datumabase.database.entities.PresetEntity
import cc.shinemoon.datumabase.model.preset.PresetModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

internal fun getDatabase(): AppDatabase {
    return DatabaseEngine.getDatabase<AppDatabase>()
}

internal fun getPreset(): PresetDao {
    return getDatabase().presetDao()
}

suspend fun getAllPresets(): MutableList<Pair<String, PresetModel>> {
    return getPreset().getAll().map { entity ->
        entity.name to Json.decodeFromString<PresetModel>(entity.rulesString)
    }.toMutableList()
}

suspend fun getAllPresetNames(): MutableList<String> {
    return getPreset().getAll().map { it.name }.toMutableList()
}

suspend fun getPresetByName(name: String): PresetModel? {
    getPreset().getByName(name)?.let {
        return Json.decodeFromString<PresetModel>(it.rulesString)
    } ?: run {
        return null
    }
}

suspend fun saveIntoPresets(name: String, preset: PresetModel) {
    getPreset().insert(
        PresetEntity(
            version = 0.0,
            name = name,
            rulesString = Json.encodeToString(preset)
        )
    )
}

suspend fun deleteFromPresets(name: String) {
    getPreset().delete(name)
}