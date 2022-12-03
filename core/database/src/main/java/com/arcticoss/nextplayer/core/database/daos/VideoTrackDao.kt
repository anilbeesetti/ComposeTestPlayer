package com.arcticoss.nextplayer.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arcticoss.nextplayer.core.database.entities.VideoTrackEntity

@Dao
interface VideoTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(videoTrackEntity: VideoTrackEntity)

    @Query("SELECT EXISTS(SELECT * FROM video_track WHERE media_id = :id )")
    suspend fun isExist(id: Long): Boolean

}