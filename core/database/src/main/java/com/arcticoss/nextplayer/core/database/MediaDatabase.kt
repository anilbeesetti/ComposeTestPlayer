package com.arcticoss.nextplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arcticoss.nextplayer.core.database.daos.*
import com.arcticoss.nextplayer.core.database.entities.*

@Database(
    entities = [
        MediaEntity::class,
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
    abstract fun mediaItemDao(): MediaDao
    abstract fun videoTrackDao(): VideoTrackDao
    abstract fun audioTrackDao(): AudioTrackDao
    abstract fun subtitleTrackDao(): SubtitleTrackDao

    companion object {
        const val DATABASE_NAME = "media_db"
    }
}