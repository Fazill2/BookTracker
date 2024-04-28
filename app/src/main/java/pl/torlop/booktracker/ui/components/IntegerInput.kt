package pl.torlop.booktracker.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text

import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

@Composable
fun IntegerInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Integer Input",
    minValue: Int = Int.MIN_VALUE,
    maxValue: Int = Int.MAX_VALUE,
    enabled: Boolean = true
) {
    var textValue by remember { mutableStateOf(value.toString()) }

    TextField(
        value = textValue,
        onValueChange = { newValue ->
            if (newValue.isEmpty()) {
                textValue = "" // Pass null when input is empty
            } else {
                val parsedValue = newValue.toIntOrNull() ?: minValue
                if (parsedValue < minValue) {
                    textValue = minValue.toString()
                    onValueChange(minValue)
                } else if (parsedValue > maxValue) {
                    textValue = maxValue.toString()
                    onValueChange(maxValue)
                } else {
                    textValue = parsedValue.toString()
                    onValueChange(parsedValue)
                }
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        modifier = modifier,
        singleLine = true,
        enabled = enabled
    )
}