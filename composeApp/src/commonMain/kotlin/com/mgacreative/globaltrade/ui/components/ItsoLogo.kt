package com.mgacreative.globaltrade.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ItsoLogo(
    modifier: Modifier = Modifier
) {
    val darkBlue = Color(0xFF1B4E8B)
    val lightBlue = Color(0xFF29ABE2)
    val orange = Color(0xFFF7941E)

    Box(
        modifier = modifier
            .size(240.dp)
            .clip(CircleShape)
            .background(darkBlue),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.78f)
                .clip(CircleShape)
                .background(lightBlue),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val centerY = size.height / 2
                val scale = size.width / 200f

                drawRect(
                    color = orange,
                    topLeft = Offset(centerX + 12f * scale, centerY - 40f * scale),
                    size = Size(28f * scale, 90f * scale)
                )

                drawRect(
                    color = darkBlue,
                    topLeft = Offset(centerX - 15f * scale, centerY - 55f * scale),
                    size = Size(60f * scale, 10f * scale)
                )
                drawRect(
                    color = darkBlue,
                    topLeft = Offset(centerX - 5f * scale, centerY - 45f * scale),
                    size = Size(12f * scale, 110f * scale)
                )
                drawRect(
                    color = darkBlue,
                    topLeft = Offset(centerX - 30f * scale, centerY + 65f * scale),
                    size = Size(80f * scale, 12f * scale)
                )

                val gearCenterX = centerX - 20f * scale
                val gearCenterY = centerY + 25f * scale
                val gearOuterRadius = 30f * scale
                val gearInnerRadius = 18f * scale

                drawCircle(
                    color = Color.White,
                    radius = gearOuterRadius,
                    center = Offset(gearCenterX, gearCenterY)
                )
                drawCircle(
                    color = orange,
                    radius = gearInnerRadius,
                    center = Offset(gearCenterX, gearCenterY)
                )
                
                val toothCount = 10
                for (i in 0 until toothCount) {
                    val angle = (i * 360f / toothCount) * (PI / 180.0).toFloat()
                    val tx = gearCenterX + cos(angle.toDouble()).toFloat() * gearOuterRadius
                    val ty = gearCenterY + sin(angle.toDouble()).toFloat() * gearOuterRadius
                    drawCircle(
                        color = Color.White,
                        radius = 5f * scale,
                        center = Offset(tx, ty)
                    )
                }
                
                val flamePath = Path().apply {
                    moveTo(centerX + 26f * scale, centerY - 55f * scale)
                    quadraticTo(
                        centerX + 35f * scale, centerY - 85f * scale,
                        centerX + 15f * scale, centerY - 95f * scale
                    )
                    quadraticTo(
                        centerX + 40f * scale, centerY - 85f * scale,
                        centerX + 45f * scale, centerY - 55f * scale
                    )
                    close()
                }
                drawPath(flamePath, color = orange)
                
                for (i in 0..4) {
                    val yPos = centerY + 30f * scale + (i * 8f * scale)
                    drawLine(
                        color = darkBlue,
                        start = Offset(centerX + 45f * scale, yPos),
                        end = Offset(centerX + 65f * scale, yPos),
                        strokeWidth = 2f * scale
                    )
                }
            }
            
            Text(
                text = "1920",
                color = darkBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 25.dp)
            )
        }

        Text(
            text = "TİCARET VE SANAYİ ODASI",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 18.dp)
        )
        
        Text(
            text = "İSKENDERUN",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 15.dp)
        )
        
        Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            Text(
                text = "â˜…",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = "â˜…",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}
