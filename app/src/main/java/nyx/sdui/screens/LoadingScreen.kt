package nyx.sdui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nyx.sdui.ui.theme.Teal200

@Composable
fun LoadingScreen() {
    val infoTexts = listOf(
        "I like you.",
        "How is it?",
        "Tap me!",
        "You deserve it <3",
        "Sei meraviglioso!",
        "Ti voglio bene <3",
        "Your own app!",
        "Do you like this?",
        "You are cute af",
        "Hug? :3",
        "More Lasagna?",
        "My Gnocchi goddess!"
    )

    var infoText by remember { mutableStateOf(infoTexts[0]) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Teal200)
            .clickable { infoText = infoTexts.random() },
        Arrangement.Center,
        Alignment.CenterHorizontally,
    ) {
        Text(
            infoText,
            color = White,
            fontSize = 16.sp
        )

        CircularProgressIndicator(
            Modifier
                .size(20.dp)
                .padding(top = 16.dp), strokeWidth = 2.dp
        )
    }
}