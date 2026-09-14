package cc.shinemoon.datumabase.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import cc.shinemoon.datumabase.database.entities.PresetEntity

@Dao
internal interface PresetDao {

    @Query("select * from user_preset")
    suspend fun getAll(): List<PresetEntity>

    @Query("select * from user_preset where id = :id")
    suspend fun getById(id: Long): PresetEntity

    @Query("select * from user_preset where name = :name")
    suspend fun getByName(name: String): PresetEntity?

    @Query("select count(*) from user_preset")
    suspend fun count(): Long

    @Insert
    suspend fun insert(preset: PresetEntity)

    @Update
    suspend fun update(preset: PresetEntity)

    @Query("update user_preset set rulesString = :presetString, version = :version where name = :name")
    suspend fun updateByName(name: String, presetString: String, version: Double): Int

    @Delete
    suspend fun delete(preset: PresetEntity)

    @Query("delete from user_preset where name = :name")
    suspend fun delete(name: String)
}
