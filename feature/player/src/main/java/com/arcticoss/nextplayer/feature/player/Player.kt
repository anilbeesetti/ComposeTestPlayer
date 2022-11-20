package com.arcticoss.nextplayer.feature.player

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class NextPlayerHelper @Inject constructor(private val player: Player): IPlayerHelper {
    override val exoPlayer: ExoPlayer
        get() = player as ExoPlayer

    override val currentPositionFlow: Flow<Long>
        get() = flow {
            while (true) {
                emit(exoPlayer.currentPosition)
                delay(1.seconds / 30)
            }
        }

    override fun getTrackGroupFromFormatId(trackType: Int, id: String):  Tracks.Group? {
        for (group in exoPlayer.currentTracks.groups) {
            if (group.type == trackType) {
                val trackGroup = group.mediaTrackGroup
                val format: Format = trackGroup.getFormat(0)
                if (Objects.equals(id, format.id)) {
                    return group
                }
            }
        }
        return null
    }

    override fun moveToMediaItem(index: Int) {
        if (index > exoPlayer.currentMediaItemIndex) {
            for (i in exoPlayer.currentMediaItemIndex until index) {
                exoPlayer.seekToNextMediaItem()
            }
        } else {
            for (i in index until exoPlayer.currentMediaItemIndex) {
                exoPlayer.seekToPreviousMediaItem()
            }
        }
    }
}


interface IPlayerHelper {

    val exoPlayer: ExoPlayer

    val currentPositionFlow: Flow<Long>

    fun getTrackGroupFromFormatId(trackType: Int, id: String): Tracks.Group?

    fun moveToMediaItem(index: Int)

}