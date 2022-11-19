package com.arcticoss.nextplayer.core.database.daos

import androidx.room.*
import com.arcticoss.nextplayer.core.database.entities.FolderEntity
import com.arcticoss.nextplayer.core.database.relations.FolderAndMediaItemRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folderEntity: FolderEntity): Long

    @Delete
    suspend fun delete(folderEntity: FolderEntity)

    @Query("SELECT id FROM folder WHERE path = :path")
    suspend fun id(path: String): Long

    @Query("SELECT EXISTS(SELECT * FROM folder WHERE path = :path )")
    suspend fun isExist(path: String): Boolean

    @Transaction
    @Query("SELECT * FROM folder")
    fun getFolderAndMediaItemStream(): Flow<List<FolderAndMediaItemRelation>>

    @Transaction
    @Query("SELECT * FROM folder WHERE id = :folderId")
    fun getFolderAndMediaItemStream(folderId: Long): Flow<FolderAndMediaItemRelation>

}