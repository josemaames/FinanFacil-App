package upch.jamesss.finanfacil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)

        val etPassword = findViewById<EditText>(R.id.etPassword)

        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString()

            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                val intent = Intent(
                    this,
                    MainActivity::class.java
                )

                startActivity(intent)
            }
        }
    }
}