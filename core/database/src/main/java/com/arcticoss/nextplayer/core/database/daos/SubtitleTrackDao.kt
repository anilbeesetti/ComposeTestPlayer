package com.arcticoss.nextplayer.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.arcticoss.nextplayer.core.database.entities.SubtitleTrackEntity

@Dao
interface SubtitleTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subtitleTrackEntity: SubtitleTrackEntity)

}