package com.example.quiz.ui.quiz

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quiz.ui.quiz.model.Questao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaExecucaoQuiz(
    quizId: String?,
    onQuizFinished: () -> Unit,
    // CORREÇÃO: O ViewModel correto é o QuizExecutionViewModel
    viewModel: QuizExecutionViewModel = viewModel(
        factory = QuizExecutionViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    // Carrega as questões quando a tela é exibida pela primeira vez
    LaunchedEffect(quizId) {
        if (quizId != null) {
            viewModel.carregarQuestoes(quizId)
        }
    }

    // Observa os estados do ViewModel
    val questoes by viewModel.questoes
    val indiceQuestaoAtual by viewModel.indiceQuestaoAtual
    val pontuacao by viewModel.pontuacao
    val quizFinalizado by viewModel.quizFinalizado
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val quizTitulo by viewModel.quizTitulo

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quizTitulo.ifEmpty { "Executando Quiz" }) },
                navigationIcon = {
                    if (!quizFinalizado) {
                        IconButton(onClick = onQuizFinished) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar"
                            )
                        }
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
                isLoading -> {
                    CircularProgressIndicator()
                }
                errorMessage != null -> {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
                quizFinalizado -> {
                    // Tela de resultado final
                    ResultadoQuiz(
                        pontuacao = pontuacao,
                        totalQuestoes = questoes.size,
                        onVoltarClick = onQuizFinished
                    )
                }
                questoes.isNotEmpty() -> {
                    // Mostra a questão atual
                    val questaoAtual = questoes[indiceQuestaoAtual]
                    ConteudoQuestao(
                        questao = questaoAtual,
                        numeroQuestao = indiceQuestaoAtual + 1,
                        totalQuestoes = questoes.size,
                        onRespostaSelecionada = { resposta ->
                            viewModel.selecionarResposta(resposta)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ConteudoQuestao(
    questao: Questao,
    numeroQuestao: Int,
    totalQuestoes: Int,
    onRespostaSelecionada: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Questão $numeroQuestao de $totalQuestoes",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = questao.pergunta,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Botões de resposta
        questao.opcoes.forEach { opcao ->
            Button(
                onClick = { onRespostaSelecionada(opcao) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(text = opcao)
            }
        }
    }
}

@Composable
fun ResultadoQuiz(pontuacao: Int, totalQuestoes: Int, onVoltarClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Quiz Finalizado!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Sua pontuação: $pontuacao de $totalQuestoes", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onVoltarClick) {
            Text("Voltar para a lista")
        }
    }
}