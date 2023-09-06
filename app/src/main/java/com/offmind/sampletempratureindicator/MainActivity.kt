package com.offmind.sampletempratureindicator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offmind.sampletempratureindicator.ui.theme.SampleTempratureIndicatorTheme
import kotlinx.coroutines.delay
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SampleTempratureIndicatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var selectedHumidity by remember {
                        mutableStateOf(30)
                    }

                    var displayedHumidity by remember {
                        mutableStateOf(selectedHumidity)
                    }

                    LaunchedEffect(null) {
                        while (true) {
                            delay(kotlin.random.Random.nextLong(1500, 5000))
                            if (displayedHumidity > selectedHumidity) {
                                displayedHumidity--
                            } else if (displayedHumidity < selectedHumidity) {
                                displayedHumidity++
                            }
                        }
                    }

                    Row(modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xFF19203F),
                                    Color(0xFF192041),
                                    Color(0xFF19203F)
                                )
                            )
                        )
                        .padding(20.dp)) {
                        Box(
                            modifier = Modifier
                                .wrapContentWidth()
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            HumiditySelector(
                                selectedHumidity
                            ) {
                                if (selectedHumidity != it)
                                    selectedHumidity = it
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .padding(horizontal = 5.dp),
                            verticalArrangement = Arrangement.Center
                        ) {

                            TemperatureBlock(temperature = 20)
                            Spacer(modifier = Modifier.height(20.dp))

                            CurrentHumidityBlock(humidity = displayedHumidity)
                            Spacer(modifier = Modifier.height(20.dp))

                            AbsoluteHumidityBlock(humidity = 45)

                            Spacer(modifier = Modifier.height(20.dp))
                            Image(
                                painter = painterResource(id = R.drawable.warnsign),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(25.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            HumidityInfoBlock()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HumidityInfoBlock(modifier: Modifier = Modifier) {
    val annotatedString = buildAnnotatedString {
        appendInlineContent(id = "imageId")
        append(" — extreme humidity levels. Use precaution for set-points outside of 20%-55%")
    }
    val inlineContentMap = mapOf(
        "imageId" to InlineTextContent(
            Placeholder(10.sp, 10.sp, PlaceholderVerticalAlign.TextCenter)
        ) {
            Image(
                painter = painterResource(id = R.drawable.dotsign),
                modifier = Modifier.fillMaxSize(),
                contentDescription = ""
            )
        }
    )

    Text(
        annotatedString, inlineContent = inlineContentMap, fontSize = 14.sp,
        fontWeight = FontWeight(300)
    )
}

@Composable
fun AbsoluteHumidityBlock(modifier: Modifier = Modifier, humidity: Int) {
    Column(modifier = modifier) {
        Text(
            text = "Absolute Humidity".uppercase(Locale.ROOT),
            fontSize = 14.sp,
            color = Color(0xFF30445B),
            fontWeight = FontWeight(300)
        )
        Text(text = "${humidity}%", fontSize = 24.sp, fontWeight = FontWeight(500))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CurrentHumidityBlock(modifier: Modifier = Modifier, humidity: Int) {
    val firstHumidityValue = humidity / 10
    val secondHumidityValue = humidity % 10

    Column(modifier = modifier) {
        Text(
            text = "Current Humidity".uppercase(Locale.ROOT),
            fontSize = 14.sp,
            color = Color(0xFF30445B),
            fontWeight = FontWeight(300)
        )
        Row {
            AnimatedContent(
                targetState = firstHumidityValue,
                transitionSpec = {
                    slideIntoContainer(
                        towards = AnimatedContentScope.SlideDirection.Up,
                        animationSpec = tween(durationMillis = 500)
                    ) with slideOutVertically(animationSpec = tween(durationMillis = 500)) + fadeOut()
                }, label = ""
            ) { targetCount ->
                Text(text = "$targetCount", fontSize = 60.sp, fontWeight = FontWeight(800))
            }
            AnimatedContent(
                targetState = secondHumidityValue,
                transitionSpec = {
                    slideIntoContainer(
                        towards = AnimatedContentScope.SlideDirection.Up,
                        animationSpec = tween(durationMillis = 500)
                    ) with slideOutVertically(animationSpec = tween(durationMillis = 500)) + fadeOut()
                }, label = ""
            ) { targetCount ->
                Text(text = "$targetCount", fontSize = 60.sp, fontWeight = FontWeight(800))
            }
            Text(text = "%", fontSize = 60.sp, fontWeight = FontWeight(800))
        }
    }
}

@Composable
fun TemperatureBlock(modifier: Modifier = Modifier, temperature: Int) {
    Column(modifier = modifier) {
        Text(
            text = "Return temperature".uppercase(Locale.ROOT),
            fontSize = 14.sp,
            color = Color(0xFF30445B),
            fontWeight = FontWeight(300)
        )
        Text(text = "${temperature}°C", fontSize = 24.sp, fontWeight = FontWeight(500))
    }
}

val pointsArray = listOf(
    Point(0f,  0 / 9f),
    Point(10f, 1 / 9f),
    Point(25f, 2 / 9f),
    Point(30f, 3 / 9f),
    Point(35f, 4 / 9f),
    Point(40f, 5 / 9f),
    Point(45f, 6 / 9f),
    Point(50f, 7 / 9f),
    Point(75f, 8 / 9f),
    Point(100f, 9 / 9f)
)

val heightMap = mutableMapOf<Int, Float>()

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HumiditySelector(
    currentSelectedHumidity: Int = 0,
    onHumidityChanged: (newSelectedValue: Int) -> Unit = {}
) {
    var columnHeightPx by remember {
        mutableStateOf(0f)
    }

    var currentFraction by remember {
        mutableStateOf(pointsArray.getFractionByValue(currentSelectedHumidity.toFloat()))
    }

    var activePointIndex by remember {
        mutableStateOf(pointsArray.indexOfFirst {
            it.value >= pointsArray.getValueByFraction(
                currentFraction
            )
        })
    }

    val colorStops = arrayOf(
        0.25f to Color(0xFFBF518A),
        0.5f to Color(0xFF00B2E1),
        0.83f to Color(0xFFBF518A)
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(180.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0x00BF518A),
                            Color(0xFFBF518A)
                        )
                    ),
                    shape = LineBackground(
                        horizontalOffset = with(LocalDensity.current) { 38.dp.toPx() },
                        lineWidth = with(LocalDensity.current) { 5.dp.toPx() })
                )
        )
        Box(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    columnHeightPx = coordinates.size.height.toFloat()
                },
        ) {
            pointsArray.forEachIndexed { index, point ->
                TextScaleElement(
                    point = point,
                    pointsArray.getValueByFraction(currentFraction),
                    currentFraction,
                    isActive = index == activePointIndex
                ) {
                    heightMap[index] = if (columnHeightPx != 0F) it / columnHeightPx else 0F
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colorStops = colorStops
                                .map { it.copy(second = it.second.copy(alpha = 0.4f)) }
                                .toTypedArray()
                        ),
                        shape = ScaleBackground(
                            1 - currentFraction,
                            with(LocalDensity.current) { 50.dp.toPx() },
                            horizontalOffset = with(LocalDensity.current) { 48.dp.toPx() },
                            width = with(LocalDensity.current) { 70.dp.toPx() },
                            height = with(LocalDensity.current) { 30.dp.toPx() })
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(colorStops = colorStops),
                        shape = LineBackground(
                            1 - currentFraction,
                            buttonSize = with(LocalDensity.current) { 50.dp.toPx() },
                            horizontalOffset = with(LocalDensity.current) { 38.dp.toPx() },
                            width = with(LocalDensity.current) { 70.dp.toPx() },
                            height = with(LocalDensity.current) { 30.dp.toPx() },
                            lineWidth = with(LocalDensity.current) { 5.dp.toPx() })
                    )
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val isDragged by interactionSource.collectIsDraggedAsState()
                val isPressed by interactionSource.collectIsPressedAsState()

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(color = Color.White, shape = CircleShape)
                        .clickable(interactionSource = interactionSource, indication = null) {}
                        .draggable(
                            interactionSource = interactionSource,
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                val newFraction =
                                    currentFraction - (delta / if (columnHeightPx != 0f) columnHeightPx else 1f)
                                currentFraction = newFraction.coerceIn(0.1f, 0.9f)

                                activePointIndex = pointsArray.getIndexByFractionRange(
                                    fraction = currentFraction,
                                    currentIndex = activePointIndex,
                                    itemRelationalHeights = heightMap
                                )

                                onHumidityChanged(
                                    pointsArray
                                        .getValueByFraction(newFraction)
                                        .toInt()
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    this@Column.AnimatedVisibility(
                        visible = isPressed || isDragged,
                        enter = fadeIn() + scaleIn(initialScale = 1.5f),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Image(
                            modifier = Modifier.size(35.dp),
                            painter = painterResource(id =R.drawable.scrollindicator),
                            contentDescription = null
                        )
                    }

                    Image(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(id =R.drawable.scrollarrows),
                        contentDescription = null
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(currentFraction)
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFFBF518A),
                            Color(0x00BF518A)
                        )
                    ),
                    shape = LineBackground(
                        horizontalOffset = with(LocalDensity.current) { 38.dp.toPx() },
                        lineWidth = with(LocalDensity.current) { 5.dp.toPx() })
                )
        )
    }
}

@Composable
fun TextScaleElement(
    point: Point,
    value: Float,
    fraction: Float,
    isActive: Boolean,
    onTextSizeChanged: (newSize: Int) -> Unit = {}
) {

    val lerpFraction by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(500),
        label = "positionAnimation"
    )
    
    val size by animateValueAsState(
        targetValue = if (isActive) 35.sp else 20.sp,
        animationSpec = tween(500),
        label = "sizeAnimation",
        typeConverter = TwoWayConverter(
            convertToVector = { AnimationVector1D(it.value) },
            convertFromVector = { it.value.sp }
        )
    )

    val displayValue by animateIntAsState(
        targetValue = if (isActive) value.toInt() else point.value.toInt(),
        animationSpec = tween(if (isActive) 100 else 500, easing = LinearEasing),
        label = "displayValueAnim"
    )

    val position by animateFloatAsState(
        targetValue = if (isActive) fraction else point.fraction,
        animationSpec = tween(if (isActive) 100 else 300),
        label = "positionAnimation"
    )


    val color by animateColorAsState(
        targetValue = if (isActive) Color(0xFF72C3E0) else Color.White,
        animationSpec = tween(500),
        label = "positionAnimation"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (point.value <= 25 || point.value >= 50) {
                Image(
                    painter = painterResource(id = R.drawable.dotsign),
                    contentDescription = null,
                    modifier = Modifier.size(5.dp)
                )
                Spacer(modifier = Modifier.size(5.dp))
            }
            Text(
                text = "${displayValue}%",
                fontSize = size,
                color = color,
                fontWeight = FontWeight(300 + (500 * lerpFraction).toInt()),
                modifier = Modifier
                    .background(Color.Red.copy(alpha = 0f))
                    .onGloballyPositioned {
                        onTextSizeChanged(it.size.height)
                    }
            )
        }
        Spacer(
            modifier = Modifier.fillMaxHeight(position)
        )
    }
}

