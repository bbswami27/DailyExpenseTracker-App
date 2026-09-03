package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BudgetProgressSection(
    budgets: List<Budget>,
    categoryTotals: List<CategoryTotal>
) {

    if (budgets.isEmpty()) {
        return
    }

    val spentByCategory = categoryTotals.associate {
        it.category to it.total
    }

    val totalBudget = budgets.sumOf {
        it.amount
    }

    val totalSpent = categoryTotals.sumOf {
        it.total
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        AppText(
            text = "मासिक बजट स्थिति",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {

            Column(
                modifier = Modifier.padding(14.dp)
            ) {

                AppText(
                    text = "कुल बजट: ${formatAmount(totalBudget)}",
                    fontWeight = FontWeight.Bold
                )

                AppText(
                    text = "कुल खर्च: ${formatAmount(totalSpent)}"
                )

                AppText(
                    text = "बचा बजट: ${formatAmount(totalBudget - totalSpent)}"
                )
            }
        }

        budgets.forEach { budget ->

            val spent = spentByCategory[budget.category] ?: 0.0

            val percentage =
                if (budget.amount > 0) {
                    (spent / budget.amount) * 100
                } else {
                    0.0
                }

            val progress =
                (percentage / 100)
                    .coerceIn(0.0, 1.0)
                    .toFloat()

            val progressColor = when {

                percentage >= 100 -> {
                    MaterialTheme.colorScheme.error
                }

                percentage >= 80 -> {
                    Color(0xFFFF9800)
                }

                else -> {
                    MaterialTheme.colorScheme.primary
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {

                Column(
                    modifier = Modifier.padding(14.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        AppText(
                            text = budget.category,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Medium
                        )

                        AppText(
                            text = "${percentage.toInt()}%",
                            color = progressColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = {
                            progress
                        },

                        modifier = Modifier.fillMaxWidth(),

                        color = progressColor
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    AppText(
                        text = "${formatAmount(spent)} / ${formatAmount(budget.amount)}",
                        fontSize = 12.sp
                    )

                    when {

                        percentage >= 100 -> {
                            AppText(
                                text = "Budget Exceeded",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        percentage >= 80 -> {
                            AppText(
                                text = "बजट का 80% से अधिक खर्च हो चुका है",
                                color = Color(0xFFFF9800),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
