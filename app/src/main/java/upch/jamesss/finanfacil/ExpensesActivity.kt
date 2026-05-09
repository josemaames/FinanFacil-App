package upch.jamesss.finanfacil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import upch.jamesss.finanfacil.data.local.database.AppDatabase
import upch.jamesss.finanfacil.data.local.entity.TransactionEntity
import java.util.Locale

class ExpensesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_expensess)

        val recyclerView =
            findViewById<RecyclerView>(R.id.rvExpenses)

        val btnBackHome =
            findViewById<Button>(R.id.btnBackHome)

        val txtTotal =
            findViewById<TextView>(R.id.txtTotal)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        val db =
            AppDatabase.getDatabase(applicationContext)

        fun updateTotal(expenses: List<TransactionEntity>) {

            val total =
                expenses.sumOf { it.amount }

            txtTotal.text =
                String.format(
                    Locale.getDefault(),
                    "Total gastado: S/ %.2f",
                    total
                )
        }

        lifecycleScope.launch {

            val expenses =
                db.transactionDao()
                    .getAllTransactions()
                    .toMutableList()

            updateTotal(expenses)

            lateinit var adapter: ExpenseAdapter

            adapter = ExpenseAdapter(
                expenses
            ) { expense ->

                lifecycleScope.launch {

                    db.transactionDao()
                        .deleteTransaction(expense)

                    adapter.removeExpense(expense)

                    updateTotal(expenses)
                }
            }

            recyclerView.adapter =
                adapter
        }

        btnBackHome.setOnClickListener {

            val intent = Intent(
                this,
                MainActivity::class.java
            )

            startActivity(intent)
        }
    }
}