data class Point(
    val value: Float,
    val fraction: Float
)

fun List<Point>.getFractionByValue(value: Float): Float {
    val rightIndex = this.indexOfFirst { it.value > value }
    val right = this[rightIndex]
    val left = this[rightIndex - 1]
    val pos = (value - left.value) / (right.value - left.value)
    return left.fraction + (right.fraction - left.fraction) * pos
}

fun List<Point>.getIndexByFractionRange(
    fraction: Float,
    currentIndex: Int,
    itemRelationalHeights: Map<Int, Float>
): Int {
    val rightIndex = (currentIndex + 1).coerceAtMost(itemRelationalHeights.size - 1)
    val leftIndex = (currentIndex - 1).coerceAtLeast(0)

    val right = this[rightIndex]
    val left = this[leftIndex]

    val itemHeightFraction = itemRelationalHeights[currentIndex]

    return if (left.fraction + itemRelationalHeights[leftIndex]!! / 2f > fraction - itemHeightFraction!! / 2f) {
        leftIndex
    } else if (right.fraction - itemRelationalHeights[rightIndex]!! / 2f < itemHeightFraction!! / 2f + fraction) {
        rightIndex
    } else {
        currentIndex
    }
}

fun List<Point>.getValueByFraction(fraction: Float): Float {
    val rightIndex = plus(
        Point(101f, 1.1f)
    )
        .indexOfFirst { it.fraction > fraction }
        .coerceIn(1, size - 1)

    val right = this[rightIndex]
    val left = this[rightIndex - 1]
    val pos = (fraction - left.fraction) / (right.fraction - left.fraction)
    return left.value + (right.value - left.value) * pos
}

