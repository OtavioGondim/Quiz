package com.example.quiz.ui.ranking

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz.ui.ranking.model.UsuarioRanking
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RankingViewModel : ViewModel() {

    val rankingList = mutableStateOf<List<UsuarioRanking>>(emptyList())
    val isLoading = mutableStateOf(true)
    val errorMessage = mutableStateOf<String?>(null)

    init {
        carregarRanking()
    }

    private fun carregarRanking() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Busca a coleção de utilizadores
                val snapshot = Firebase.firestore
                    .collection("usuarios")
                    // Ordena pela pontuação total, do maior para o menor
                    .orderBy("pontuacaoTotal", Query.Direction.DESCENDING)
                    // Limita aos 20 melhores para não sobrecarregar o app
                    .limit(20)
                    .get()
                    .await()

                // Converte os documentos para a nossa lista de objetos UsuarioRanking
                rankingList.value = snapshot.toObjects(UsuarioRanking::class.java)
                errorMessage.value = null
            } catch (e: Exception) {
                Log.e("RankingViewModel", "Erro ao buscar ranking", e)
                errorMessage.value = "Falha ao carregar o ranking."
            } finally {
                isLoading.value = false
            }
        }
    }
}