package com.example.quiz.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz.data.QuizRepository
import com.example.quiz.ui.quiz.model.Quiz
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch

// Alterado para AndroidViewModel para ter acesso ao Contexto da Aplicação
class QuizListViewModel(application: Application) : AndroidViewModel(application) {

    // O ViewModel agora usa o Repositório como única fonte de dados.
    private val repository = QuizRepository(application.applicationContext)

    // Os estados da UI continuam os mesmos
    val quizzes = mutableStateOf<List<Quiz>>(emptyList())
    val isLoading = mutableStateOf(true)
    val errorMessage = mutableStateOf<String?>(null)

    init {
        fetchQuizzes()
    }

    private fun fetchQuizzes() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Agora busca os quizzes através do repositório
                quizzes.value = repository.getQuizzes()
                errorMessage.value = null
            } catch (e: Exception) {
                errorMessage.value = "Falha ao carregar os quizzes."
            } finally {
                isLoading.value = false
            }
        }
    }
}
