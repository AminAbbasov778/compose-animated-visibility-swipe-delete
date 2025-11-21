package com.example.animatedvisibilty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.animatedvisibilty.ui.theme.AnimatedVisibiltyTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimatedVisibiltyTheme {
                AnimatedVisibiltyScreen()
            }
        }
    }
}



@Composable
fun AnimatedVisibiltyScreen() {
    val items = remember {
        mutableStateListOf(
            ImageItem(0, R.drawable.view),
            ImageItem(1, R.drawable.view2),
            ImageItem(2, R.drawable.view3),
            ImageItem(3, R.drawable.view4),
            ImageItem(4, R.drawable.view5),
            ImageItem(5, R.drawable.view6),
            ImageItem(6, R.drawable.view7),
            ImageItem(7, R.drawable.view8),
            ImageItem(8, R.drawable.view9),
            ImageItem(9, R.drawable.view11)
        )
    }

    LaunchedEffect(Unit) {
        items.forEachIndexed { index, item ->
            delay(300)
            items[index] = item.copy(isEntryVisible = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .systemBarsPadding()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(
                count = items.size,
                key = { items[it].id }
            ) { index ->
                val item = items[index]
                val isLeftColumn = index % 2 == 0

                AnimatedVisibility(
                    visible = item.isEntryVisible,
                    enter = fadeIn() + if (isLeftColumn)
                        slideInHorizontally { -it }
                    else
                        slideInHorizontally { it }
                ) {
                    SwipeToDeleteItem(
                        imageRes = item.imageRes,
                        isLeftColumn = isLeftColumn,
                        onDismiss = {
                            items.removeAt(index)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun SwipeToDeleteItem(
    imageRes: Int,
    isLeftColumn: Boolean,
    onDismiss: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var isVisible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut(animationSpec = tween(300)) +
                slideOutHorizontally(
                    targetOffsetX = { if (isLeftColumn) -it else it },
                    animationSpec = tween(300)
                ) +
                scaleOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .aspectRatio(0.85f)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val threshold = 200f
                            if (isLeftColumn && offsetX < -threshold) {
                                isVisible = false
                            } else if (!isLeftColumn && offsetX > threshold) {
                                isVisible = false
                            } else {
                                offsetX = 0f
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            if ((isLeftColumn && dragAmount < 0) ||
                                (!isLeftColumn && dragAmount > 0)
                            ) {
                                offsetX += dragAmount
                            }
                        }
                    )
                }
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(300)
            onDismiss()
        }
    }
}
