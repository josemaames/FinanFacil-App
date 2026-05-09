package upch.jamesss.finanfacil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import upch.jamesss.finanfacil.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnRegisterExpense.setOnClickListener {

            val intent = Intent(
                this,
                RegisterExpenseActivity::class.java
            )

            startActivity(intent)
        }

        binding.btnViewExpenses.setOnClickListener {

            val intent = Intent(
                this,
                ExpensesActivity::class.java
            )

            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)

            finish()
        }
    }
}
