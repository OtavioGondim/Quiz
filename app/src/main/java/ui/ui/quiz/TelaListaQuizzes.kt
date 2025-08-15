package com.example.quiz.ui.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quiz.ui.quiz.model.Quiz
import com.example.quiz.ui.theme.QuizTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaListaQuizzes(
    onQuizSelected: (Quiz) -> Unit,
    onLogout: () -> Unit,
    // Pede uma instância do nosso ViewModel.
    quizViewModel: QuizListViewModel = viewModel()
) {
    // Observa os estados do ViewModel. A UI irá reagir a qualquer mudança aqui.
    val quizzes by quizViewModel.quizzes
    val isLoading by quizViewModel.isLoading
    val errorMessage by quizViewModel.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quizzes Disponíveis") },
                actions = {
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
            if (isLoading) {
                // Mostra um indicador de progresso enquanto os dados são carregados.
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                // Mostra uma mensagem de erro se a busca falhar.
                Text(text = errorMessage!!)
            } else {
                // Mostra a lista de quizzes quando os dados estiverem prontos.
                LazyColumn(
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
        // O preview não precisa do ViewModel real.
        TelaListaQuizzes(onQuizSelected = {}, onLogout = {})
    }
}