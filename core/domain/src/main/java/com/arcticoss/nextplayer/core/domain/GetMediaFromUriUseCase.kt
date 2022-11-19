package com.arcticoss.nextplayer.core.domain

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import com.arcticoss.nextplayer.core.data.repository.MediaRepository
import com.arcticoss.model.Media
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetMediaFromUriUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    @ApplicationContext private val context: Context
) {

    suspend operator fun invoke(uri: Uri): Media? {
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        val loader = CursorLoader(context, uri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        return cursor?.let {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            it.moveToFirst()
            val result = it.getString(columnIndex)
            it.close()
            mediaRepository.getMedia(result)
        }
    }
}