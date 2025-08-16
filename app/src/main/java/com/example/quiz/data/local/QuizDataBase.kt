package com.example.quiz.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// @Database para definir a classe como o nosso banco de dados Room.
@Database(
    // Lista todas as nossas tabelas (Entidades).
    entities = [QuizEntity::class, QuestaoEntity::class, HistoricoEntity::class],
    // Versão do banco de dados. Se mudar a estrutura, precisa de incrementar este número.
    version = 1,
    // Exporta o schema para um ficheiro (boa prática).
    exportSchema = false
)
// Diz ao Room para usar a nossa classe de conversores.
@TypeConverters(Converters::class)
abstract class QuizDatabase : RoomDatabase() {

    // Fornece acesso aos nossos DAOs.
    abstract fun quizDao(): QuizDao
    abstract fun historicoDao(): HistoricoDao

    // Companion object para criar uma instância Singleton do banco de dados.
    // Isto garante que apenas uma instância do banco de dados exista em toda a aplicação.
    companion object {
        // @Volatile garante que a instância seja sempre a mais atualizada.
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context): QuizDatabase {
            // Retorna a instância existente ou cria uma nova se não existir.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database" // Nome do ficheiro do banco de dados no dispositivo.
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}