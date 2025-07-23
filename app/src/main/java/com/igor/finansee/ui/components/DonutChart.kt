package com.igor.finansee.ui.components

import android.graphics.Color as AndroidColor
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.ExpenseWithCategory
import com.igor.finansee.data.models.TransactionType
@Composable
fun DonutChart(
    expenses: List<ExpenseWithCategory>,
    allCategories: List<Category>,
    transactionType: TransactionType,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setUsePercentValues(true)
                isDrawHoleEnabled = true
                setHoleColor(AndroidColor.TRANSPARENT)
                setEntryLabelColor(AndroidColor.BLACK)
                setTransparentCircleAlpha(0)
                setDrawEntryLabels(false)
                description.isEnabled = false
                legend.orientation = Legend.LegendOrientation.VERTICAL
                legend.isWordWrapEnabled = true
                legend.isEnabled = true
                setUsePercentValues(false)
            }
        },
        update = { chart ->
            val grouped = expenses
                .filter { it.category != null }
                .groupBy { it.category!!.name }
                .mapValues { entry -> entry.value.sumOf { it.expense.valor } }

            val entries = grouped.map { PieEntry(it.value.toFloat(), it.key) }

            val cores = grouped.keys.map { categoria ->
                val details = getCategoryUIDetails(transactionType, allCategories.find { it.name == categoria }?.id ?: -1, allCategories)
                details.iconBackgroundColor.toArgb()
            }


            val dataSet = PieDataSet(entries, null).apply {
                colors = cores
                sliceSpace = 2f
                valueTextColor = AndroidColor.BLACK
                valueTextSize = 14f
            }

            val data = PieData(dataSet)
            chart.data = data
            chart.invalidate()
        }
    )
}
