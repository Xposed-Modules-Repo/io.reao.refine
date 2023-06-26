package org.miui.refine.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MIUIButton(
    text: String,
    clickCallback: () -> Unit
) {
    Box(modifier = Modifier) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { clickCallback() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xfff0f0f0), contentColor = Color.Black
            )
        ) {
            Text(
                modifier = Modifier.padding(vertical = 5.dp),
                text = text,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}