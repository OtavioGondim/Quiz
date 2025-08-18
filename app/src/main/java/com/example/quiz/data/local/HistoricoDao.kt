package com.example.quiz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoricoDao {

    // Insere um novo registo de histórico.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistorico(historico: HistoricoEntity)

    // Busca todos os registos de histórico, ordenados pelo mais recente.
    @Query("SELECT * FROM historico ORDER BY dataRealizacao DESC")
    suspend fun getAllHistorico(): List<HistoricoEntity>

    // Apaga todo o histórico.
    @Query("DELETE FROM historico")
    suspend fun clearAllHistorico()
}
