package com.example.eventease.presentation.detail.comps

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.eventease.R

@Composable
fun EventHeaderImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.placeholder),
        contentDescription = "Event Header",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}