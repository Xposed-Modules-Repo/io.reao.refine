package org.miui.refine.packageinstaller

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument


@Composable
fun NavigationGraph(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = "packageInstallerScreen") {
        composable("packageInstallerScreen//{intentData}",
            arguments = listOf(
                navArgument("intentData") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            PackageInstallerScreen(navController,backStackEntry.arguments?.getString("intentData")!!)
        }
        composable(
            "packageInfoScreen"
        ) {
            PackageInfoScreen(navController)
        }
    }
}