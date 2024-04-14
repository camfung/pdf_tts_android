package com.example.pdf_tts_android


import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.sql.Date

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainContent() {
    val context = LocalContext.current
    val formState = remember {
        FormState()
    }

    val voices = arrayOf("Alloy", "Echo", "Nova", "Shimmer", "Onyx", "Fable")
    val uriState = remember { mutableStateOf<Uri?>(null) }

    // Lambda function to update the Uri state
    val setUri: (Uri) -> Unit = { uri ->
        uriState.value = uri
    }

    Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(20.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Pdf to Speech Converter",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        )
        {

            SelectFileButton(
                label = "Select pdf",
                setUri = setUri
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        // Starting Page
        MyTextField(
            label = "Starting Page",
            value = formState.startingPage,
            onValueChanged = formState.startingPageOnChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        // End Page
        MyTextField(
            label = "End Page",
            value = formState.endPage,
            onValueChanged = formState.endPageOnChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        // MP3 File Name
        MyTextField(
            label = "MP3 File Name:\n(No File Ext)",
            value = formState.fileName,
            onValueChanged = formState.fileNameOnChanged
        )

        Spacer(modifier = Modifier.height(8.dp))

//          here for future feature
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Select Voice")
            MyExposedDropdownMenuBox(voices)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Play Sample Button
        Button(
            modifier = Modifier.width(150.dp),
            onClick = { /* TODO: Implement play sample functionality */ }) {
            Text("Play Sample")
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.width(150.dp),
                onClick = { /* TODO: Implement play sample functionality */ }) {
                Text("Estimate Price")
            }
            // Play Sample Button
            UploadButton(uriState = uriState, formState = formState)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // here for future feature
        val mp3Files = listOf(
            Pair(Date(2024, 4, 1), "audiobook1.mp3"),
            Pair(Date(2024, 4, 2), "audiobook2.mp3"),
            Pair(Date(2024, 4, 3), "audiobook3.mp3"),
            Pair(Date(2024, 4, 1), "audiobook1.mp3"),
            Pair(Date(2024, 4, 2), "audiobook2.mp3"),
            Pair(Date(2024, 4, 3), "audiobook3.mp3")
        )
        Mp3List(mp3Files, "MP3 Files")
    }
}




