package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import com.arcticoss.nextplayer.core.model.ResizeMode

/**
 * Controls how video and album art is resized.
 */
internal val ResizeMode.contentScale
    get() = when (this) {
        ResizeMode.FitScreen -> ContentScale.Fit
        ResizeMode.FixedWidth -> ContentScale.FillWidth
        ResizeMode.FixedHeight -> ContentScale.FillHeight
        ResizeMode.Fill -> ContentScale.FillBounds
        ResizeMode.Zoom -> ContentScale.Crop
    }

internal fun Modifier.resize(
    aspectRatio: Float,
    resizeMode: ResizeMode
) = when (resizeMode) {
    ResizeMode.FitScreen -> aspectRatio(aspectRatio)
    ResizeMode.Fill -> fillMaxSize()
    ResizeMode.FixedWidth -> fixedWidth(aspectRatio)
    ResizeMode.FixedHeight -> fixedHeight(aspectRatio)
    ResizeMode.Zoom -> zoom(aspectRatio)
}

private fun Modifier.fixedWidth(
    aspectRatio: Float
) = clipToBounds()
    .fillMaxWidth()
    .wrapContentHeight(unbounded = true)
    .aspectRatio(aspectRatio)

private fun Modifier.fixedHeight(
    aspectRatio: Float
) = clipToBounds()
    .fillMaxHeight()
    .wrapContentWidth(unbounded = true)
    .aspectRatio(aspectRatio)

private fun Modifier.zoom(
    aspectRatio: Float
) = clipToBounds()
    .layout { measurable, constraints ->
        val maxWidth = constraints.maxWidth
        val maxHeight = constraints.maxHeight
        if (aspectRatio > maxWidth.toFloat() / maxHeight) {

            // wrap width unbounded
            val modifiedConstraints = constraints.copy(maxWidth = Constraints.Infinity)

            val placeable = measurable.measure(modifiedConstraints)

            layout(constraints.maxWidth, placeable.height) {
                val offsetX = Alignment.CenterHorizontally
                    .align(
                        size = 0,
                        space = constraints.maxWidth - placeable.width,
                        layoutDirection = layoutDirection
                    )
                placeable.place(IntOffset(offsetX, 0))
            }
        } else {

            // wrap height unbounded
            val modifiedConstraints = constraints.copy(maxHeight = Constraints.Infinity)

            val placeable = measurable.measure(modifiedConstraints)

            layout(placeable.width, constraints.maxHeight) {
                val offsetY = Alignment.CenterVertically
                    .align(
                        size = 0,
                        space = constraints.maxHeight - placeable.height
                    )
                placeable.place(IntOffset(0, offsetY))
            }
        }
    }
    .aspectRatio(aspectRatio)