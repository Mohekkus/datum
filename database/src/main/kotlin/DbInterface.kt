package cc.shinemoon.datumabase

import cc.shinemoon.datumabase.model.preset.PresetModel

interface DbInterface {

    suspend fun get(name: String): PresetModel?
    suspend fun getPresetListNames(): List<String>

    suspend fun add(name: String, preset: PresetModel)
    suspend fun delete(name: String)

}