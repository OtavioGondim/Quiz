package com.example.quiz.ui.quiz

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quiz.auth.GerenciadorAuth
import com.example.quiz.ui.historico.Historico
import com.example.quiz.ui.quiz.model.Questao
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class QuizExecutionViewModel(application: Application) : AndroidViewModel(application) {

    // Estados da UI
    val questoes = mutableStateOf<List<Questao>>(emptyList())
    val indiceQuestaoAtual = mutableStateOf(0)
    val pontuacao = mutableStateOf(0)
    val quizFinalizado = mutableStateOf(false)
    val isLoading = mutableStateOf(true)
    val errorMessage = mutableStateOf<String?>(null)
    val quizTitulo = mutableStateOf("")

    private var quizId: String = ""

    fun carregarQuestoes(id: String) {
        if (questoes.value.isNotEmpty() && quizId == id) return

        quizId = id

        viewModelScope.launch {
            isLoading.value = true
            quizFinalizado.value = false
            indiceQuestaoAtual.value = 0
            pontuacao.value = 0

            try {
                val quizDoc = Firebase.firestore.collection("quizzes").document(quizId).get().await()
                quizTitulo.value = quizDoc.getString("titulo") ?: "Quiz Desconhecido"

                val snapshot = Firebase.firestore
                    .collection("quizzes")
                    .document(quizId)
                    .collection("questoes")
                    .get()
                    .await()

                questoes.value = snapshot.documents.mapNotNull { document ->
                    document.toObject(Questao::class.java)?.copy(id = document.id)
                }
                errorMessage.value = null
            } catch (e: Exception) {
                Log.e("QuizExecutionViewModel", "Erro ao buscar questões", e)
                errorMessage.value = "Falha ao carregar as perguntas."
            } finally {
                isLoading.value = false
            }
        }
    }

    fun selecionarResposta(respostaSelecionada: String) {
        if (quizFinalizado.value) return

        val questaoAtual = questoes.value[indiceQuestaoAtual.value]
        if (questaoAtual.respostaCorreta == respostaSelecionada) {
            pontuacao.value++
        }

        if (indiceQuestaoAtual.value < questoes.value.size - 1) {
            indiceQuestaoAtual.value++
        } else {
            quizFinalizado.value = true
            salvarResultado()
        }
    }

    private fun salvarResultado() {
        viewModelScope.launch {
            val historico = Historico(
                quizId = quizId,
                quizTitulo = quizTitulo.value,
                pontuacao = pontuacao.value,
                totalQuestoes = questoes.value.size,
                dataRealizacao = Timestamp.now()
            )

            val resultado = GerenciadorAuth.salvarResultadoQuiz(historico)
            resultado.onFailure { exception ->
                Log.e("QuizExecutionViewModel", "Falha ao salvar histórico", exception)
            }
        }
    }

    // Factory para permitir que a UI crie uma instância deste ViewModel
    companion object {
        fun provideFactory(
            application: Application
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(QuizExecutionViewModel::class.java)) {
                    return QuizExecutionViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}