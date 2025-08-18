package com.example.quiz.ui.quiz.model


// Os nomes das variáveis correspondem aos campos no Firestore.
data class Questao(
    val id: String = "",
    val pergunta: String = "",
    val opcoes: List<String> = emptyList(),
    val respostaCorreta: String = ""
)