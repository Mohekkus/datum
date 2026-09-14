package cc.shinemoon.datumabase.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor() {

    private var db: RoomDatabase? = null

    internal inline fun <reified T: RoomDatabase> getDatabase(): T {
        @Suppress("UNCHECKED_CAST")
        return db as? T ?: initializeDatabase(getDatabaseBuilder<T>()).also {
            db = it
        }
    }

    private inline fun <reified T: RoomDatabase> getDatabaseBuilder(): RoomDatabase.Builder<T> {
        val dbFile = getDatabaseFile()
        return Room.databaseBuilder<T>(
            name = dbFile.absolutePath,
        )
    }

    private fun getDatabaseFile(): File {
        val appDataDir = System.getenv("APPDATA")
            ?.takeIf { it.isNotBlank() }
            ?.let(::File)
            ?: File(System.getProperty("user.home"), ".datum")

        val datumDir = File(appDataDir, "Datum")
        datumDir.mkdirs()

        return File(datumDir, "datumabase.db")
    }

    private inline fun <reified T: RoomDatabase> initializeDatabase(dbBuilder: RoomDatabase.Builder<T>): T {
        return dbBuilder
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
