package pl.torlop.booktracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import pl.torlop.booktracker.entity.SumDurationByDate
import pl.torlop.booktracker.ui.components.barchart.BarChartComponent
import pl.torlop.booktracker.viewmodel.BookViewModel
import pl.torlop.booktracker.viewmodel.SessionViewModel
import java.util.*

@Composable
fun HomeScreen(drawerState: DrawerState, viewModel: BookViewModel, sessionViewModel: SessionViewModel,navController: NavController) {
    val sessionList = sessionViewModel.getDailyReadingTime().collectAsState(initial = emptyList())
    val sessionWeekList = sessionViewModel.getDailyReadingTimeBetweenDates(
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time,
        Calendar.getInstance().time
    ).collectAsState(initial = emptyList())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(16.dp)
        ) {
            BarChartComponent(
                basicData = sessionWeekList.value,
                modifier = Modifier,
                startDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }.time,
                endDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1);  set(Calendar.HOUR_OF_DAY, 0)  }.time
            )
        }
    }

}

fun createDataPoints(data: List<SumDurationByDate>, color: Color): List<BarData> {
    val values = data.map { it.duration / 60 }
    return values.mapIndexed { index, value ->
        BarData(
            point = Point(
                x = index.toFloat(),
                y = value.toFloat()
            ),
            color = color,
            label = "${value}h",
            description = "${value}h"
        )
    }
}

fun fillData(data: List<SumDurationByDate>, startDate: Date, endDate: Date): List<SumDurationByDate> {

    val dataMap = data.associateBy { formatDate(it.date) }.toMutableMap()
    val calendar = Calendar.getInstance()
    calendar.time = startDate
    while (calendar.time.before(endDate)) {
        val date = calendar.time
        val stringDate = formatDate(date)
        if (!dataMap.containsKey(stringDate)) {
            dataMap[stringDate] = SumDurationByDate(date, 0)
        }
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    val dataList = dataMap.values.toList()

    return dataList.sortedBy { it.date }
}

fun formatDate(date: Date): String {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
}
