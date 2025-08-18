package com.example.quiz.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Define uma chave estrangeira para garantir que cada questão pertença a um quiz.
@Entity(
    tableName = "questoes",
    foreignKeys = [ForeignKey(
        entity = QuizEntity::class,
        parentColumns = ["id"],
        childColumns = ["quizId"],
        onDelete = ForeignKey.CASCADE // Se um quiz for apagado, as suas questões também serão.
    )]
)
data class QuestaoEntity(
    @PrimaryKey val id: String,
    val quizId: String, // Coluna para a chave estrangeira
    val pergunta: String,
    val opcoes: List<String>,
    val respostaCorreta: String
)