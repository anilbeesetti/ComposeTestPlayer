package com.arcticoss.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arcticoss.database.daos.*
import com.arcticoss.database.entities.*

@Database(
    entities = [
        MediaItemEntity::class,
        FolderEntity::class,
        ThumbnailEntity::class,
        VideoTrackEntity::class,
        AudioTrackEntity::class,
        SubtitleTrackEntity::class
    ],
    version = 1
)
abstract class MediaDatabase: RoomDatabase() {

    abstract fun folderDao(): FolderDao
    abstract fun thumbnailDao(): ThumbnailDao
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun videoTrackDao(): VideoTrackDao
    abstract fun audioTrackDao(): AudioTrackDao
    abstract fun subtitleTrackDao(): SubtitleTrackDao

    companion object {
        const val DATABASE_NAME = "media_db"
    }
}