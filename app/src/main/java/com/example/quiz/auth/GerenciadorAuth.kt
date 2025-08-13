package com.example.quiz.auth
import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object GerenciadorAuth {

    private const val TAG = "GerenciadoAuth"

    private const val COLECAO_USUARIOS = "Usuarios"

    private val auth: FirebaseAuth = Firebase.auth

    @SuppressLint("StaticFieldLeak")
    private val db: FirebaseFirestore = Firebase.firestore

    fun getUsuarioAtual(): FirebaseUser?{
        return auth.currentUser
    }

    /**
     * Função para criar uma nova conta de usuário com e-mail e senha.
     * Após o sucesso, salva as informações do usuário no Firestore.
     * @param "email" O e-mail do usuário.
     * @param "senha" A senha do usuário.
     * @param "nomeUsuario" O nome de usuário escolhido.
     * @return Result<FirebaseUser> Um objeto Result contendo o FirebaseUser em caso de sucesso ou uma exceção em caso de falha.
     */
    suspend fun cadastrarUsuario(email: String, senha: String, nomeUsuario: String): Result<FirebaseUser> {
        return try{
            val resultadoAuth = auth.createUserWithEmailAndPassword(email, senha).await()
            val usuarioFirebase = resultadoAuth.user!!

            //salvar informações do usuario
            salvarPerfilUsuario(usuarioFirebase.uid, email, nomeUsuario)

            Log.d(TAG,"Usuario criado com sucesso: ${usuarioFirebase.uid}")
            Result.success(usuarioFirebase)
        } catch (e: Exception){
            Log.e(TAG,"Falha ao criar o usuario" ,e)
            Result.failure(e)
        }
    }
    /**
     * Função para fazer login de um usuário existente com e-mail e senha.
     * @param "email" O e-mail do usuário.
     * @param "senha" A senha do usuário.
     * @return Result<FirebaseUser> Um objeto Result contendo o FirebaseUser em caso de sucesso ou uma exceção em caso de falha.
     */

    suspend fun fazerLogin(email: String, senha: String): Result<FirebaseUser>{
        return try{
            val resultadoAuth = auth.signInWithEmailAndPassword(email, senha).await()
            val usuarioFirebase = resultadoAuth.user!!
            Log.d(TAG, "Login bem sucedido: ${usuarioFirebase.uid}")
            Result.success(usuarioFirebase)
        }catch (e: Exception){
            Log.e(TAG,"Falha ao realizar o login", e)
            Result.failure(e)
        }
    }
    /**
     * Salva ou atualiza o perfil de um usuário no Cloud Firestore.
     * @param "idUsuario O UID do usuário do Firebase Authentication.
     * @param "email" O e-mail do usuário.
     * @param "nomeUsuario" O nome de usuário.
     */

    private suspend fun salvarPerfilUsuario(idUsuario: String, email: String, nomeUsuario: String){
        //Cria um mapa com os dados do usuário
        val perfilUsuario = hashMapOf(
            "uid" to idUsuario,
            "nomeUsuario" to nomeUsuario,
            "email" to email,
            "criadoEm" to System.currentTimeMillis(),
        )
        try {
            db.collection(COLECAO_USUARIOS).document(idUsuario).set(perfilUsuario).await()
            Log.d(TAG,"Perfil salvo no firestone com sucesso")
        } catch (e: Exception){
            Log.e(TAG,"Erro ao salvar o login do usuario no firestone", e)
        }
    }
    //Função para fazer o logout do usuario
    fun fazerLogout(){
        auth.signOut()
        Log.d(TAG,"logout feito com sucesso")
    }
}