package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val chartColors = listOf(
    Color(0xFFFFC857),
    Color(0xFF4D9BE8),
    Color(0xFF55D68B),
    Color(0xFFFF7474),
    Color(0xFFA879E8),
    Color(0xFFFF985E),
    Color(0xFF37C6C0),
    Color(0xFFF062A7)
)

@Composable
fun CategoryDonutChart(
    categoryTotals: List<CategoryTotal>,
    modifier: Modifier = Modifier
) {
    val validCategories = categoryTotals.filter {
        it.total > 0
    }

    val grandTotal = validCategories.sumOf {
        it.total
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            AppText(
                text = "Spending Overview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            AppText(
                text = "इस माह श्रेणीवार खर्च",
                fontSize = 12.sp,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            if (
                grandTotal <= 0 ||
                validCategories.isEmpty()
            ) {

                AppText(
                    text = "इस माह कोई खर्च नहीं है।",
                    modifier = Modifier.padding(
                        vertical = 24.dp
                    )
                )

            } else {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Canvas(
                        modifier = Modifier.size(170.dp)
                    ) {
                        val strokeWidth = 32.dp.toPx()

                        var startAngle = -90f

                        validCategories.forEachIndexed {
                                index,
                                categoryTotal ->

                            val sweepAngle =
                                (
                                        categoryTotal.total /
                                                grandTotal *
                                                360.0
                                        ).toFloat()

                            drawArc(
                                color =
                                    chartColors[
                                        index %
                                                chartColors.size
                                    ],
                                startAngle = startAngle,
                                sweepAngle =
                                    (sweepAngle - 2f)
                                        .coerceAtLeast(0f),
                                useCenter = false,
                                topLeft = Offset(
                                    strokeWidth / 2,
                                    strokeWidth / 2
                                ),
                                size = size.copy(
                                    width =
                                        size.width -
                                                strokeWidth,
                                    height =
                                        size.height -
                                                strokeWidth
                                ),
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            )

                            startAngle += sweepAngle
                        }
                    }

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        AppText(
                            text = "Total",
                            fontSize = 12.sp,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )

                        AppText(
                            text = formatAmount(grandTotal),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                validCategories.forEachIndexed {
                        index,
                        categoryTotal ->

                    val percentage =
                        (
                                categoryTotal.total /
                                        grandTotal *
                                        100.0
                                ).toInt()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment =
                            Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        color =
                                            chartColors[
                                                index %
                                                        chartColors.size
                                            ],
                                        shape = CircleShape
                                    )
                            )

                            AppText(
                                text = categoryTotal.category,
                                modifier =
                                    Modifier.padding(
                                        start = 10.dp
                                    ),
                                fontSize = 13.sp
                            )
                        }

                        AppText(
                            text = "$percentage%",
                            color =
                                MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
