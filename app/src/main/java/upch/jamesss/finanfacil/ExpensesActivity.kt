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
import androidx.room.Room
import kotlinx.coroutines.launch
import upch.jamesss.finanfacil.data.local.database.AppDatabase

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

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "finanfacil_db"
        ).build()

        lifecycleScope.launch {

            val expenses =
                db.transactionDao()
                    .getAllTransactions()
                    .toMutableList()

            runOnUiThread {

                fun updateTotal() {

                    val total =
                        expenses.sumOf { it.amount }

                    txtTotal.text =
                        "Total gastado: S/ $total"
                }

                updateTotal()

                recyclerView.adapter =
                    ExpenseAdapter(
                        expenses
                    ) { expense ->

                        lifecycleScope.launch {

                            db.transactionDao()
                                .deleteTransaction(expense)
                        }

                        expenses.remove(expense)

                        updateTotal()
                    }
            }
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