package com.sanmer.mrepo.ui.utils

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.theme.AppTheme

@Composable
fun Logo(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    backgroundColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .shadow(elevation = 10.dp)
            .background(color = backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize(0.6f),
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = contentColor
        )
    }
}

@Preview(
    name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Composable
private fun LogoPreview() {
    AppTheme {
        Logo(
            modifier = Modifier
                .size(40.dp),
            iconRes = R.drawable.ic_logo
        )
    }
}