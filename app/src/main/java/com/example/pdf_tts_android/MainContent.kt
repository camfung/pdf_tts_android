package com.example.pdf_tts_android


import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.util.InternalAPI
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    val context = LocalContext.current
    val formState = remember {
        FormState()
    }

    val voices = arrayOf("Alloy", "Echo", "Nova", "Shimmer", "Onyx", "Fable")

    val pickPdfResult =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Log.d("URI", uri.toString())
//                val inputStream = context.assets.open(uri.)

            }
        }
    Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(20.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {

            Text(
                text = "Pdf to Speech Converter",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        )
        {

            SelectFileButton(
                label = "Select pdf", fileType = "application/pdf", requestUrl = stringResource(
                    com.example.pdf_tts_android.R.string.upload_url
                )
            )
            // Choose Folder Button
            Button(onClick = { /* TODO: Implement folder selection */ }) {
                Icon(Icons.Filled.Menu, contentDescription = "Select Save Location")
                Text("Select Save Location")
            }
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
            label = "MP3 File Name",
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
        Button(onClick = { /* TODO: Implement play sample functionality */ }) {
            Text("Play Sample")
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { /* TODO: Implement play sample functionality */ }) {
                Text("Estimate Price")
            }
            // Play Sample Button
            Button(onClick = { }) {
                Text("Generate Mp3 File")
            }
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

@Composable
fun Mp3List(mp3Files: List<Pair<Date, String>>, title: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(border = BorderStroke(1.dp, Color.LightGray)),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(mp3Files) { (creationDate, mp3FileName) ->
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Date Created: $creationDate",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "File Name: $mp3FileName",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MyTextField(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit,
    valid: Boolean = true
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "$label:")
        OutlinedTextField(
            value = value,
            onValueChange = onValueChanged,
            label = { Text(label) },
            isError = !valid,
            modifier = Modifier.width(250.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyExposedDropdownMenuBox(voices: Array<String>) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(voices[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.width(250.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            voices.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        selectedText = item
                        expanded = false
                        Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}


// here I was trying to make the selectFileButton and this callback function less coupled. I was
// spending too much time on it so i just left them coupled :(
data class UploadFileParams(
    val context: Context,
    val uri: Uri,
    val filename: String = "",
    val fileType: String = "",
    val requestUrl: String = ""
)

suspend fun uploadFile(
    params: UploadFileParams
) {
    params.context.contentResolver.openInputStream(params.uri)?.use { inputStream ->
        val pdfBytes = inputStream.readBytes()
        Log.d("pdfbytes" , pdfBytes.toString())
        val client = HttpClient(Android) {
            defaultRequest {
                // header("key", "value")
            }

        }
        val response: HttpResponse = client.submitFormWithBinaryData(
            url = params.requestUrl,
            formData = formData {
                append("file", pdfBytes, Headers.build {
                    append(HttpHeaders.ContentType, params.fileType)
                    append(HttpHeaders.ContentDisposition, "filename=\"${params.filename}\"")
                })
            }
        )
        val a = response.body<String>()
        Log.d("Response status", response.status.toString())
        Log.d("message", a)
    }

}

@Composable
private fun SelectFileButton(
    label: String,
    fileType: String,
    modifier: Modifier = Modifier,
    fileName: String = "",
    requestUrl: String = "",
) {
    val context = LocalContext.current
    val coroutineScope =
        rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result: Uri? ->
            coroutineScope.launch {
                result?.let { uri ->
                    uploadFile(UploadFileParams(context, uri, fileName, fileType, requestUrl))
                }
            }
        }
    Column {
        Button(onClick = {
            launcher.launch(fileType)
        }, modifier = modifier) {
            Text(label)
        }
    }
}

