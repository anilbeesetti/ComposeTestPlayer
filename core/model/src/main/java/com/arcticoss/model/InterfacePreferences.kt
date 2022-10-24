package com.arcticoss.model

import kotlinx.serialization.Serializable

@Serializable
data class InterfacePreferences(
    val theme: Theme = Theme.FollowSystem,
    val showFloatingButton: Boolean = false,
    val groupVideos: Boolean = false,
    val showHidden: Boolean = false,
    val sortBy: SortBy = SortBy.Title,
    val sortOrder: SortOrder = SortOrder.Ascending
)

enum class Theme(val title: String) {
    Dark(title = "Dark"),
    Light(title = "Light"),
    FollowSystem(title = "Follow system");
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
