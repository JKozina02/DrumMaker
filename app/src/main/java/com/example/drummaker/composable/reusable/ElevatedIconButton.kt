package com.example.drummaker.composable.reusable

import androidx.annotation.DrawableRes
import com.example.drummaker.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.drummaker.ui.theme.ElevatedBackgroundColor
import com.example.drummaker.ui.theme.ElevatedBorderSelected
import com.example.drummaker.ui.theme.ElevatedBorderUnselected

@Composable
fun ElevatedIconButton(
    isSelected: Boolean = false,
    isHalved: Boolean = false,
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
                    color = if (isSelected) ElevatedBorderSelected else ElevatedBorderUnselected
                )
                .size(width = if (isHalved) 40.dp else 75.dp, height = 75.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "MenuIcon",
                modifier = Modifier.rotate(90f)
            )
        }
    }


@Preview
@Composable
fun ElevatedIconButtonPreview(){
    Box(
        modifier = Modifier
            .background(
                color = ElevatedBackgroundColor,
                shape = RoundedCornerShape(5.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(5.dp),
                color = ElevatedBorderUnselected
            )
            .size(75.dp)
            .shadow(
                shape = RoundedCornerShape(5.dp),
                elevation = (-5).dp,
            ),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.squares_four),
            contentDescription = "MenuIcon"
        )
    }
}

@Preview
@Composable
fun ElevatedIconButtonSelectedPreview(){
    Box(
        modifier = Modifier
            .background(
                color = ElevatedBackgroundColor,
                shape = RoundedCornerShape(5.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(5.dp),
                color = ElevatedBorderSelected
            )
            .size(75.dp)
            .shadow(
                shape = RoundedCornerShape(5.dp),
                elevation = (-5).dp,
            ),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.note),
            contentDescription = "MenuIcon",

        )
    }
}

@Preview
@Composable
fun ElevatedIconButtonHalfPreview(){
    Box(
        modifier = Modifier
            .background(
                color = ElevatedBackgroundColor,
                shape = RoundedCornerShape(5.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(5.dp),
                color = ElevatedBorderUnselected
            )
            .size(
                width = 75.dp,
                height = 40.dp
            ),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.arrow_down),
            contentDescription = "MenuIcon"
        )
    }
}