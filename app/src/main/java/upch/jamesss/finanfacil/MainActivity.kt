package upch.jamesss.finanfacil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import upch.jamesss.finanfacil.databinding.ActivityMainBinding
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch
import upch.jamesss.finanfacil.data.local.database.AppDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val txtTotalSpent =
            findViewById<TextView>(R.id.txtTotalSpent)

        val txtExpensesCount =
            findViewById<TextView>(R.id.txtExpensesCount)

        val txtLastCategory =
            findViewById<TextView>(R.id.txtLastCategory)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "finanfacil_db"
        ).build()

        lifecycleScope.launch {

            val expenses =
                db.transactionDao()
                    .getAllTransactions()

            runOnUiThread {

                val total =
                    expenses.sumOf { it.amount }

                txtTotalSpent.text =
                    "💰 Total gastado: S/ $total"

                txtExpensesCount.text =
                    "📝 Gastos registrados: ${expenses.size}"

                if (expenses.isNotEmpty()) {

                    txtLastCategory.text =
                        "🏷 Última categoría: ${expenses.last().category}"
                }
            }
        }

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

        binding.btnStatistics.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    StatisticsActivity::class.java
                )
            )
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
