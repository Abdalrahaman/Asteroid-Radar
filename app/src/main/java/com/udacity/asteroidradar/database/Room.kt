package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NasaDao {

    @Query("select * from databaseasteroid ORDER BY closeApproachDate ASC")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate > :startDate AND closeApproachDate <= :endDate ORDER BY closeApproachDate ASC")
    fun getWeeklyAsteroids(
        startDate: String,
        endDate: String
    ): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate == :todayDate ORDER BY closeApproachDate ASC")
    fun geTodayAsteroids(
        todayDate: String
    ): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("DELETE FROM databaseasteroid")
    fun deleteAll()
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class NasaDatabase : RoomDatabase() {
    abstract val nasaDao: NasaDao
}

private lateinit var INSTANCE: NasaDatabase

fun getDatabase(context: Context): NasaDatabase {
    synchronized(NasaDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                NasaDatabase::class.java,
                "nasa"
            ).build()
        }
    }
    return INSTANCE
}