package cc.shinemoon.datumabase

interface DbInterface {

    suspend fun get(name: String): String?
    suspend fun getPresetListNames(): List<String>

    suspend fun add(name: String, presetString: String)
    suspend fun delete(name: String)

}