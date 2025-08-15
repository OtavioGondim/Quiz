package com.example.quiz.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TelaExecucaoQuiz(
    quizId: String,
    onQuizFinished: () -> Unit,
    quizViewModel: QuizExecutionViewModel = viewModel()
) {
    // Carrega as questões quando a tela é exibida pela primeira vez
    LaunchedEffect(key1 = quizId) {
        quizViewModel.carregarQuestoes(quizId)
    }

    val questoes by quizViewModel.questoes
    val indiceQuestaoAtual by quizViewModel.indiceQuestaoAtual
    val pontuacao by quizViewModel.pontuacao
    val quizFinalizado by quizViewModel.quizFinalizado
    val isLoading by quizViewModel.isLoading
    val errorMessage by quizViewModel.errorMessage

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(text = errorMessage!!)
        } else if (quizFinalizado) {
            // Tela de resultado final
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Quiz Finalizado!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Sua pontuação: $pontuacao / ${questoes.size}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onQuizFinished) {
                    Text("Voltar para a lista de quizzes")
                }
            }
        } else if (questoes.isNotEmpty()) {
            // Tela da pergunta atual
            val questao = questoes[indiceQuestaoAtual]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pergunta ${indiceQuestaoAtual + 1} de ${questoes.size}",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = questao.pergunta,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Mostra as opções de resposta
                questao.opcoes.forEach { opcao ->
                    Button(
                        onClick = { quizViewModel.selecionarResposta(opcao) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(text = opcao)
                    }
                }
            }
        }
    }
}