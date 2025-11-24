package com.example.drummaker.composable.reusable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.drummaker.ui.theme.Blue
import com.example.drummaker.ui.theme.ElevatedBackgroundColor
import com.example.drummaker.ui.theme.ElevatedBorderUnselected
import com.example.drummaker.ui.theme.Red

@Composable
fun SequencerButton(
    isSelected: Boolean = false,
    clickedColor: Color,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .clickable{ onClick() }
            .background(
                color = if(isSelected)clickedColor else ElevatedBackgroundColor,
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

    }
}

@Preview
@Composable
fun PreviewSequencerButtonRed(
    isSelected: Boolean = true,
    clickedColor: Color = Red,
    onClick: () -> Unit = { }
){
    Box(
        modifier = Modifier
            .clickable{ onClick() }
            .background(
                color = if(isSelected) clickedColor else ElevatedBackgroundColor ,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(5.dp),
                color = ElevatedBorderUnselected
            )
            .size(40.dp),
        contentAlignment = Alignment.Center
    ) {

    }
}

@Preview
@Composable
fun PreviewSequencerButtonBlue(
    isSelected: Boolean = false,
    clickedColor: Color = Blue,
    onClick: () -> Unit = { }
){
    Box(
        modifier = Modifier
            .clickable{
                onClick()
            }
            .background(
                color = if(isSelected) clickedColor else ElevatedBackgroundColor ,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(5.dp),
                color = ElevatedBorderUnselected
            )
            .size(40.dp),
        contentAlignment = Alignment.Center
    ) {

    }
}
@Preview
@Composable
fun PreviewSequencerButtonUnclicked(
    isSelected: Boolean = false,
    clickedColor: Color = Blue,
    onClick: () -> Unit = { }
){
    Box(
        modifier = Modifier
            .clickable{ onClick() }
            .background(
                color = if(isSelected) clickedColor else ElevatedBackgroundColor ,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(5.dp),
                color = ElevatedBorderUnselected
            )
            .size(40.dp),
        contentAlignment = Alignment.Center
    ) {

    }
}