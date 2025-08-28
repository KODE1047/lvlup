// app/src/main/java/com/example/lvlup/ui/navigation/AppNavigation.kt

package com.example.lvlup.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lvlup.ui.screens.AddEditTaskScreen
import com.example.lvlup.ui.screens.AnalyticsScreen
import com.example.lvlup.ui.screens.TaskListScreen
import com.example.lvlup.ui.viewmodel.TaskViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val taskViewModel: TaskViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "taskList") {
        composable("taskList") {
            TaskListScreen(
                viewModel = taskViewModel,
                onAddTask = { navController.navigate("addEditTask") },
                onEditTask = { taskId -> navController.navigate("addEditTask?taskId=$taskId") },
                onNavigateToAnalytics = { navController.navigate("analytics") }
            )
        }
        composable(
            route = "addEditTask?taskId={taskId}",
            arguments = listOf(navArgument("taskId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            AddEditTaskScreen(
                viewModel = taskViewModel,
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("analytics") {
            AnalyticsScreen(
                viewModel = taskViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}