package cc.shinemoon.datumabase.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import cc.shinemoon.datumabase.database.dao.PresetDao
import cc.shinemoon.datumabase.database.entities.PresetEntity

@Database(entities = [PresetEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
}