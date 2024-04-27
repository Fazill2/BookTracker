package pl.torlop.booktracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pl.torlop.booktracker.entity.ReadingSession

@Composable
fun SessionListComponent(sessions: List<ReadingSession>, modifier: Modifier = Modifier){
    Column(
        modifier = modifier
    ) {
        sessions.forEach {
            SessionListEntry(session = it)
        }
    }
}

@Composable
fun SessionListEntry(session: ReadingSession){
    Row {
        Text("On ${session.date} you read from ${session.pagesStart} to ${session.pagesEnd} for ${session.duration} minutes.")
    }
}