package com.company.vncclient.ui.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.company.vncclient.service.VncForegroundService
import androidx.compose.foundation.Image
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.asImageBitmap
import com.company.vncclient.projection.ScreenFrameBus


@Composable
fun HomeScreen(
    onStartClick: () -> Unit){
    val screenFrame = ScreenFrameBus.frameFlow.collectAsState().value

    if (screenFrame != null) {
        Image(
            bitmap = screenFrame.asImageBitmap(),
            contentDescription = "Remote Screen",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(
                    screenFrame.width.toFloat() / screenFrame.height.toFloat()
                )
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
    val context = LocalContext.current

    var serverAddress by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf("9000") }
    var usePassword by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    var isRunning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Enterprise VNC Client",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = serverAddress,
            onValueChange = { serverAddress = it },
            label = { Text("Server Address") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = serverPort,
            onValueChange = { serverPort = it },
            label = { Text("Port") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = usePassword,
                onCheckedChange = { usePassword = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Use Password")
        }

        if (usePassword) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            ),
            onClick = {
                if (!isRunning) {
                    startVncService(context)
                } else {
                    stopVncService(context)
                }
                isRunning = !isRunning
            }
        ) {
            Text(if (isRunning) "Stop Monitoring" else "Start Monitoring")
        }
    }
}

private fun startVncService(context: Context) {
    val intent = Intent(context, VncForegroundService::class.java)
    context.startForegroundService(intent)
}

private fun stopVncService(context: Context) {
    val intent = Intent(context, VncForegroundService::class.java)
    context.stopService(intent)
}


