package com.example.quiz.ui.quiz.model


// Os nomes das variáveis correspondem exatamente aos nomes dos campos no Firestore.
data class Quiz(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val corDeFundo: String = ""
)

