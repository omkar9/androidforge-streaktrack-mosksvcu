package com.androidforge.streaktrack.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.androidforge.streaktrack.presentation.habits.add_edit_habit.AddEditHabitScreen
import com.androidforge.streaktrack.presentation.habits.detail.HabitDetailScreen
import com.androidforge.streaktrack.presentation.habits.list.HabitListScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HabitList.route,
        modifier = modifier,
        enterTransition = { enterTransition() },
        exitTransition = { exitTransition() },
        popEnterTransition = { popEnterTransition() },
        popExitTransition = { popExitTransition() }
    ) {
        composable(route = Screen.HabitList.route) {
            HabitListScreen(
                onNavigateToAddEditHabit = { habitId ->
                    navController.navigate(Screen.AddEditHabit.createRoute(habitId))
                },
                onNavigateToHabitDetail = { habitId ->
                    navController.navigate(Screen.HabitDetail.createRoute(habitId))
                }
            )
        }
        composable(
            route = Screen.AddEditHabit.route,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId")
            AddEditHabitScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.HabitDetail.route,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType; nullable = false })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            HabitDetailScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddEditHabit = { id ->
                    navController.navigate(Screen.AddEditHabit.createRoute(id))
                }
            )
        }
    }
}

private fun AnimatedContentTransitionScope<*>.enterTransition() =
    slideInHorizontally(animationSpec = tween(400), initialOffsetX = { it }) + fadeIn(animationSpec = tween(400))

private fun AnimatedContentTransitionScope<*>.exitTransition() =
    slideOutHorizontally(animationSpec = tween(400), targetOffsetX = { -it }) + fadeOut(animationSpec = tween(400))

private fun AnimatedContentTransitionScope<*>.popEnterTransition() =
    slideInHorizontally(animationSpec = tween(400), initialOffsetX = { -it }) + fadeIn(animationSpec = tween(400))

private fun AnimatedContentTransitionScope<*>.popExitTransition() =
    slideOutHorizontally(animationSpec = tween(400), targetOffsetX = { it }) + fadeOut(animationSpec = tween(400))