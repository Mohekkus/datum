package cc.shinemoon.datum.viewmodel

import cc.shinemoon.datum.model.preset.PresetModel
import cc.shinemoon.datum.usecase.DatabaseUseCase

class FakeDatabaseUseCase : DatabaseUseCase {
    private val storage = mutableMapOf<String, PresetModel>()
    var closed = false
        private set

    override suspend fun get(name: String): PresetModel? = storage[name]

    override suspend fun getAllNames(): List<String> = storage.keys.toList()

    override suspend fun add(name: String, preset: PresetModel) {
        storage[name] = preset
    }

    override suspend fun update(name: String, preset: PresetModel): Boolean {
        if (!storage.containsKey(name)) return false
        storage[name] = preset
        return true
    }

    override suspend fun delete(name: String) {
        storage.remove(name)
    }

    override suspend fun close() {
        closed = true
    }
}
