package com.arcticoss.nextplayer.core.datastore.datasource

import android.util.Log
import androidx.datastore.core.DataStore
import com.arcticoss.model.MediaPreferences
import javax.inject.Inject

private const val TAG = "MediaPreferencesDataSou"

class MediaPreferencesDataSource @Inject constructor(
    private val mediaPreferences: DataStore<MediaPreferences>
) {

    val mediaPrefStream = mediaPreferences.data

    suspend fun updateMediaPreferences(mediaPref: MediaPreferences) {
        mediaPreferences.updateData {
            Log.d(TAG, "updateMediaPreferences: MediaScreen")
            it.copy(
                lastPlayedVideo = mediaPref.lastPlayedVideo,
                viewOption = mediaPref.viewOption,
                showHidden = mediaPref.showHidden,
                sortOrder = mediaPref.sortOrder,
                sortBy = mediaPref.sortBy
            )
        }
    }

}