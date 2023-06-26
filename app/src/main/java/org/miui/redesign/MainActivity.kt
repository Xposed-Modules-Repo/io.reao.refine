package org.miui.redesign

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.miui.redesign.ui.theme.RedesignTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            val systemUiController = rememberSystemUiController()

            SideEffect {
                systemUiController.setSystemBarsColor(Color.White, darkIcons = true)
            }

            RedesignTheme {
                ProvideWindowInsets {
                    Scaffold(
                        contentWindowInsets = WindowInsets(0.dp),
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_redesign),
                                    contentDescription = "redesign",
                                    modifier = Modifier.height(15.dp).wrapContentWidth(),
                                    contentScale = ContentScale.FillHeight,
                                    colorFilter = ColorFilter.tint(Color.Gray),
                                )
                            }
                            )
                        }
                    ) { contentPadding ->

                        Box(modifier = Modifier.padding(contentPadding)) {
                            Greeting(
                                "port androidx.compose.ui.graphics.Color\n" +
                                        "import androidx.compose.ui.res.stringResource\n" +
                                        "import androidx.core.view.WindowCompat\n" +
                                        "import com.google.accompanist.insets.LocalWindowInsets\n" +
                                        "import com.google.accompanist.insets.ProvideWindowInsets\n" +
                                        "import com.google.accompanist.insets.navigationBarsHeight\n" +
                                        "import com.google.accompanist.insets.rememberInsetsPaddingValues\n" +
                                        "import com.google.accompanist.insets.ui.Scaffold\n" +
                                        "import com.google.accompanist.insets.ui.TopAppBar\n" +
                                        "import com.google.accompanist.sample.AccompanistSampleTheme\n" +
                                        "import com.google.accompanist.sample.R\n" +
                                        "import com.google.accompanist.systemuicontroller.rememberSystemU"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {

        Text(
            text = "Hello $name!",
            modifier = modifier
        )

        Image(
            painter = painterResource(id = R.drawable.ic_redesign),
            contentDescription = "redesign"
        )


    }
}

@Composable
fun ViewSetup() {
    val systemUiController = rememberSystemUiController()

    systemUiController.setSystemBarsColor(
        color = Color.White,
        darkIcons = true
    )

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RedesignTheme {
        Greeting("Android")
    }
}