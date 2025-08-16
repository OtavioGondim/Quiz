package com.example.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quiz.auth.GerenciadorAuth
import com.example.quiz.ui.cadastro.TelaCadastro
import com.example.quiz.ui.historico.TelaHistorico
import com.example.quiz.ui.login.TelaLogin
import com.example.quiz.ui.quiz.TelaExecucaoQuiz
import com.example.quiz.ui.quiz.TelaListaQuizzes
import com.example.quiz.ui.ranking.TelaRanking
import com.example.quiz.ui.theme.QuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            TelaLogin(
                onLoginSuccess = {
                    navController.navigate("quiz_list") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToCadastro = {
                    navController.navigate("cadastro")
                }
            )
        }

        composable("cadastro") {
            TelaCadastro(
                onCadastroSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("quiz_list") {
            TelaListaQuizzes(
                onQuizSelected = { quiz ->
                    navController.navigate("quiz_execution/${quiz.id}")
                },
                onLogout = {
                    GerenciadorAuth.fazerLogout()
                    navController.navigate("login") { popUpTo(0) }
                },
                onNavigateToHistorico = {
                    navController.navigate("historico")
                },
                // Passa a ação de navegação para a TelaListaQuizzes
                onNavigateToRanking = {
                    navController.navigate("ranking")
                }
            )
        }

        composable(
            route = "quiz_execution/{quizId}",
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")
            if (quizId != null) {
                TelaExecucaoQuiz(
                    quizId = quizId,
                    onQuizFinished = {
                        // Volta para a lista de quizzes quando o quiz terminar
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("historico") {
            TelaHistorico(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Nova rota para a tela de ranking
        composable("ranking") {
            TelaRanking(
                onNavigateBack = {
                    // Ação para o botão de voltar
                    navController.popBackStack()
                }
            )
        }
    }
}
