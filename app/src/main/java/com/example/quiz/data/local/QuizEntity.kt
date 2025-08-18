package com.example.quiz.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Anotação @Entity para dizer ao Room que esta classe é uma tabela.
@Entity(tableName = "quizzes")
data class QuizEntity(
    // @PrimaryKey para definir a chave primária da tabela.
    @PrimaryKey val id: String,
    val titulo: String,
    val descricao: String,
    val corDeFundo: String
)