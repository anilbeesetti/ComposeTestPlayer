package com.arcticoss.nextplayer.media.ui.mediascreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcticoss.nextplayer.R
import com.arcticoss.nextplayer.media.ui.mediascreen.composables.MediaLargeTopAppBar
import com.arcticoss.nextplayer.media.ui.mediascreen.composables.ShowVideoFiles
import com.arcticoss.nextplayer.utils.PermissionUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun MediaScreen(
    viewModel: VideoFilesViewModel = viewModel(),
) {
    val videoFiles by viewModel.videoFiles.collectAsStateWithLifecycle()
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            MediaLargeTopAppBar(
                title = "NextPlayer",
                scrollBehavior = scrollBehaviour
            )
        }
    ) { innerPadding ->
        if (PermissionUtils.hasPermission()) {
            ShowVideoFiles(
                videoFiles = videoFiles,
                contentPadding = innerPadding
            )
        } else {
            ShowPermissionInfo(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun ShowPermissionInfo(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(130.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = R.string.permission_not_granted),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 5.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = R.string.permission_info),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 5.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { PermissionUtils.grantPermission(context) }) {
            Text(
                text = stringResource(id = R.string.grant_permission),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}
