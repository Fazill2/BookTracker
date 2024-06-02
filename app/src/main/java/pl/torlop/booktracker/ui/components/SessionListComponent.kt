package pl.torlop.booktracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import pl.torlop.booktracker.entity.ReadingSession
import java.text.SimpleDateFormat
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Composable
fun SessionListComponent(sessions: List<ReadingSession>, modifier: Modifier = Modifier, deleteSession : (ReadingSession) -> Unit){
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max).background(
                MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            TableCell("Date",  0.3f)
            TableCell("Pages Start", 0.2f)
            TableCell("Pages End", 0.2f)
            TableCell("Duration", 0.3f)
        }
        sessions.forEach { session ->
            SessionListEntry(session, deleteSession)
        }
    }
}
@OptIn(ExperimentalTime::class)
@Composable
fun SessionListEntry(session: ReadingSession, deleteSession: (ReadingSession) -> Unit){
    val formatter = SimpleDateFormat.getDateInstance()
    val dateString = formatter.format(session.date)

    Row(
        modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max).pointerInput(Unit){
            detectTapGestures(
                onLongPress = {
                    deleteSession(session)
                }
            )
        }
    ) {
        TableCell(dateString, 0.3f, Modifier.background(MaterialTheme.colorScheme.surfaceContainer))
        TableCell(session.pagesStart.toString(), 0.2f, Modifier.background(MaterialTheme.colorScheme.surfaceContainer))
        TableCell(session.pagesEnd.toString(), 0.2f, Modifier.background(MaterialTheme.colorScheme.surfaceContainer))
        TableCell("${session.duration} min", 0.3f, Modifier.background(MaterialTheme.colorScheme.surfaceContainer))
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .fillMaxHeight()
            .padding(8.dp),

        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,

            )
    }
}