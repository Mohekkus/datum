package cc.shinemoon.datumabase.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import java.io.File

internal object DatabaseEngine {

    private var db: RoomDatabase? = null

    inline fun <reified T: RoomDatabase> getDatabase(): T {
        return db as? T ?: initializeDatabase(getDatabaseBuilder<T>())
    }

    private inline fun <reified T: RoomDatabase> getDatabaseBuilder(): RoomDatabase.Builder<T> {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "datumabase.db")
        return Room.databaseBuilder<T>(
            name = dbFile.absolutePath,
        )
    }

    private inline fun <reified T: RoomDatabase> initializeDatabase(dbBuilder: RoomDatabase.Builder<T>): T {
        return dbBuilder
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}