package com.example.quiz.ui.historico

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz.auth.GerenciadorAuth
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HistoricoViewModel : ViewModel() {

    val historicoList = mutableStateOf<List<Historico>>(emptyList())
    val isLoading = mutableStateOf(true)
    val errorMessage = mutableStateOf<String?>(null)

    init {
        carregarHistorico()
    }

    private fun carregarHistorico() {
        viewModelScope.launch {
            isLoading.value = true
            val idUsuario = GerenciadorAuth.getUsuarioAtual()?.uid

            if (idUsuario == null) {
                errorMessage.value = "Utilizador não encontrado."
                isLoading.value = false
                return@launch
            }

            try {
                val snapshot = Firebase.firestore
                    .collection("usuarios")
                    .document(idUsuario)
                    .collection("historico")
                    // Ordena os resultados pelos mais recentes primeiro
                    .orderBy("dataRealizacao", Query.Direction.DESCENDING)
                    .get()
                    .await()

                historicoList.value = snapshot.toObjects(Historico::class.java)
                errorMessage.value = null
            } catch (e: Exception) {
                Log.e("HistoricoViewModel", "Erro ao buscar histórico", e)
                errorMessage.value = "Falha ao carregar o histórico."
            } finally {
                isLoading.value = false
            }
        }
    }
}

