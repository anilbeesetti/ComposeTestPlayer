package com.arcticoss.nextplayer.core.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arcticoss.nextplayer.core.database.entities.LocalSubtitleEntity

@Dao
interface LocalSubtitleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(localSubtitleEntity: LocalSubtitleEntity)

    @Delete
    suspend fun delete(localSubtitleEntity: LocalSubtitleEntity)

    @Query("SELECT EXISTS(SELECT * FROM local_subtitle_track WHERE path = :path )")
    suspend fun isExist(path: String): Boolean

    @Query("SELECT * FROM local_subtitle_track")
    suspend fun getLocalSubtitleEntities(): List<LocalSubtitleEntity>

}