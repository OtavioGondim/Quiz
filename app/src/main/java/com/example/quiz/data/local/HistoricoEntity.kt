package com.example.quiz.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "historico")
data class HistoricoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Chave primária local
    val quizId: String,
    val quizTitulo: String,
    val pontuacao: Int,
    val totalQuestoes: Int,
    val dataRealizacao: Timestamp
)