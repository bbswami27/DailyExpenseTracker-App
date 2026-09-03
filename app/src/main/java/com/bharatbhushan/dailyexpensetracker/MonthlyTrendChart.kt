package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.platform.LocalConfiguration

private data class MonthChartItem(
    val monthKey: String,
    val monthLabel: String,
    val income: Double,
    val expense: Double
)

@Composable
fun MonthlyTrendChart(
    monthlyIncome: List<MonthlyTotal>,
    monthlyExpenses: List<MonthlyTotal>,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current

    val isCompactScreen =
        configuration.screenWidthDp < 600

    val chartCanvasHeight =
        if (isCompactScreen) {
            110.dp
        } else {
            180.dp
        }
    val incomeMap = monthlyIncome.associate {
        it.monthKey to it.total
    }

    val expenseMap = monthlyExpenses.associate {
        it.monthKey to it.total
    }

    val chartItems = remember(
        monthlyIncome,
        monthlyExpenses
    ) {
        val keyFormatter = SimpleDateFormat(
            "yyyy-MM",
            Locale.US
        )

        val labelFormatter = SimpleDateFormat(
            "MMM",
            Locale.getDefault()
        )

        val calendar = Calendar.getInstance()

        List(6) {

            val monthKey = keyFormatter.format(
                calendar.time
            )

            val monthLabel = labelFormatter.format(
                calendar.time
            )

            val item = MonthChartItem(
                monthKey = monthKey,
                monthLabel = monthLabel,
                income = incomeMap[monthKey] ?: 0.0,
                expense = expenseMap[monthKey] ?: 0.0
            )

            calendar.add(Calendar.MONTH, -1)

            item
        }.reversed()
    }

    val maximumValue =
        chartItems.maxOfOrNull {
            maxOf(it.income, it.expense)
        } ?: 0.0

    val incomeColor =
        Color(0xFF55D68B)

    val expenseColor =
        Color(0xFFFF7474)

    val axisColor =
        MaterialTheme.colorScheme.outline.copy(
            alpha = 0.35f
        )

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
                text = "Monthly Trend",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            AppText(
                text = "पिछले 6 महीनों की Income और Expense",
                fontSize = 12.sp,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(18.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                ChartLegend(
                    color = incomeColor,
                    text = "Income"
                )

                ChartLegend(
                    color = expenseColor,
                    text = "Expense"
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartCanvasHeight)
            ) {
                val groupWidth =
                    size.width / chartItems.size

                val maximumBarWidth =
                    if (isCompactScreen) {
                        6.dp.toPx()
                    } else {
                        14.dp.toPx()
                    }

                val barWidth =
                    (groupWidth * 0.14f)
                        .coerceAtMost(maximumBarWidth)

                val chartHeight =
                    size.height - 10.dp.toPx()

                drawLine(
                    color = axisColor,
                    start = Offset(
                        0f,
                        chartHeight
                    ),
                    end = Offset(
                        size.width,
                        chartHeight
                    ),
                    strokeWidth = 1.dp.toPx()
                )

                chartItems.forEachIndexed {
                        index,
                        item ->

                    val centerX =
                        groupWidth * index +
                                groupWidth / 2f

                    val incomeHeight =
                        if (maximumValue > 0) {
                            (
                                    item.income /
                                            maximumValue *
                                            chartHeight
                                    ).toFloat()
                        } else {
                            0f
                        }

                    val expenseHeight =
                        if (maximumValue > 0) {
                            (
                                    item.expense /
                                            maximumValue *
                                            chartHeight
                                    ).toFloat()
                        } else {
                            0f
                        }

                    drawRect(
                        color = incomeColor,
                        topLeft = Offset(
                            centerX - barWidth - 2.dp.toPx(),
                            chartHeight - incomeHeight
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            barWidth,
                            incomeHeight
                        )
                    )

                    drawRect(
                        color = expenseColor,
                        topLeft = Offset(
                            centerX + 2.dp.toPx(),
                            chartHeight - expenseHeight
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            barWidth,
                            expenseHeight
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceAround
            ) {

                chartItems.forEach { item ->

                    AppText(
                        text = item.monthLabel,
                        fontSize = 11.sp,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            val currentMonth =
                chartItems.lastOrNull()

            if (currentMonth != null) {

                AppText(
                    text =
                        "इस माह Balance: " +
                                formatAmount(
                                    currentMonth.income -
                                            currentMonth.expense
                                ),
                    fontWeight = FontWeight.Bold,
                    color =
                        if (
                            currentMonth.income >=
                            currentMonth.expense
                        ) {
                            incomeColor
                        } else {
                            expenseColor
                        }
                )
            }
        }
    }
}

@Composable
private fun ChartLegend(
    color: Color,
    text: String
) {
    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Spacer(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )

        AppText(
            text = text,
            modifier = Modifier.padding(start = 6.dp),
            fontSize = 12.sp
        )
    }
}
