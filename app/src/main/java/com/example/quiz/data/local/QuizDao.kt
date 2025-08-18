package com.example.quiz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

// @Dao para dizer ao Room que esta interface é um Data Access Object.
@Dao
interface QuizDao {

    // --- Operações para Quizzes ---

    // Insere uma lista de quizzes. Se um quiz com o mesmo ID já existir, ele será substituído.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<QuizEntity>)

    // Busca todos os quizzes da tabela.
    @Query("SELECT * FROM quizzes")
    suspend fun getAllQuizzes(): List<QuizEntity>

    // --- Operações para Questões ---

    // Insere uma lista de questões.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestoes(questoes: List<QuestaoEntity>)

    // Busca todas as questões de um quiz específico, usando o quizId.
    @Query("SELECT * FROM questoes WHERE quizId = :quizId")
    suspend fun getQuestoesForQuiz(quizId: String): List<QuestaoEntity>

    // Apaga todas as questões de um quiz específico. Útil para sincronização.
    @Query("DELETE FROM questoes WHERE quizId = :quizId")
    suspend fun deleteQuestoesForQuiz(quizId: String)
}