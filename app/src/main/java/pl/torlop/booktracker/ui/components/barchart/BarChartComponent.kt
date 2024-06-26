package pl.torlop.booktracker.ui.components.barchart

import android.icu.text.DateFormatSymbols
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import pl.torlop.booktracker.createDataPoints
import pl.torlop.booktracker.entity.SumDurationByDate
import pl.torlop.booktracker.fillData
import java.util.*

@Composable
fun BarChartComponent(basicData: List<SumDurationByDate>, modifier: Modifier = Modifier, startDate: Date, endDate: Date) {
    val data =  fillData(basicData, startDate, endDate)
    val weekDayLabels = DateFormatSymbols(Locale.getDefault()).weekdays;
    if (data.isEmpty()) return
    val labels = data.map { it.date }
    val xAxisData: AxisData = AxisData.Builder()
        .steps(data.size)
        .backgroundColor(Color.Transparent)
        .axisStepSize(50.dp)
        .labelData {
            index ->
            if (index != data.size) {
               weekDayLabels[
                    Calendar.getInstance().apply { time = labels[index] }
                        .get(Calendar.DAY_OF_WEEK)].toString().substring(0, 3)
            } else {
                ""
            }
        }
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .bottomPadding(15.dp)
        .startDrawPadding(20.dp)
        .build()

    val values = data.map { it.duration / 60 }
    var maxValue = values.maxOrNull() ?: 0
    if (maxValue == 0) maxValue = 2 else maxValue += 1
    val yAxisData: AxisData = AxisData.Builder()
        .steps(maxValue)
        .backgroundColor(Color.Transparent)
        .labelData {
                index ->

            ( index  ).toString() + "h"
        }
        .labelAndAxisLinePadding(15.dp)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val dataPoints = createDataPoints(data, MaterialTheme.colorScheme.primary)
    dataPoints.add(
        BarData(
            point = Point(
                x = data.size.toFloat(),
                y = 0f
            ),
            color = Color.Transparent,
            label = "",
            description = ""

        )
    )

    val barChartData = BarChartData(
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        chartData = dataPoints,
        backgroundColor = MaterialTheme.colorScheme.surface,
        barStyle = BarStyle(
            barWidth = 20.dp,
            paddingBetweenBars = 20.dp
        ),
        paddingTop = 20.dp,
    )

    BarChart(
        barChartData = barChartData,
        modifier = modifier.height(350.dp)
    )

}