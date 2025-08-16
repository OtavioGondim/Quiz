package com.example.quiz.ui.historico

import com.google.firebase.Timestamp

// Criado para representar um registo de histórico de quiz.
// Os nomes das variáveis correspondem aos campos que vamos salvar no Firestore.
data class Historico(
    val quizId: String = "",
    val quizTitulo: String = "",
    val pontuacao: Int = 0,
    val totalQuestoes: Int = 0,
    // Usamos o Timestamp do Firebase para guardar a data e hora exatas.
    val dataRealizacao: Timestamp = Timestamp.now()
)

