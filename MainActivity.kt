package com.myra.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("hi", "IN")
            }
        }
        setContent { MyraHome() }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    @Composable
    private fun MyraHome() {
        val context = LocalContext.current
        var heard by remember { mutableStateOf("Abhi tumhari baat sunne ka intezar hai…") }
        var reply by remember { mutableStateOf("Hi! Main MYRA hoon. Chalo, shuru karte hain.") }
        var listening by remember { mutableStateOf(false) }

        val speechLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            listening = false
            val text = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!text.isNullOrBlank()) {
                heard = text
                val lower = text.lowercase()
                reply = when {
                    "youtube" in lower -> {
                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.youtube.com"))
                        context.startActivity(intent)
                        "YouTube khol rahi hoon."
                    }
                    "chrome" in lower -> {
                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com"))
                        context.startActivity(intent)
                        "Browser khol rahi hoon."
                    }
                    "stop" in lower -> "Theek hai, main ruk gayi."
                    else -> "Maine suna: $text. Agle version mein AI brain connect karenge."
                }
                tts?.speak(reply, TextToSpeech.QUEUE_FLUSH, null, "myra_reply")
            }
        }

        val micPermission = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) startListening(speechLauncher) { listening = it }
            else reply = "Microphone permission ke bina main sun nahi sakti."
        }

        MaterialTheme(colorScheme = darkColorScheme(
            primary = Color(0xFFFF79B8),
            background = Color(0xFF090B16),
            surface = Color(0xFF171A2B)
        )) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color(0xFF11162C), Color(0xFF080912)))
                ).padding(22.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("MYRA", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    Text("Your personal AI companion", color = Color(0xFFB9BED5), fontSize = 13.sp)
                    Spacer(Modifier.height(34.dp))
                    Box(
                        modifier = Modifier.size(190.dp)
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFFF79B8))),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("M", color = Color.White, fontSize = 82.sp, fontWeight = FontWeight.Light)
                    }
                    Spacer(Modifier.height(28.dp))
                    Text(if (listening) "Sun rahi hoon…" else "Hello! Main MYRA hoon ❤️",
                        color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(18.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF191D31)),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Tumne kaha:", color = Color(0xFFFF9BCB), fontSize = 12.sp)
                            Text(heard, color = Color.White)
                            Spacer(Modifier.height(10.dp))
                            Text("MYRA:", color = Color(0xFFFF9BCB), fontSize = 12.sp)
                            Text(reply, color = Color.White)
                        }
                    }
                    Spacer(Modifier.height(22.dp))
                    Button(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                                == PackageManager.PERMISSION_GRANTED) {
                                startListening(speechLauncher) { listening = it }
                            } else micPermission.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(if (listening) "Listening…" else "🎙  MYRA se baat karo", fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Starter build • Features will be added step by step",
                        color = Color(0xFF9298B2), fontSize = 11.sp)
                }
            }
        }
    }

    private fun startListening(
        launcher: androidx.activity.result.ActivityResultLauncher<Intent>,
        onState: (Boolean) -> Unit
    ) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "MYRA ko apna command bolo")
        }
        try {
            onState(true)
            launcher.launch(intent)
        } catch (_: Exception) {
            onState(false)
        }
    }
}
