package com.example.ecocleanmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecocleanmanager.data.RepositorioUsuarios

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val user = etUsername.text.toString()
            val pass = etPassword.text.toString()

            val usuarioEncontrado = RepositorioUsuarios.listaUsuarios.find {
                it.nombre == user && it.contrasena == pass
            }

            if (usuarioEncontrado != null) {
                // Login successful!
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("IS_ADMIN", usuarioEncontrado.isAdmin)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}