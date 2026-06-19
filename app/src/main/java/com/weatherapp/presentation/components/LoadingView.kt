package com.weatherapp.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LoadingView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        LargeShimmerCard()
        MediumShimmerRow()
        MediumShimmerRow()
        SmallShimmerRow()
    }
}

@Composable
fun LargeShimmerCard() {
    ShimmerBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        corner = 32.dp
    )
}

@Composable
fun MediumShimmerRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        ShimmerBox(
            modifier = Modifier
                .weight(1f)
                .height(120.dp),
            corner = 24.dp
        )
        ShimmerBox(
            modifier = Modifier
                .weight(1f)
                .height(120.dp),
            corner = 24.dp
        )
    }
}

@Composable
fun SmallShimmerRow() {
    ShimmerBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        corner = 20.dp
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    corner: Dp = 16.dp
) {
    val baseColor = MaterialTheme.colorScheme.background
    val highlight = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing)
        ),
        label = "shimmerAnim"
    )

    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlight, baseColor),
        start = Offset(translate, 0f),
        end = Offset(translate + 300f, 300f)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(corner))
            .background(brush)
    )
}

@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    content: @Composable () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        content()

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingViewPreview() {
    LoadingView()
}