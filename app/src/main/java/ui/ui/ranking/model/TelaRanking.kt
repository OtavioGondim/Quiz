package com.example.quiz.ui.ranking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.quiz.ui.ranking.model.UsuarioRanking
import com.example.quiz.ui.theme.QuizTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRanking(
    onNavigateBack: () -> Unit,
    rankingViewModel: RankingViewModel = viewModel()
) {
    val rankingList by rankingViewModel.rankingList
    val isLoading by rankingViewModel.isLoading
    val errorMessage by rankingViewModel.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ranking Global") },
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
            } else if (rankingList.isEmpty()) {
                Text(text = "O ranking ainda está vazio.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(rankingList) { index, usuario ->
                        RankingCard(posicao = index + 1, usuario = usuario)
                    }
                }
            }
        }
    }
}

@Composable
fun RankingCard(posicao: Int, usuario: UsuarioRanking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$posicao.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = usuario.nomeUsuario,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${usuario.partidasJogadas} partidas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${usuario.pontuacaoTotal} pts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaRankingPreview() {
    QuizTheme {
        TelaRanking(onNavigateBack = {})
    }
}