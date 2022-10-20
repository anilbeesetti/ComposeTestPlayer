package com.arcticoss.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerUiPreferences(
    val brightnessLevel: Int = 15
)
