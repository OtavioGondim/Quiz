package com.example.quiz.auth

import android.util.Log
import com.example.quiz.ui.historico.Historico
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object GerenciadorAuth {

    private const val TAG = "GerenciadorAuth"
    private const val COLECAO_USUARIOS = "usuarios"
    private const val SUBCOLECAO_HISTORICO = "historico"

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    fun getUsuarioAtual(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun cadastrarUsuario(email: String, senha: String, nomeUsuario: String): Result<FirebaseUser> {
        return try {
            val resultadoAuth = auth.createUserWithEmailAndPassword(email, senha).await()
            val usuarioFirebase = resultadoAuth.user!!
            salvarPerfilUsuario(usuarioFirebase.uid, email, nomeUsuario)
            Log.d(TAG, "Usuário criado e perfil salvo com sucesso: ${usuarioFirebase.uid}")
            Result.success(usuarioFirebase)
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao criar usuário ou salvar perfil", e)
            Result.failure(e)
        }
    }

    suspend fun fazerLogin(email: String, senha: String): Result<FirebaseUser> {
        return try {
            val resultadoAuth = auth.signInWithEmailAndPassword(email, senha).await()
            val usuarioFirebase = resultadoAuth.user!!
            Log.d(TAG, "Login bem-sucedido: ${usuarioFirebase.uid}")
            Result.success(usuarioFirebase)
        } catch (e: Exception) {
            Log.e(TAG, "Falha no login", e)
            Result.failure(e)
        }
    }

    private suspend fun salvarPerfilUsuario(idUsuario: String, email: String, nomeUsuario: String) {
        val perfilUsuario = hashMapOf(
            "uid" to idUsuario,
            "nomeUsuario" to nomeUsuario,
            "email" to email,
            "criadoEm" to System.currentTimeMillis(),
            "pontuacaoTotal" to 0,
            "partidasJogadas" to 0 // <-- NOVO CAMPO ADICIONADO
        )
        db.collection(COLECAO_USUARIOS).document(idUsuario).set(perfilUsuario).await()
    }

    suspend fun salvarResultadoQuiz(historico: Historico): Result<Unit> {
        val idUsuario = auth.currentUser?.uid
        if (idUsuario == null) {
            return Result.failure(Exception("Nenhum utilizador logado para salvar o histórico."))
        }

        return try {
            val userDocRef = db.collection(COLECAO_USUARIOS).document(idUsuario)

            db.runBatch { batch ->
                val historicoRef = userDocRef.collection(SUBCOLECAO_HISTORICO).document()
                batch.set(historicoRef, historico)

                // Atualiza a pontuação total
                batch.update(userDocRef, "pontuacaoTotal", FieldValue.increment(historico.pontuacao.toLong()))
                // Incrementa o número de partidas jogadas
                batch.update(userDocRef, "partidasJogadas", FieldValue.increment(1)) // <-- NOVA ATUALIZAÇÃO
            }.await()

            Log.d(TAG, "Histórico e pontuação total atualizados com sucesso para o utilizador: $idUsuario")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao salvar histórico e pontuação", e)
            Result.failure(e)
        }
    }

    fun fazerLogout() {
        auth.signOut()
        Log.d(TAG, "Usuário deslogado.")
    }
}
