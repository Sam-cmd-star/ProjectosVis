package com.example.ecocleanmanager.data

data class Usuario(
    val nombre: String,
    val contrasena: String,
    val isAdmin: Boolean = false
)

object RepositorioUsuarios {
    val listaUsuarios = listOf(
        Usuario("usuario1", "123456789", isAdmin = true),
        Usuario("usuario2", "123456789", isAdmin = true),
        Usuario("usuario3", "123456789"),
        Usuario("usuario4", "123456789"),
        Usuario("usuario5", "123456789")
    )
}