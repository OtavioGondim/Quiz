package com.example.quiz.ui.quiz

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quiz.ui.quiz.model.Quiz
import com.example.quiz.ui.theme.QuizTheme

// Factory para permitir a criação do AndroidViewModel na UI
class QuizListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaListaQuizzes(
    onQuizSelected: (Quiz) -> Unit,
    onLogout: () -> Unit,
    onNavigateToHistorico: () -> Unit,
    onNavigateToRanking: () -> Unit,
    // Cria o ViewModel usando a Factory
    quizViewModel: QuizListViewModel = viewModel(
        factory = QuizListViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val quizzes by quizViewModel.quizzes
    val isLoading by quizViewModel.isLoading
    val errorMessage by quizViewModel.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quizzes Disponíveis") },
                actions = {
                    IconButton(onClick = onNavigateToHistorico) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Meu Histórico"
                        )
                    }
                    IconButton(onClick = onNavigateToRanking) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = "Ranking Global"
                        )
                    }
                    TextButton(onClick = onLogout) {
                        Text("Sair")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                errorMessage != null -> Text(text = errorMessage!!)
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quizzes) { quiz ->
                        QuizCard(quiz = quiz, onClick = { onQuizSelected(quiz) })
                    }
                }
            }
        }
    }
}

@Composable
fun QuizCard(quiz: Quiz, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = quiz.titulo,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = quiz.descricao,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaListaQuizzesPreview() {
    QuizTheme {
        TelaListaQuizzes(
            onQuizSelected = {},
            onLogout = {},
            onNavigateToHistorico = {},
            onNavigateToRanking = {}
        )
    }
}
