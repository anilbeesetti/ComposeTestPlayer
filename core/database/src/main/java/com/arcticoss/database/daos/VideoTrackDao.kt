package com.arcticoss.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.arcticoss.database.entities.VideoTrackEntity

@Dao
interface VideoTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(videoTrackEntity: VideoTrackEntity)

}