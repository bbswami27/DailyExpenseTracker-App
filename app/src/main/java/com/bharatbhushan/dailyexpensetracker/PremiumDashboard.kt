package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val IncomeGreen = Color(0xFF55D68B)
private val ExpenseRed = Color(0xFFFF7474)

@Composable
fun PremiumDashboard(
    todayTotal: Double,
    monthTotal: Double,
    financialYearTotal: Double,
    monthIncome: Double,
    categoryTotals: List<CategoryTotal>,
    budgets: List<Budget>,
    recentExpenses: List<Expense>,
    monthlyIncome: List<MonthlyTotal>,
    monthlyExpenses: List<MonthlyTotal>,
    savingsGoals: List<SavingsGoal>,
    onCashInClick: () -> Unit,
    onCashOutClick: () -> Unit,
    onRecentTransactionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val balance = monthIncome - monthTotal

    val totalBudget = budgets.sumOf {
        it.amount
    }

    val budgetProgress =
        if (totalBudget > 0) {
            (monthTotal / totalBudget)
                .toFloat()
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        AppText(
            text = "नमस्ते 👋",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        AppText(
            text = "Cash Flow Home",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickCashCard("Cash In", "पैसा आया", Color(0xFF087F5B), Icons.Default.ArrowDownward, onCashInClick, Modifier.weight(1f))
            QuickCashCard("Cash Out", "पैसा गया", Color(0xFFD9480F), Icons.Default.ArrowUpward, onCashOutClick, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(20.dp)
        ) {

            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Column {

                        AppText(
                            text = "इस माह Balance",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        AppText(
                            text = formatAmount(balance),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Icon(
                        imageVector =
                            Icons.Default.AccountBalanceWallet,
                        contentDescription = "Balance",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AppText(
                    text =
                        if (balance >= 0) {
                            "आपका budget balance में है"
                        } else {
                            "खर्च आय से अधिक है"
                        },
                    fontSize = 13.sp,
                    color =
                        if (balance >= 0) {
                            IncomeGreen
                        } else {
                            ExpenseRed
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            PremiumMetricCard(
                title = "इस माह आय",
                amount = monthIncome,
                icon = {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Income",
                        tint = IncomeGreen
                    )
                },
                amountColor = IncomeGreen,
                modifier = Modifier.weight(1f)
            )

            PremiumMetricCard(
                title = "इस माह खर्च",
                amount = monthTotal,
                icon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Expense",
                        tint = ExpenseRed
                    )
                },
                amountColor = ExpenseRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            PremiumMetricCard(
                title = "आज का खर्च",
                amount = todayTotal,
                icon = {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Today",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                amountColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            PremiumMetricCard(
                title = "इस FY का खर्च",
                amount = financialYearTotal,
                icon = {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Financial Year",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                amountColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                AppText(
                    text = "Budget Overview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    AppText(
                        text =
                            "खर्च ${formatAmount(monthTotal)}"
                    )

                    AppText(
                        text =
                            if (totalBudget > 0) {
                                "${(budgetProgress * 100).toInt()}%"
                            } else {
                                "Budget तय नहीं"
                            },
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = {
                        budgetProgress
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color =
                        if (budgetProgress >= 1f) {
                            ExpenseRed
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                AppText(
                    text =
                        if (totalBudget > 0) {
                            "कुल Budget: ${formatAmount(totalBudget)}"
                        } else {
                            "☰ Menu से Monthly Budget तय करें"
                        },
                    fontSize = 13.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SavingsGoalsOverviewCard(
            goals = savingsGoals
        )
        Spacer(modifier = Modifier.height(18.dp))

        MonthlyTrendChart(
            monthlyIncome = monthlyIncome,
            monthlyExpenses = monthlyExpenses
        )
        Spacer(modifier = Modifier.height(18.dp))

        BudgetProgressSection(
            budgets = budgets,
            categoryTotals = categoryTotals
        )

        Spacer(modifier = Modifier.height(18.dp))

        CategoryDonutChart(
            categoryTotals = categoryTotals
        )

        Spacer(modifier = Modifier.height(18.dp))

        AppText(
            text = "हाल के खर्च / Recent Transactions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onRecentTransactionsClick)
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (recentExpenses.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                AppText(
                    text = "अभी कोई खर्च दर्ज नहीं है।",
                    modifier = Modifier.padding(16.dp)
                )
            }

        } else {

            recentExpenses
                .sortedByDescending {
                    it.createdAt
                }
                .take(5)
                .forEach { expense ->

                    RecentExpenseRow(
                        expense = expense,
                        onClick = onRecentTransactionsClick
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun QuickCashCard(
    title: String,
    subtitle: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Icon(icon, null, tint = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            AppText(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            AppText(subtitle, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun PremiumMetricCard(
    title: String,
    amount: Double,
    icon: @Composable () -> Unit,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                    text = title,
                    fontSize = 12.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )

                icon()
            }

            Spacer(modifier = Modifier.height(8.dp))

            AppText(
                text = formatAmount(amount),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@Composable
private fun RecentExpenseRow(
    expense: Expense,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    AppText(
                        text = expense.category,
                        fontWeight = FontWeight.Bold
                    )

                    if (expense.description.isNotBlank()) {
                        AppText(
                            text = expense.description,
                            fontSize = 12.sp,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    AppText(
                        text =
                            "${formatExpenseDate(expense.createdAt)} • " +
                                    expense.paymentMode,
                        fontSize = 11.sp,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }

                AppText(
                    text = "-${formatAmount(expense.amount)}",
                    color = ExpenseRed,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(
                    alpha = 0.30f
                )
            )
        }
    }
}
@Composable
private fun SavingsGoalsOverviewCard(
    goals: List<SavingsGoal>
) {
    val activeGoals = goals.filter {
        !it.isCompleted
    }

    val targetTotal = goals.sumOf {
        it.targetAmount
    }

    val savedTotal = goals.sumOf {
        it.savedAmount
    }

    val progress =
        if (targetTotal > 0) {
            (savedTotal / targetTotal)
                .toFloat()
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                AppText(
                    text = "Savings Goals",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                AppText(
                    text = "${goals.size} Goals",
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            AppText(
                text =
                    "Saved: ${formatAmount(savedTotal)}"
            )

            AppText(
                text =
                    "Target: ${formatAmount(targetTotal)}",
                fontSize = 13.sp
            )

            AppText(
                text =
                    "Active Goals: ${activeGoals.size}",
                fontSize = 12.sp,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}
