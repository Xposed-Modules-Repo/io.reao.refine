package org.miui.refine.ui.component

import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MIUISwitchColors(): SwitchColors {
    return SwitchDefaults.colors(
        checkedTrackColor = Color(0xFF0F79FF),
        uncheckedBorderColor = Color(0xFFE2E2E2),
        uncheckedTrackColor = Color(0xFFE2E2E2),
        uncheckedThumbColor = Color.White,
        disabledCheckedTrackColor = Color(0xFF7BB9FD),
        disabledCheckedThumbColor = Color(0xFFB9DDFD)
    )

}
