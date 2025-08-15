package com.example.quiz.ui.login

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
import kotlinx.coroutines.launch

@Composable
fun TelaLogin(
    onLoginSuccess: () -> Unit, // Ação para quando o login for bem-sucedido
    onNavigateToCadastro: () -> Unit // Ação para navegar para a tela de cadastro
) {
    // Escopo da coroutine para chamar as funções suspend do GerenciadorAuth
    val coroutineScope = rememberCoroutineScope()

    // Estados para armazenar os valores dos campos de texto
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erroMensagem by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Estrutura principal da tela
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa a tela inteira
            .padding(16.dp), // Adiciona um espaçamento nas bordas
        verticalArrangement = Arrangement.Center, // Centraliza os itens verticalmente
        horizontalAlignment = Alignment.CenterHorizontally // Centraliza os itens horizontalmente
    ) {
        Text(
            text = "Bem-vindo ao Quiz App",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo de texto para o Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp)) // Espaço entre os campos

        // Campo de texto para a Senha
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(), // Esconde os caracteres da senha
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        // Exibe a mensagem de erro, se houver
        erroMensagem?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de Login
        Button(
            onClick = {
                // Inicia o processo de login
                isLoading = true
                erroMensagem = null
                coroutineScope.launch {
                    val resultado = GerenciadorAuth.fazerLogin(email, senha)
                    resultado.onSuccess {
                        // Se o login for bem-sucedido, executa a ação
                        isLoading = false
                        onLoginSuccess()
                    }
                    resultado.onFailure { exception ->
                        // Se falhar, mostra uma mensagem de erro
                        isLoading = false
                        erroMensagem = "Email ou senha inválidos. Tente novamente."
                        // Log.e("TelaLogin", "Erro no login", exception) // Para depuração
                    }
                }
            },
            enabled = !isLoading, // Desabilita o botão enquanto carrega
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Entrar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botão para navegar para a tela de cadastro
        TextButton(onClick = onNavigateToCadastro) {
            Text("Não tem uma conta? Cadastre-se")
        }
    }
}

// Preview para visualização no Android Studio
@Preview(showBackground = true)
@Composable
fun TelaLoginPreview() {
    // Para o preview funcionar, você pode passar funções vazias
    TelaLogin(onLoginSuccess = {}, onNavigateToCadastro = {})
}