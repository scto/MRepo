package com.sanmer.mrepo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.app.event.State
import com.sanmer.mrepo.app.utils.MediaStoreUtils.absolutePath
import com.sanmer.mrepo.app.utils.MediaStoreUtils.copyTo
import com.sanmer.mrepo.app.utils.MediaStoreUtils.displayName
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.SuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InstallViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val suRepository: SuRepository
) : ViewModel() {
    val console = mutableStateListOf<String>()

    val state = object : State(initial = Event.LOADING) {
        override fun setFailed(value: Any?) {
            value?.let { send(it.toString())}
            super.setFailed(value)
        }
    }

    init {
        Timber.d("InstallViewModel init")
    }

    fun send(message: String) = console.add("- $message")

    private val onSucceeded: (LocalModule) -> Unit = {
        viewModelScope.launch {
            localRepository.insertLocal(it)
            state.setSucceeded()
        }
    }

    fun install(
        context: Context,
        path: Uri,
        deleteZipFile: Boolean
    ) {
        val file = context.cacheDir.resolve("install.zip")
        path.copyTo(file)
        send("Copying zip to temp directory")
        send("Installing ${path.displayName}")

        suRepository.install(
            zipFile = file,
            console = { console.add(it) },
            onSuccess = {
                onSucceeded(it)
                file.delete()
                if (deleteZipFile) {
                    path.delete()
                }
            },
            onFailure = {
                state.setFailed()
                file.delete()
            }
        )
    }

    private fun Uri.delete() = runCatching {
        absolutePath.let {
            suRepository.fs.getFile(it).delete()
        }
    }.onFailure {
        Timber.e(it)
    }
}