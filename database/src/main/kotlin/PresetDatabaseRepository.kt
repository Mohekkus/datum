package cc.shinemoon.datumabase

import cc.shinemoon.datumabase.database.dao.PresetDao
import cc.shinemoon.datumabase.database.entities.PresetEntity
import cc.shinemoon.datumabase.model.preset.PresetModel
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PresetDatabaseRepository @Inject constructor(): DbInterface {

    private fun getPreset(): PresetDao {
        return getDatabase().presetDao()
    }

    override suspend fun get(name: String): PresetModel? {
        getPreset().getByName(name)?.let {
            return Json.decodeFromString<PresetModel>(it.rulesString)
        } ?: run {
            return null
        }
    }

    override suspend fun getPresetListNames(): List<String> {
        return getPreset().getAll().map { it.name }
    }

    override suspend fun add(name: String, preset: PresetModel) {
        getPreset().insert(
            PresetEntity(
                version = 0.0,
                name = name,
                rulesString = Json.encodeToString(preset)
            )
        )
    }

    override suspend fun delete(name: String) {
        getPreset().delete(name)
    }


}