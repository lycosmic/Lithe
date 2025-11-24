package io.github.lycosmic.lithe.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularWavyProgressIndicator(
    modifier: Modifier = Modifier,
    waveColor: Color = MaterialTheme.colorScheme.primary, // 波浪颜色
    trackColor: Color = MaterialTheme.colorScheme.secondaryContainer, // 轨道颜色
    waveStrokeWidth: Dp = 4.dp, // 波浪笔触宽度
    trackStrokeWidth: Dp = 4.dp, // 轨道笔触宽度
    amplitude: Dp = 1.2.dp, // 振幅：决定波浪起伏的高度
    wavelength: Dp = 15.dp, // 波长
    gapSize: Dp = 4.dp, // 间隙大小
    cycleDuration: Int = 1500, // 整体旋转周期
    waveFlowDuration: Int = 3000 // 波浪流动周期
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WavyTransition")

    // 呼吸动画: 使用 keyframes 实现非对称的伸缩
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5000
                // 3秒内缓慢张开
                300f at 3000 using CubicBezierEasing(.42f, 0f, 1f, 1f)
                // 2秒内快速收缩
                30f at 5000 using FastOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    // 自转动画: 整体旋转
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(cycleDuration, easing = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)),
        ),
        label = "Rotation"
    )

    // 相位流动动画: 控制波浪纹理移动
    val phaseShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(), // 移动一个完整波长，实现无缝循环
        animationSpec = infiniteRepeatable(
            animation = tween(waveFlowDuration, easing = LinearEasing)
        ),
        label = "PhaseShift"
    )

    val wavePath = remember { Path() }
    val trackPath = remember { Path() }
    val density = LocalDensity.current

    val waveStrokeWidthPx = with(density) { waveStrokeWidth.toPx() }
    val trackStrokeWidthPx = with(density) { trackStrokeWidth.toPx() }
    val amplitudePx = with(density) { amplitude.toPx() }
    val wavelengthPx = with(density) { wavelength.toPx() }
    val gapSizePx = with(density) { gapSize.toPx() }

    Canvas(modifier = modifier.size(48.dp)) {
        val center = this.center
        val step = 3
        val maxStroke = maxOf(waveStrokeWidthPx, trackStrokeWidthPx)
        // 减去半个笔触宽度, 以及振幅, 防止超出边界
        val baseRadius = (size.minDimension - maxStroke) / 2f - amplitudePx

        // 间隙弧长
        val effectiveGapLength = gapSizePx + maxStroke
        // 间隙角度
        val gapAngle = Math.toDegrees((effectiveGapLength / baseRadius).toDouble()).toFloat()

        rotate(rotation) {
            // --- 绘制波浪 ---
            wavePath.rewind()
            val circumference = 2 * PI * baseRadius
            val frequency = circumference / wavelengthPx
            // 结束角度
            val endAngle = sweepAngle

            for (i in 0..endAngle.toInt() step step) {
                val currentAngle = i.toFloat()
                val rad = Math.toRadians(currentAngle.toDouble())
                // 波浪纹理
                val waveOffset = amplitudePx * sin((rad * frequency) + phaseShift)
                val r = baseRadius + waveOffset

                val x = center.x + (r * cos(rad)).toFloat()
                val y = center.y + (r * sin(rad)).toFloat()

                if (i == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
            }
            drawPath(
                path = wavePath,
                color = waveColor,
                style = Stroke(width = waveStrokeWidthPx, cap = StrokeCap.Round)
            )

            // --- 绘制轨道 ---
            trackPath.rewind()
            val trackStartAngle = sweepAngle + gapAngle
            val trackEndAngle = 360f - gapAngle

            if (trackStartAngle < trackEndAngle) {
                for (i in trackStartAngle.toInt()..trackEndAngle.toInt() step step) {
                    val currentAngle = i.toFloat()
                    val rad = Math.toRadians(currentAngle.toDouble())
                    val x = center.x + (baseRadius * cos(rad)).toFloat()
                    val y = center.y + (baseRadius * sin(rad)).toFloat()

                    if (i == trackStartAngle.toInt()) trackPath.moveTo(
                        x,
                        y
                    ) else trackPath.lineTo(x, y)
                }
                drawPath(
                    path = trackPath,
                    color = trackColor,
                    style = Stroke(width = trackStrokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CircularWavyProgressIndicatorPrev() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularWavyProgressIndicator()
    }
}