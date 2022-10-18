package com.arcticoss.database.daos

import androidx.room.*
import com.arcticoss.database.entities.MediaItemEntity
import com.arcticoss.database.relations.MediaItemAndThumbnailRelation
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
    fun getMediaItemEntities(): List<MediaItemEntity>

    @Transaction
    @Query("SELECT * From media")
    fun getMediaItemEntitiesStream(): Flow<List<MediaItemAndThumbnailRelation>>

}