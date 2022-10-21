package com.arcticoss.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaPreferences(
    val lastPlayedVideo: String = "",
    val viewOption: ViewOption = ViewOption.Videos,
    val showHidden: Boolean = false,
    val sortOrder: SortOrder = SortOrder.Ascending,
    val sortBy: SortBy = SortBy.Title
)

//enum class ViewOption {
//    Videos, Folders
//}
//
//enum class SortBy {
//    Title, Length
//}
//
//enum class SortOrder {
//    Ascending, Descending
//}
