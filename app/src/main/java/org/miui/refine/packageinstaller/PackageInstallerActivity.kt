package org.miui.refine.packageinstaller


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
import org.miui.refine.ui.theme.refineTheme


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

        setContent {

            ViewSetup()

            val navController = rememberNavController()

            refineTheme {
                ProvideWindowInsets {
                    NavigationGraph(navController = navController)
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