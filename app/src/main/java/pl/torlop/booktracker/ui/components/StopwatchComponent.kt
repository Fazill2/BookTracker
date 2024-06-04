package pl.torlop.booktracker.ui.components

import android.graphics.drawable.VectorDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import pl.torlop.booktracker.R
import pl.torlop.booktracker.utils.Utils.Companion.secondsToTimeString

@Composable
fun StopwatchComponent(time: Long, isRunning: Boolean, onStartStop: () -> Unit, onReset: () -> Unit, onSave: () -> Unit, modifier: Modifier = Modifier,
                       iconWidth: Int = 128, iconHeight: Int = 128){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        val icon: ImageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground)
        Image(
            imageVector = icon,
            contentDescription = "App icon",
            modifier = Modifier.size(iconWidth.dp).align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Fit
        )
        Text(
            text = secondsToTimeString(time),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp).align(
                Alignment.CenterHorizontally)
        )
        Row(
            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onStartStop) {
                Icon(
                    imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Start"
                )
            }
            Button(onClick = onReset) {
                Icon(
                    imageVector = Icons.Filled.RestartAlt,
                    contentDescription = "Reset")
            }
        }
        Button(
            onClick = onSave,
            enabled = !isRunning,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = "Save"
            )
        }
    }
}