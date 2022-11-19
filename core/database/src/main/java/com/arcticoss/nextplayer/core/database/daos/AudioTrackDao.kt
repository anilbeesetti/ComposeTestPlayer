package com.arcticoss.nextplayer.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.arcticoss.nextplayer.core.database.entities.AudioTrackEntity

@Dao
interface AudioTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(audioTrackEntity: AudioTrackEntity)

}