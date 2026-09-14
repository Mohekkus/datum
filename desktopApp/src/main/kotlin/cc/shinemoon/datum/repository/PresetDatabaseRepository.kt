package cc.shinemoon.datum.repository

import cc.shinemoon.datum.usecase.DatabaseUseCase
import cc.shinemoon.datumabase.PresetDatabaseManager
import cc.shinemoon.datum.model.preset.PresetModel
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresetDatabaseRepository @Inject constructor(
    private val manager: PresetDatabaseManager,
): DatabaseUseCase {

    override suspend fun get(name: String): PresetModel? {
        manager.get(name)?.let {
            return Json.decodeFromString<PresetModel>(it)
        } ?: return null
    }

    override suspend fun getAllNames(): List<String> = manager.getPresetListNames()

    override suspend fun add(name: String, preset: PresetModel) {
        manager.add(name, Json.encodeToString(preset))
    }

    override suspend fun update(name: String, preset: PresetModel): Boolean {
        return manager.update(name, Json.encodeToString(preset))
    }

    override suspend fun delete(name: String) {
        manager.delete(name)
    }

    override suspend fun close() {
        manager.close()
    }
}
