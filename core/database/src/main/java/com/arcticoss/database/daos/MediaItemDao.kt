package com.arcticoss.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arcticoss.database.entities.MediaItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mediaItemEntity: MediaItemEntity): Long

    @Delete
    suspend fun delete(mediaItemEntity: MediaItemEntity)

    @Query("SELECT EXISTS(SELECT * FROM media WHERE path = :path )")
    suspend fun isExist(path: String): Boolean

    @Query("SELECT * From media")
    fun getMediaItemEntitiesStream(): Flow<List<MediaItemEntity>>

    @Query("SELECT * From media")
    fun getMediaItemEntities(): List<MediaItemEntity>

}