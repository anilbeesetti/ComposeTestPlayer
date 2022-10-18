package com.arcticoss.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.arcticoss.database.entities.AudioTrackEntity

@Dao
interface AudioTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(audioTrackEntity: AudioTrackEntity)

}