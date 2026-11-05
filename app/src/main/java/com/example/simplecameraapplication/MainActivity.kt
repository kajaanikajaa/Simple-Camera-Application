package com.example.simplecameraapplication

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CameraScreen()
        }
    }

    @Composable
    fun CameraScreen() {

        var capturedImage by remember {
            mutableStateOf<Bitmap?>(null)
        }

        // Open camera and capture photo
        val cameraLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) { bitmap ->

                if (bitmap != null) {
                    capturedImage = bitmap
                }
            }

        // Camera screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Simple Camera App",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Display captured image
            if (capturedImage != null) {

                Image(
                    bitmap = capturedImage!!.asImageBitmap(),
                    contentDescription = "Captured Photo",
                    modifier = Modifier
                        .size(250.dp)
                )

            } else {

                Text(text = "No photo captured yet")

            }

            Spacer(modifier = Modifier.height(30.dp))

            // Capture button
            Button(
                onClick = {
                    cameraLauncher.launch(null)
                }
            ) {
                Text("Open Camera")
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Share button
            Button(
                onClick = {

                    capturedImage?.let { bitmap ->

                        val file = File(
                            cacheDir,
                            "captured_image.jpg"
                        )

                        val outputStream =
                            FileOutputStream(file)

                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            100,
                            outputStream
                        )

                        outputStream.close()

                        val imageUri =
                            FileProvider.getUriForFile(
                                this@MainActivity,
                                "$packageName.fileprovider",
                                file
                            )

                        val shareIntent =
                            Intent(Intent.ACTION_SEND).apply {

                                type = "image/jpeg"

                                putExtra(
                                    Intent.EXTRA_STREAM,
                                    imageUri
                                )

                                addFlags(
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                )
                            }

                        startActivity(
                            Intent.createChooser(
                                shareIntent,
                                "Share Image"
                            )
                        )
                    }
                },
                enabled = capturedImage != null
            ) {
                Text("Share Photo")
            }
        }
    }
}