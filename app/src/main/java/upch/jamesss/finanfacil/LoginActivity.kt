package upch.jamesss.finanfacil

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()

        // Si ya existe una sesión iniciada
        if (auth.currentUser != null) {

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )

            finish()
            return
        }

        val etEmail =
            findViewById<EditText>(R.id.etEmail)

        val etPassword =
            findViewById<EditText>(R.id.etPassword)

        val btnLogin =
            findViewById<Button>(R.id.btnLogin)

        val txtRegister =
            findViewById<TextView>(R.id.txtRegister)

        val txtForgotPassword =
            findViewById<TextView>(R.id.txtForgotPassword)

        txtRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )

        }

        txtForgotPassword.setOnClickListener {

            val email =
                etEmail.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                etEmail.error = "Ingrese un correo válido"

                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Se envió un enlace para restablecer tu contraseña.",
                        Toast.LENGTH_LONG
                    ).show()

                }
                .addOnFailureListener { e ->

                    Toast.makeText(
                        this,
                        e.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()

                }

        }

        btnLogin.setOnClickListener {

            val email =
                etEmail.text.toString().trim()

            val password =
                etPassword.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                etEmail.error = "Correo inválido"
                return@setOnClickListener
            }

            if (password.isBlank()) {

                etPassword.error = "Ingrese su contraseña"
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    btnLogin.isEnabled = true

                    if (task.isSuccessful) {

                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            )
                        )

                        finish()

                    } else {

                        Toast.makeText(
                            this,
                            "Correo o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }

        }

    }

}
