package com.example.quiz.data

import android.content.Context
import android.util.Log
import com.example.quiz.data.local.HistoricoEntity
import com.example.quiz.data.local.QuestaoEntity
import com.example.quiz.data.local.QuizDatabase
import com.example.quiz.data.local.QuizEntity
import com.example.quiz.ui.historico.Historico
import com.example.quiz.ui.quiz.model.Questao
import com.example.quiz.ui.quiz.model.Quiz
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class QuizRepository(context: Context) {

    private val quizDao = QuizDatabase.getDatabase(context).quizDao()
    private val historicoDao = QuizDatabase.getDatabase(context).historicoDao()
    private val firestore = Firebase.firestore

    // --- Funções para Quizzes ---

    suspend fun getQuizzes(): List<Quiz> {
        // Tenta buscar do banco local primeiro
        val localQuizzes = quizDao.getAllQuizzes().map { it.toModel() }

        // Se o banco local estiver vazio, busca do Firebase
        if (localQuizzes.isEmpty()) {
            Log.d("QuizRepository", "Banco de dados local vazio, buscando do Firebase...")
            return fetchQuizzesFromFirebase()
        }
        return localQuizzes
    }

    private suspend fun fetchQuizzesFromFirebase(): List<Quiz> {
        return try {
            val snapshot = firestore.collection("quizzes").get().await()
            val quizzes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Quiz::class.java)?.copy(id = doc.id)
            }
            // Salva os quizzes buscados no banco de dados local para uso futuro
            quizDao.insertQuizzes(quizzes.map { it.toEntity() })
            quizzes
        } catch (e: Exception) {
            Log.e("QuizRepository", "Erro ao buscar quizzes do Firebase", e)
            emptyList()
        }
    }

    // --- Funções para Questões ---

    suspend fun getQuestoesForQuiz(quizId: String): List<Questao> {
        val localQuestoes = quizDao.getQuestoesForQuiz(quizId).map { it.toModel() }
        if (localQuestoes.isEmpty()) {
            Log.d("QuizRepository", "Questões locais não encontradas para o quiz $quizId, buscando do Firebase...")
            return fetchQuestoesFromFirebase(quizId)
        }
        return localQuestoes
    }

    private suspend fun fetchQuestoesFromFirebase(quizId: String): List<Questao> {
        return try {
            val snapshot = firestore.collection("quizzes").document(quizId)
                .collection("questoes").get().await()
            val questoes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Questao::class.java)?.copy(id = doc.id)
            }
            quizDao.insertQuestoes(questoes.map { it.toEntity(quizId) })
            questoes
        } catch (e: Exception) {
            Log.e("QuizRepository", "Erro ao buscar questões do Firebase", e)
            emptyList()
        }
    }

    // --- Funções para Histórico ---

    suspend fun getHistorico(): List<Historico> {
        return historicoDao.getAllHistorico().map { it.toModel() }
    }

    suspend fun saveHistorico(historico: Historico) {
        historicoDao.insertHistorico(historico.toEntity())
        // A lógica para salvar no Firebase continua no GerenciadorAuth para manter a consistência
    }

    // --- Funções de conversão ---

    private fun QuizEntity.toModel() = Quiz(id, titulo, descricao, corDeFundo)
    private fun Quiz.toEntity() = QuizEntity(id, titulo, descricao, corDeFundo)

    private fun QuestaoEntity.toModel() = Questao(id, pergunta, opcoes, respostaCorreta)
    private fun Questao.toEntity(quizId: String) = QuestaoEntity(id, quizId, pergunta, opcoes, respostaCorreta)

    private fun HistoricoEntity.toModel() = Historico(quizId, quizTitulo, pontuacao, totalQuestoes, dataRealizacao)
    private fun Historico.toEntity() = HistoricoEntity(quizId = quizId, quizTitulo = quizTitulo, pontuacao = pontuacao, totalQuestoes = totalQuestoes, dataRealizacao = dataRealizacao)
}