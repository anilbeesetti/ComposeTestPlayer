package com.arcticoss.nextplayer.feature.media.composables

import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.feature.media.utils.TimeUtils
import com.arcticoss.model.MediaItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaListItem(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
            )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth(0.45f)
                    .aspectRatio(16f / 10f)
            ) {
//                TODO: "Images make lazy column laggy"
//                if (mediaItem.thumbnailPath.isNotEmpty()) {
//                    Image(
//                        bitmap = remember {
//                            BitmapFactory.decodeFile(mediaItem.thumbnailPath).asImageBitmap()
//                        },
//                        contentDescription = "",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = mediaItem.title,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FieldChip(text = TimeUtils.formatTime(context, mediaItem.duration / 1000))
                }
            }
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(18.dp)
                .clearAndSetSemantics { },
            onClick = { /*TODO*/ }
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = ""
            )
        }
    }
}

@Composable
fun FieldChip(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    color: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraSmall.copy(CornerSize(2.dp)))
            .background(backgroundColor)
            .padding(
                horizontal = 4.dp,
                vertical = 1.dp
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
            color = color
        )
    }
}