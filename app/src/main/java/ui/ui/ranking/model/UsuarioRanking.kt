package com.example.quiz.ui.ranking.model

// Data class para representar um utilizador na tela de ranking.
data class UsuarioRanking(
    val nomeUsuario: String = "",
    val pontuacaoTotal: Long = 0,
    val partidasJogadas: Long = 0 // <-- NOVO CAMPO ADICIONADO
)
