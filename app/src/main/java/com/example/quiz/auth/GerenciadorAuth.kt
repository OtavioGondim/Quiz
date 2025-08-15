package com.example.quiz.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Uma classe Singleton para gerenciar a autenticação e os dados do usuário com o Firebase.
 * Isso ajuda a centralizar toda a lógica de autenticação em um único lugar.
 */
object GerenciadorAuth {

    // -- VARIÁVEIS E SUAS FUNÇÕES --

    /**
     * @property //TAG
     * Constante para uso em logs (Log.d, Log.e). Facilita a filtragem e depuração de mensagens
     * específicas desta classe no Logcat do Android Studio.
     */
    private const val TAG = "GerenciadorAuth"

    /**
     * @property COLECAO_USUARIOS
     * Constante que define o nome da coleção no Cloud Firestore. É nesta coleção que os perfis
     * de todos os usuários (com dados como nomeUsuario, email, etc.) serão armazenados.
     */
    private const val COLECAO_USUARIOS = "usuarios"

    /**
     * @property auth
     * Instância do Firebase Authentication. É o principal objeto para interagir com o serviço de
     * autenticação do Firebase, permitindo criar usuários, fazer login, logout e obter o usuário atual.
     */
    private val auth: FirebaseAuth = Firebase.auth

    /**
     * @property //db
     * Instância do Cloud Firestore. É o objeto usado para acessar o banco de dados NoSQL do Firebase,
     * permitindo salvar, ler, atualizar e deletar dados, como os perfis dos usuários.
     */
    private val db: FirebaseFirestore = Firebase.firestore

    // -- FIM DAS VARIÁVEIS --

    /**
     * Retorna o usuário atualmente logado.
     * @return FirebaseUser? O usuário atual ou null se ninguém estiver logado.
     */
    fun getUsuarioAtual(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Função para criar uma nova conta de usuário com e-mail e senha.
     * Após o sucesso, salva as informações do usuário no Firestore.
     * @param email O e-mail do usuário.
     * @param senha A senha do usuário.
     * @param nomeUsuario O nome de usuário escolhido.
     * @return Result<FirebaseUser> Um objeto Result contendo o FirebaseUser em caso de sucesso ou uma exceção em caso de falha.
     */
    suspend fun cadastrarUsuario(email: String, senha: String, nomeUsuario: String): Result<FirebaseUser> {
        return try {
            // Etapa 1: Cria o usuário no Firebase Authentication
            val resultadoAuth = auth.createUserWithEmailAndPassword(email, senha).await()
            val usuarioFirebase = resultadoAuth.user!!

            // Etapa 2: Salva as informações adicionais do usuário no Firestore
            // Se esta etapa falhar, a exceção será capturada pelo bloco catch abaixo.
            salvarPerfilUsuario(usuarioFirebase.uid, email, nomeUsuario)

            Log.d(TAG, "Usuário criado e perfil salvo com sucesso: ${usuarioFirebase.uid}")
            Result.success(usuarioFirebase)
        } catch (e: Exception) {
            // Se qualquer uma das etapas falhar, o erro será capturado aqui.
            Log.e(TAG, "Falha ao criar usuário ou salvar perfil", e)
            Result.failure(e)
        }
    }

    /**
     * Função para fazer login de um usuário existente com e-mail e senha.
     * @param //email O e-mail do usuário.
     * @param //senha A senha do usuário.
     * @return Result<FirebaseUser> Um objeto Result contendo o FirebaseUser em caso de sucesso ou uma exceção em caso de falha.
     */
    suspend fun fazerLogin(email: String, senha: String): Result<FirebaseUser> {
        return try {
            // Autentica o usuário
            val resultadoAuth = auth.signInWithEmailAndPassword(email, senha).await()
            val usuarioFirebase = resultadoAuth.user!!
            Log.d(TAG, "Login bem-sucedido: ${usuarioFirebase.uid}")
            // Aqui você pode buscar os dados do usuário do Firestore se necessário
            // ou sincronizar com o armazenamento local.
            Result.success(usuarioFirebase)
        } catch (e: Exception) {
            Log.e(TAG, "Falha no login", e)
            Result.failure(e)
        }
    }

    /**
     * Salva ou atualiza o perfil de um usuário no Cloud Firestore.
     * Esta função agora lança uma exceção se falhar, em vez de capturá-la.
     * @param idUsuario O UID do usuário do Firebase Authentication.
     * @param email O e-mail do usuário.
     * @param nomeUsuario O nome de usuário.
     */
    private suspend fun salvarPerfilUsuario(idUsuario: String, email: String, nomeUsuario: String) {
        val perfilUsuario = hashMapOf(
            "uid" to idUsuario,
            "nomeUsuario" to nomeUsuario,
            "email" to email,
            "criadoEm" to System.currentTimeMillis()
            // Você pode adicionar mais campos aqui, como pontuação inicial, etc.
            // "pontuacao" to 0
        )
        // Agora, se db.collection... falhar, a exceção será lançada e capturada
        // pelo bloco catch em 'cadastrarUsuario'.
        db.collection(COLECAO_USUARIOS).document(idUsuario).set(perfilUsuario).await()
    }

    /**
     * Função para fazer logout do usuário atual.
     */
    fun fazerLogout() {
        auth.signOut()
        Log.d(TAG, "Usuário deslogado.")
        // Aqui você também limparia os dados do usuário do armazenamento local.
    }
}