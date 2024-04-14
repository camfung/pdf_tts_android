package com.example.pdf_tts_android

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineExceptionHandler
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import kotlinx.coroutines.launch
import java.net.URLEncoder


data class UploadFileParams(
    val context: Context,
    val uri: Uri,
    val filename: String = "",
    val fileType: String = "",
    val requestUrl: String = "",
    val startingPage: String,
    val endingPage: String
)


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SelectFileButton(
    label: String,
    modifier: Modifier = Modifier,
    setUri: (Uri) -> Unit
) {
    val coroutineScope =
        rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result: Uri? ->
            coroutineScope.launch {
                result?.let { uri ->
                    setUri(uri)
                }
            }
        }
    Column {
        Button(modifier = Modifier.width(150.dp), onClick = {
            launcher.launch("application/pdf")
        }) {
            Text(label)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun UploadButton(uriState: MutableState<Uri?>, formState: FormState) {
    val context = LocalContext.current
    val requestUrl = stringResource(id = R.string.getaudio)
    val loadingState = remember { mutableStateOf(false) }
    val messageState = remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val errorHandler = CoroutineExceptionHandler { _, exception ->
        loadingState.value = false
        messageState.value = "Failed: ${exception.localizedMessage}"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            modifier = Modifier.width(150.dp),
            onClick = {
                uriState.value?.let { uri ->
                    coroutineScope.launch(errorHandler) {
                        loadingState.value = true
                        messageState.value = null
                        val success = uploadFile(
                            UploadFileParams(
                                context = context,
                                fileType = "application/pdf",
                                uri = uri,
                                requestUrl = requestUrl,
                                endingPage = formState.endPage,
                                startingPage = formState.startingPage,
                                filename = formState.fileName
                            )
                        )
                        loadingState.value = false
                        messageState.value =
                            if (success) "Audio generated successfully!\nSaved to Downloads folder" else "Failed to generate audio."
                    }
                }
            }
        ) {
            Text("Generate Audio")
        }

        if (loadingState.value) {
            CircularProgressIndicator()
        }

        messageState.value?.let { msg ->
            Text(msg)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
suspend fun uploadFile(params: UploadFileParams): Boolean {
    val client = HttpClient()
    return client.use { client ->
        params.context.contentResolver.openInputStream(params.uri)?.use { inputStream ->
            val pdfBytes = inputStream.readBytes()

            val response = client.submitFormWithBinaryData(
                url = buildString {
                    append(params.requestUrl)
                    append("?starting_page=${params.startingPage}")
                    append("&ending_page=${params.endingPage}")
                    append("&voice=alloy")
                    append("&file_name=${URLEncoder.encode(params.filename, "UTF-8")}")
                },
                formData = formData {
                    append("file", pdfBytes, Headers.build {
                        append(HttpHeaders.ContentType, params.fileType)
                        append(HttpHeaders.ContentDisposition, "filename=\"placeholder.pdf")
                    })
                }
            )

            if (response.status == HttpStatusCode.OK) {
                val mp3Bytes = response.readBytes()
                saveFile(params.context, mp3Bytes, params.filename)
                true
            } else {
                Log.e("HTTP Error", "Received HTTP status: ${response.status}")
                false
            }
        } ?: false
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun saveFile(context: Context, mp3Bytes: ByteArray, filename: String) {
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
    uri?.let {
        resolver.openOutputStream(it)?.use { outputStream ->
            outputStream.write(mp3Bytes)
        }
    }
}
