package io.reao.refine.packageinstaller


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.reao.refine.ui.theme.refineTheme
import java.net.URLEncoder


class PackageInstallerActivity : ComponentActivity() {

    companion object {
        val SCHEME_PACKAGE = "package"
        val TAG = PackageInstallerActivity::class.java.simpleName
        lateinit var instance: PackageInstallerActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        instance = this

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val data = URLEncoder.encode(intent.data!!.toString(), "UTF-8")
        setContent {

            ViewSetup()

            val navController = rememberNavController()

            refineTheme {
                ProvideWindowInsets {
                    NavigationGraph(navController = navController, data = data)
                }
            }
        }
    }
}

@Composable
fun ViewSetup() {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White, darkIcons = true
        )
    }
}