package io.reao.refine

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import io.reao.refine.ui.SettingActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
    }
}
