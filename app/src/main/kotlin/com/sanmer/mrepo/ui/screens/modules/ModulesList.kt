package com.sanmer.mrepo.ui.screens.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.event.isSucceeded
import com.sanmer.mrepo.model.module.LocalModule
import com.sanmer.mrepo.model.module.State
import com.sanmer.mrepo.ui.component.ModuleCard
import com.sanmer.mrepo.ui.component.stateIndicator
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun ModulesList(
    list: List<LocalModule>,
    state: LazyListState
) = LazyColumn(
    state = state,
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp),
) {
    items(
        items = list,
        key = { it.id }
    ) { module ->
        ModuleItem(module)
    }
}

@Composable
private fun ModuleItem(
    module: LocalModule,
    viewModel: ModulesViewModel = hiltViewModel()
) {
    val uiState = viewModel.rememberLocalModuleState(module)
    val suState by viewModel.suState.collectAsStateWithLifecycle()

    ModuleCard(
        name = module.name,
        version = module.versionDisplay,
        author = module.author,
        description = module.description,
        alpha = uiState.alpha,
        decoration = uiState.decoration,
        switch = {
            Switch(
                checked = module.state == State.ENABLE,
                onCheckedChange = uiState.toggle,
                enabled = suState.isSucceeded
            )
        },
        indicator = when (module.state) {
            State.REMOVE -> stateIndicator(R.drawable.trash_outline)
            State.UPDATE -> stateIndicator(R.drawable.import_outline)
            State.ZYGISK_UNLOADED,
            State.RIRU_DISABLE,
            State.ZYGISK_DISABLE -> stateIndicator(R.drawable.danger_outline)
            else -> null
        },
        buttons = {
            TextButton(
                onClick = uiState.change,
                enabled = suState.isSucceeded
            ) {
                Text(
                    modifier = Modifier.padding(end = 6.dp),
                    text = stringResource(id = if (module.state == State.REMOVE) {
                        R.string.module_restore
                    } else {
                        R.string.module_remove
                    })
                )
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(id = if (module.state == State.REMOVE) {
                        R.drawable.refresh_outline
                    } else {
                        R.drawable.trash_outline
                    }),
                    contentDescription = null
                )
            }
        }
    )
}