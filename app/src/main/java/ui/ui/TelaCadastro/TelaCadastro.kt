package com.example.quiz.ui.cadastro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quiz.auth.GerenciadorAuth
import com.example.quiz.ui.theme.QuizTheme
import kotlinx.coroutines.launch

@Composable
fun TelaCadastro(
    onCadastroSuccess: () -> Unit, // Ação para quando o cadastro for bem-sucedido
    onNavigateToLogin: () -> Unit // Ação para navegar de volta para a tela de login
) {
    val coroutineScope = rememberCoroutineScope()
    var nomeUsuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erroMensagem by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crie sua Conta",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = nomeUsuario,
            onValueChange = { nomeUsuario = it },
            label = { Text("Nome de usuário") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        erroMensagem?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nomeUsuario.isBlank() || email.isBlank() || senha.isBlank()) {
                    erroMensagem = "Todos os campos são obrigatórios."
                    return@Button
                }
                isLoading = true
                erroMensagem = null
                coroutineScope.launch {
                    try { //ADICIONADO POR LLM
                        val resultado = GerenciadorAuth.cadastrarUsuario(email, senha, nomeUsuario)
                        if (resultado.isSuccess) {
                            onCadastroSuccess()
                        } else {
                            erroMensagem = "Erro ao cadastrar: ${resultado.exceptionOrNull()?.localizedMessage}"
                        }
                    } catch (e: Exception) {
                        erroMensagem = "Erro inesperado: ${e.localizedMessage}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Cadastrar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Já tem uma conta? Faça login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaCadastroPreview() {
    QuizTheme {
        TelaCadastro(onCadastroSuccess = {}, onNavigateToLogin = {})
    }
}