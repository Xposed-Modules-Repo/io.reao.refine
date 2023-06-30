package org.miui.refine.packageinstaller

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import timber.log.Timber


@Composable
fun NavigationGraph(
    navController: NavHostController,
    data: String
) {

    Timber.d("NavigationGraph data: $data")
    NavHost(
        navController = navController,
        startDestination = "packageInstallerScreen/{data}"
    ) {
        composable("packageInstallerScreen/{data}",
            arguments = listOf(
                navArgument("data") {
                    type = NavType.StringType
                    defaultValue = data
                }
            )
        ) { it ->
            Timber.d("data: ${it.arguments?.getString("data")}")
            PackageInstallerScreen(
                navController,
                it.arguments?.getString("data")!!
            )
        }
        composable(
            "packageInfoScreen"
        ) {
            PackageInfoScreen(navController)
        }
    }
}