package com.example.quiz.ui.historico

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz.data.QuizRepository
import kotlinx.coroutines.launch

class HistoricoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuizRepository(application.applicationContext)

    val historicoList = mutableStateOf<List<Historico>>(emptyList())
    val isLoading = mutableStateOf(true)
    val errorMessage = mutableStateOf<String?>(null)

    init {
        carregarHistorico()
    }

    private fun carregarHistorico() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Busca o histórico através do repositório, que lê do banco de dados local.
                historicoList.value = repository.getHistorico()
                errorMessage.value = null
            } catch (e: Exception) {
                Log.e("HistoricoViewModel", "Erro ao buscar histórico local", e)
                errorMessage.value = "Falha ao carregar o histórico."
            } finally {
                isLoading.value = false
            }
        }
    }
}
