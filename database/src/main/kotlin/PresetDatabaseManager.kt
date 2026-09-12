package cc.shinemoon.datumabase

import cc.shinemoon.datumabase.database.AppDatabase
import cc.shinemoon.datumabase.database.DatabaseInitializer
import cc.shinemoon.datumabase.database.dao.PresetDao
import cc.shinemoon.datumabase.database.entities.PresetEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresetDatabaseManager @Inject constructor(
    databaseEngine: DatabaseInitializer,
): DbInterface {

    private val database = databaseEngine.getDatabase<AppDatabase>()

    private fun getPreset(): PresetDao {
        return database.presetDao()
    }

    override suspend fun get(name: String): String? {
        getPreset().getByName(name)?.let {
            return it.rulesString
        } ?: run {
            return null
        }
    }

    override suspend fun getPresetListNames(): List<String> {
        return getPreset().getAll().map { it.name }
    }

    override suspend fun add(name: String, presetString: String) {
        getPreset().insert(
            PresetEntity(
                version = 0.0,
                name = name,
                rulesString = presetString
            )
        )
    }

    override suspend fun delete(name: String) {
        getPreset().delete(name)
    }
}
