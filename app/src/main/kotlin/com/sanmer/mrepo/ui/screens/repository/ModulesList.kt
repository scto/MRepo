package com.sanmer.mrepo.ui.screens.repository

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.ui.component.FastScrollbar
import com.sanmer.mrepo.ui.navigation.graphs.createViewRoute
import com.sanmer.mrepo.ui.utils.navigateSingleTopTo
import com.sanmer.mrepo.ui.utils.rememberFastScroller
import com.sanmer.mrepo.ui.utils.scrollbarState
import com.sanmer.mrepo.viewmodel.RepositoryViewModel

@Composable
fun ModulesList(
    list: List<OnlineModule>,
    state: LazyListState,
    navController: NavController,
    getModuleState: @Composable (OnlineModule) -> RepositoryViewModel.ModuleState
) = Box(
    modifier = Modifier.fillMaxSize()
) {
    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(
            items = list,
            key = { it.id }
        ) { module ->
            val moduleState = getModuleState(module)

            ModuleItem(
                module = module,
                updatable = moduleState.updatable,
                installed = moduleState.installed,
                onClick = { navController.navigateSingleTopTo(createViewRoute(module)) }
            )
        }
    }

    FastScrollbar(
        modifier = Modifier
            .fillMaxHeight()
            .align(Alignment.CenterEnd),
        state = state.scrollbarState(),
        orientation = Orientation.Vertical,
        scrollInProgress = state.isScrollInProgress,
        onThumbDisplaced = state.rememberFastScroller(),
    )
}