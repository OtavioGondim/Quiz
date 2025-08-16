package com.example.quiz.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz.data.QuizRepository
import com.example.quiz.ui.quiz.model.Quiz
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch

class QuizListViewModel(application: Application) : AndroidViewModel(application) {

    // O repositório é instanciado aqui. Certifique-se de que a classe QuizRepository existe.
    private val repository = QuizRepository(application.applicationContext)

    // Definição dos estados que a UI vai observar.
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
                // A busca dos quizzes é delegada ao repositório.
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
