package com.arcticoss.nextplayer.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.arcticoss.model.MediaPreferences
import javax.inject.Inject

class MediaPreferencesDataSource @Inject constructor(
    private val mediaPreferences: DataStore<MediaPreferences>
) {

    val mediaPrefStream = mediaPreferences.data

    suspend fun updateUiPref(mediaPref: MediaPreferences) {
        mediaPreferences.updateData {
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