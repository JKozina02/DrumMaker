package com.example.drummaker.composable.reusable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.drummaker.ui.theme.ElevatedBackgroundColor
import com.example.drummaker.ui.theme.ElevatedBorderUnselected

@Composable
fun PlayerIconButton(
    @DrawableRes drawableId: Int,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .clickable{ onClick() }
            .background(
                color = ElevatedBackgroundColor,
                shape = RoundedCornerShape(5.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(5.dp),
                color = ElevatedBorderUnselected
            )
            .size(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = "MenuIcon",
        )
    }
}