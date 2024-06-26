package dev.sanmer.mrepo.datastore

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import dev.sanmer.mrepo.app.Const
import dev.sanmer.mrepo.compat.BuildCompat
import dev.sanmer.mrepo.ui.theme.Colors
import java.io.File
import java.io.InputStream
import java.io.OutputStream

@Immutable
data class UserPreferencesCompat(
    val workingMode: WorkingMode,
    val darkMode: DarkMode,
    val themeColor: Int,
    val deleteZipFile: Boolean,
    val downloadPath: File,
    val repositoryMenu: RepositoryMenuCompat,
    val modulesMenu: ModulesMenuCompat
) {
    constructor(original: UserPreferences) : this(
        workingMode = original.workingMode,
        darkMode = original.darkMode,
        themeColor = original.themeColor,
        deleteZipFile = original.deleteZipFile,
        downloadPath = original.downloadPath.ifEmpty{ Const.PUBLIC_DOWNLOADS.absolutePath }.let(::File),
        repositoryMenu = when {
            original.hasRepositoryMenu() -> RepositoryMenuCompat(original.repositoryMenu)
            else -> RepositoryMenuCompat.default()
        },
        modulesMenu = when {
            original.hasModulesMenu() -> ModulesMenuCompat(original.modulesMenu)
            else -> ModulesMenuCompat.default()
        }
    )

    @Composable
    fun isDarkMode() = when (darkMode) {
        DarkMode.AlwaysOff -> false
        DarkMode.AlwaysOn -> true
        else -> isSystemInDarkTheme()
    }

    fun writeTo(output: OutputStream) = UserPreferences.newBuilder()
        .setWorkingMode(workingMode)
        .setDarkMode(darkMode)
        .setThemeColor(themeColor)
        .setDeleteZipFile(deleteZipFile)
        .setDownloadPath(downloadPath.path)
        .setRepositoryMenu(repositoryMenu.toProto())
        .setModulesMenu(modulesMenu.toProto())
        .build()
        .writeTo(output)

    companion object {
        fun from(input: InputStream) = UserPreferencesCompat(
            UserPreferences.parseFrom(input)
        )

        fun default() = UserPreferencesCompat(
            workingMode = WorkingMode.Setup,
            darkMode = DarkMode.FollowSystem,
            themeColor = if (BuildCompat.atLeastS) Colors.Dynamic.id else Colors.Pourville.id,
            deleteZipFile = false,
            downloadPath = Const.PUBLIC_DOWNLOADS,
            repositoryMenu = RepositoryMenuCompat.default(),
            modulesMenu = ModulesMenuCompat.default()
        )

        val WorkingMode.isRoot: Boolean get() {
            return this == WorkingMode.Superuser ||this == WorkingMode.Shizuku
        }

        val WorkingMode.isNonRoot: Boolean get() {
            return this == WorkingMode.None
        }

        val WorkingMode.isSetup: Boolean get() {
            return this == WorkingMode.Setup
        }
    }
}