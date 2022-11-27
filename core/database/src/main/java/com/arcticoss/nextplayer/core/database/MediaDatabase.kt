package com.arcticoss.nextplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arcticoss.nextplayer.core.database.daos.AudioTrackDao
import com.arcticoss.nextplayer.core.database.daos.FolderDao
import com.arcticoss.nextplayer.core.database.daos.MediaDao
import com.arcticoss.nextplayer.core.database.daos.SubtitleTrackDao
import com.arcticoss.nextplayer.core.database.daos.ThumbnailDao
import com.arcticoss.nextplayer.core.database.daos.VideoTrackDao
import com.arcticoss.nextplayer.core.database.entities.AudioTrackEntity
import com.arcticoss.nextplayer.core.database.entities.FolderEntity
import com.arcticoss.nextplayer.core.database.entities.MediaEntity
import com.arcticoss.nextplayer.core.database.entities.SubtitleTrackEntity
import com.arcticoss.nextplayer.core.database.entities.ThumbnailEntity
import com.arcticoss.nextplayer.core.database.entities.VideoTrackEntity

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
abstract class MediaDatabase : RoomDatabase() {

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