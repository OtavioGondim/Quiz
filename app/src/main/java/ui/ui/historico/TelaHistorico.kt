package com.example.quiz.ui.historico

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quiz.ui.theme.QuizTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaHistorico(
    onNavigateBack: () -> Unit,
    historicoViewModel: HistoricoViewModel = viewModel()
) {
    val historicoList by historicoViewModel.historicoList
    val isLoading by historicoViewModel.isLoading
    val errorMessage by historicoViewModel.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Histórico") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
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
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(text = errorMessage!!)
            } else if (historicoList.isEmpty()) {
                Text(text = "Você ainda não completou nenhum quiz.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historicoList) { historicoItem ->
                        HistoricoCard(historico = historicoItem)
                    }
                }
            }
        }
    }
}

// --- A FUNÇÃO QUE ESTAVA FALTANDO ---
@Composable
fun HistoricoCard(historico: Historico) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = historico.quizTitulo,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Formata a data para um formato mais legível
                val formattedDate = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
                    .format(historico.dataRealizacao.toDate())
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "${historico.pontuacao}/${historico.totalQuestoes}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaHistoricoPreview() {
    QuizTheme {
        TelaHistorico(onNavigateBack = {})
    }
}