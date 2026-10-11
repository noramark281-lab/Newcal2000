package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClinicDarkTeal
import com.example.ui.theme.TextPrimaryDark

class SpeechBubbleShape(
    private val cornerRadius: Dp = 16.dp,
    private val arrowWidth: Dp = 14.dp,
    private val arrowHeight: Dp = 10.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cr = with(density) { cornerRadius.toPx() }
        val aw = with(density) { arrowWidth.toPx() }
        val ah = with(density) { arrowHeight.toPx() }

        val bodyHeight = (size.height - ah).coerceAtLeast(0f)
        val path = Path().apply {
            // Main rounded rectangle
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = bodyHeight,
                    cornerRadius = CornerRadius(cr, cr)
                )
            )
            // Downward pointing arrow centered at the bottom of the body
            val centerX = size.width / 2f
            moveTo(centerX - aw / 2f, bodyHeight)
            lineTo(centerX, size.height)
            lineTo(centerX + aw / 2f, bodyHeight)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun SpeechBubbleTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bubbleShape = remember { SpeechBubbleShape(cornerRadius = 14.dp, arrowWidth = 14.dp, arrowHeight = 8.dp) }
    
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xF2FFFFFF) else Color(0xB8FFFFFF),
        label = "tab_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) ClinicDarkTeal else Color(0x80FFFFFF),
        label = "tab_border"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) ClinicDarkTeal else TextPrimaryDark,
        label = "tab_text"
    )

    Surface(
        shape = bubbleShape,
        color = bgColor,
        border = BorderStroke(if (isSelected) 1.8.dp else 1.dp, borderColor),
        shadowElevation = if (isSelected) 6.dp else 2.dp,
        modifier = modifier
            .testTag("tab_$title")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .padding(bottom = 6.dp) // space for arrow tip
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                color = textColor
            )
        }
    }
}
