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
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val txtLogin = findViewById<TextView>(R.id.txtLogin)

        txtLogin.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (name.isEmpty()) {
                etName.error = "Ingrese su nombre"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Correo inválido"
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Mínimo 6 caracteres"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                etConfirmPassword.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            btnRegister.isEnabled = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    btnRegister.isEnabled = true

                    if (task.isSuccessful) {

                        val uid = auth.currentUser!!.uid

                        val user = hashMapOf(
                            "uid" to uid,
                            "nombre" to name,
                            "correo" to email,
                            "presupuesto" to 0.0,
                            "fechaRegistro" to System.currentTimeMillis()
                        )

                        db.collection("usuarios")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener {

                                val mensaje = when {

                                    task.exception?.message?.contains(
                                        "already",
                                        true
                                    ) == true ->

                                        "Ese correo ya está registrado."

                                    task.exception?.message?.contains(
                                        "password",
                                        true
                                    ) == true ->

                                        "La contraseña debe tener al menos 6 caracteres."

                                    else ->

                                        "No fue posible crear la cuenta."
                                }

                                Toast.makeText(
                                    this,
                                    mensaje,
                                    Toast.LENGTH_LONG
                                ).show()
                                val profileUpdates =
                                    com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()

                                auth.currentUser?.updateProfile(profileUpdates)
                                auth.signOut()

                                startActivity(
                                    Intent(
                                        this,
                                        LoginActivity::class.java
                                    )
                                )

                                finish()

                            }.addOnFailureListener {

                                Toast.makeText(
                                    this,
                                    "Error al guardar datos",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                    } else {

                        Toast.makeText(
                            this,
                            task.exception?.message,
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

        }

    }

}