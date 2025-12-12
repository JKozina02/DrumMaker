package com.example.drummaker.composable.reusable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drummaker.ui.theme.ElevatedBackgroundColor
import com.example.drummaker.ui.theme.ElevatedBorderUnselected
import com.example.drummaker.ui.theme.SpecialTextColor

@Composable
fun BPMButton(
    value: Int,
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
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.10f).fillMaxWidth())
            Box(
                modifier = Modifier.weight(0.5f).fillMaxWidth(),
                contentAlignment = Alignment.Center // lub Center
            ) {
                Text(
                    color = SpecialTextColor,
                    text = "$value",
                    textAlign = TextAlign.End,
                    fontSize = 24.sp
                )
            }
            Box(
                modifier = Modifier.weight(0.40f).fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "BPM",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }

    }
}