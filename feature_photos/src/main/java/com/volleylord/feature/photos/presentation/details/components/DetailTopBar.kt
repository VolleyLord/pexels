package com.volleylord.feature.photos.presentation.details.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volleylord.core.ui.components.BackButton

@Composable
fun DetailTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    topBarHeight: Int = 40,
    backBtnSize: Int = 40,
    titleInset: Int = 57,
    titleFontSize: Int = 18
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(topBarHeight.dp),
        contentAlignment = Alignment.Center
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = titleFontSize.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
        BackButton(
            onClick = onBackClick,
            size = backBtnSize,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

