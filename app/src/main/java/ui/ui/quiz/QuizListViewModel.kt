package com.example.quiz.ui.quiz

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz.ui.quiz.model.Quiz
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class QuizListViewModel : ViewModel() {


    // O `mutableStateOf` garante que a UI será atualizada quando a lista mudar.
    val quizzes = mutableStateOf<List<Quiz>>(emptyList())
    val isLoading = mutableStateOf(true)
    val errorMessage = mutableStateOf<String?>(null)

    init {
        // Quando o ViewModel é criado, ele imediatamente começa a buscar os quizzes.
        fetchQuizzes()
    }

    private fun fetchQuizzes() {
        // Usamos viewModelScope para garantir que a busca seja cancelada se o ViewModel for destruído.
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Acessa a coleção "quizzes" no Firestore.
                val snapshot = Firebase.firestore.collection("quizzes").get().await()

                // Converte os documentos do Firebase para a nossa lista de objetos Quiz.
                val quizList = snapshot.documents.mapNotNull { document ->
                    // Adicionamos o ID do documento ao nosso objeto.
                    document.toObject(Quiz::class.java)?.copy(id = document.id)
                }

                quizzes.value = quizList
                errorMessage.value = null
            } catch (e: Exception) {
                // Se der erro, guardamos a mensagem para exibir na tela.
                Log.e("QuizListViewModel", "Erro ao buscar quizzes", e)
                errorMessage.value = "Falha ao carregar os quizzes. Tente novamente."
            } finally {
                isLoading.value = false
            }
        }
    }
}
