package cc.shinemoon.datumabase.database.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(
    tableName = "user_preset",
)
internal data class PresetEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "rulesString") var rulesString: String,
    @ColumnInfo(name = "version") var version: Double,
)