package cc.shinemoon.datum.usecase

import cc.shinemoon.datum.model.preset.PresetModel

interface DatabaseUseCase {

    suspend fun get(name: String): PresetModel?
    suspend fun getAllNames(): List<String>

    suspend fun add(name: String, preset: PresetModel)
//    suspend fun update(name: String, preset: PresetModel)
    suspend fun delete(name: String)
}