package com.example.quiz.ui.quiz

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz.ui.quiz.model.Questao
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class QuizExecutionViewModel : ViewModel() {

    // Estados da UI
    val questoes = mutableStateOf<List<Questao>>(emptyList())
    val indiceQuestaoAtual = mutableStateOf(0)
    val pontuacao = mutableStateOf(0)
    val quizFinalizado = mutableStateOf(false)
    val isLoading = mutableStateOf(true)
    val errorMessage = mutableStateOf<String?>(null)

    // Função para buscar as perguntas de um quiz específico
    fun carregarQuestoes(quizId: String) {
        if (questoes.value.isNotEmpty()) return // Já carregou

        viewModelScope.launch {
            isLoading.value = true
            quizFinalizado.value = false
            indiceQuestaoAtual.value = 0
            pontuacao.value = 0

            try {
                // Acessa a sub-coleção "questoes" dentro do quiz selecionado
                val snapshot = Firebase.firestore
                    .collection("quizzes")
                    .document(quizId)
                    .collection("questoes")
                    .get()
                    .await()

                // Converte os documentos para a nossa lista de objetos Questao
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

    // Função chamada quando o utilizador seleciona uma resposta
    fun selecionarResposta(respostaSelecionada: String) {
        if (quizFinalizado.value) return

        val questaoAtual = questoes.value[indiceQuestaoAtual.value]
        if (questaoAtual.respostaCorreta == respostaSelecionada) {
            // Se a resposta estiver correta, aumenta a pontuação
            pontuacao.value++
        }

        // Passa para a próxima pergunta ou finaliza o quiz
        if (indiceQuestaoAtual.value < questoes.value.size - 1) {
            indiceQuestaoAtual.value++
        } else {
            quizFinalizado.value = true
        }
    }
}