private fun curve(value: Float, height: Float, width: Float, center: Float): Float {
    val k1 = maxOf(
        0f,
        minOf(
            1f,
            (value - (center - width)) / (center - (center - width))
        )
    )

    val k2 = maxOf(
        0f,
        minOf(
            1f,
            (value - (center + width)) / (center - (center + width))
        )
    )

    val s1 = k1 * k1 * (3 - 2 * k1)
    val s2 = k2 * k2 * (3 - 2 * k2)

    return s1 * s2 * height
}

class ScaleBackground(
    private val currentValue: Float,
    private val buttonSize: Float,
    private val horizontalOffset: Float,
    private val width: Float,
    private val height: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(path = Path().apply {

            val scaledHeight = size.height - buttonSize
            val center = currentValue * scaledHeight + buttonSize / 2f
            val scaleStep = 20

            repeat(size.height.toInt() / scaleStep) {
                val y = it * scaleStep
                val x = size.width - horizontalOffset - curve(y.toFloat(), height, width, center)
                val lineWidth = if (it % 6 == 0) 70f else 45f

                drawLine(
                    path = this,
                    lineWidth = lineWidth.toInt(),
                    x = x.toInt(),
                    y = y
                )
            }
            close()
        })
    }
}

class LineBackground(
    private val currentValue: Float = 0f,
    private val buttonSize: Float = 0f,
    private val horizontalOffset: Float = 0f,
    private val width: Float = 0f,
    private val height: Float = 0f,
    private val lineWidth: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(path = Path().apply {

            val scaledHeight = size.height - buttonSize
            val center = currentValue * scaledHeight + buttonSize / 2f

            repeat(size.height.toInt()) {
                val y = it
                val x = (size.width - horizontalOffset) - curve(y.toFloat(), height, width, center)

                drawLine(
                    path = this,
                    lineWidth = lineWidth.toInt(),
                    x = x.toInt(),
                    y = y.toInt(),
                    strokeWidth = 1f
                )
            }
            close()
        })
    }
}

private fun drawLine(
    path: Path,
    x: Int,
    y: Int,
    lineWidth: Int,
    strokeWidth: Float = 2f
) {
    path.moveTo(x.toFloat(), y.toFloat())
    path.lineTo(x - lineWidth.toFloat(), y.toFloat())
    path.lineTo(x - lineWidth.toFloat(), y.toFloat() + strokeWidth)
    path.lineTo(x.toFloat(), y.toFloat() + strokeWidth)
    path.close()
}
