package com.arcticoss.nextplayer.core.database.daos

import androidx.room.*
import com.arcticoss.nextplayer.core.database.entities.MediaItemEntity
import com.arcticoss.nextplayer.core.database.relations.MediaItemAndThumbnailRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mediaItemEntity: MediaItemEntity): Long

    @Delete
    suspend fun delete(mediaItemEntity: MediaItemEntity)

    @Update
    suspend fun update(mediaItemEntity: MediaItemEntity)

    @Query("SELECT * FROM media WHERE id = :id")
    suspend fun get(id: Long): MediaItemEntity

    @Transaction
    @Query("SELECT * FROM media WHERE path = :path")
    suspend fun get(path: String): MediaItemAndThumbnailRelation

    @Query("SELECT EXISTS(SELECT * FROM media WHERE path = :path )")
    suspend fun isExist(path: String): Boolean

    @Query("SELECT * FROM media")
    fun getMediaItemEntities(): List<MediaItemEntity>

    @Transaction
    @Query("SELECT * FROM media")
    fun getMediaItemEntitiesStream(): Flow<List<MediaItemAndThumbnailRelation>>

}