package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.NasaDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.getDateSeventhDay
import com.udacity.asteroidradar.database.getDateToday
import com.udacity.asteroidradar.models.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainRepository(
    private val database: NasaDatabase
) {

    val asteroidsWeek: LiveData<List<Asteroid>> =
        Transformations.map(
            database.nasaDao.getWeeklyAsteroids(
                getDateToday(),
                getDateSeventhDay()
            )
        ) {
            it.asDomainModel()
        }

    val asteroidsToday: LiveData<List<Asteroid>> =
        Transformations.map(database.nasaDao.geTodayAsteroids(getDateToday())) {
            it.asDomainModel()
        }

    val asteroidsSaved: LiveData<List<Asteroid>> =
        Transformations.map(database.nasaDao.getAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidList = parseAsteroidsJsonResult(
                    JSONObject(
                        NasaApi.retrofitService.getAsteroidsAsync(
                            getDateToday(),
                            getDateSeventhDay()
                        )
                            .await()
                    )
                )
                database.nasaDao.insertAll(*asteroidList.asDatabaseModel())
            } catch (e: Exception) {
                Log.e("MainRepository", e.message.toString())
            }
        }
    }

    suspend fun getPictureOfDay() = withContext(Dispatchers.IO) {
        NasaApi.retrofitService.getPictureOfDayAsync().await()
    }

    fun deleteOldAsteroids() {
        database.nasaDao.deleteAll()
    }

}
