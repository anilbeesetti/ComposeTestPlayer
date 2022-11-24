package com.arcticoss.nextplayer.core.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arcticoss.nextplayer.core.database.entities.ThumbnailEntity

@Dao
interface ThumbnailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(thumbnailEntity: ThumbnailEntity)

    @Delete
    suspend fun delete(thumbnailEntity: ThumbnailEntity)

    @Query("DELETE FROM thumbnail WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT EXISTS(SELECT * FROM thumbnail WHERE media_id = :mediaId )")
    suspend fun isExist(mediaId: Long): Boolean


    @Query("SELECT * FROM thumbnail")
    suspend fun getThumbnailEntities(): List<ThumbnailEntity>

}