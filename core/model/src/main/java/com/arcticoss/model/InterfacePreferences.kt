package com.arcticoss.model

import kotlinx.serialization.Serializable

@Serializable
data class InterfacePreferences(
    val theme: Theme = Theme.FollowSystem,
    val showFloatingButton: Boolean = false,
    val groupVideos: Boolean = false,
    val tagNew: TagNew = TagNew(),
    val viewOption: ViewOption = ViewOption.Videos,
    val showHidden: Boolean = false,
    val sortBy: SortBy = SortBy.Title,
    val sortOrder: SortOrder = SortOrder.Ascending
)

@Serializable
data class TagNew(
    val show: Boolean = false,
    val period: Int = 7
)

enum class Theme {
    Dark, Light, FollowSystem
}

enum class ViewOption {
    Videos, Folders
}

enum class SortBy {
    Title, Length
}

enum class SortOrder {
    Ascending, Descending
}
