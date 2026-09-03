package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryTotalsSection(
    categoryTotals: List<CategoryTotal>
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        AppText(
            text = "इस माह श्रेणीवार खर्च",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        if (categoryTotals.isEmpty()) {

            AppText(
                text = "इस महीने कोई खर्च दर्ज नहीं है",
                fontSize = 13.sp
            )

        } else {

            categoryTotals.forEach { categoryTotal ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        AppText(
                            text = categoryTotal.category,
                            modifier = Modifier.weight(1f)
                        )

                        AppText(
                            text = formatAmount(categoryTotal.total),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
