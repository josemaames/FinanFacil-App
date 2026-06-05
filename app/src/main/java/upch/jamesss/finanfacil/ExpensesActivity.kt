package upch.jamesss.finanfacil

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
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

        val etSearch =
            findViewById<EditText>(R.id.etSearch)

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

            val allExpenses =
                db.transactionDao()
                    .getAllTransactions()
                    .toMutableList()

            updateTotal(allExpenses)

            lateinit var adapter: ExpenseAdapter

            adapter = ExpenseAdapter { expense ->

                    lifecycleScope.launch {

                        db.transactionDao()
                            .deleteTransaction(expense)

                        allExpenses.remove(expense)

                        adapter.updateData(allExpenses)

                        updateTotal(allExpenses)
                    }
                }

            adapter.updateData(allExpenses)

            recyclerView.adapter =
                adapter

            etSearch.addTextChangedListener(
                object : TextWatcher {

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                        val query =
                            s.toString().lowercase()

                        val filteredList =
                            allExpenses.filter {

                                it.category.lowercase()
                                    .contains(query)

                                        ||

                                        it.description.lowercase()
                                            .contains(query)
                            }

                        adapter.updateData(
                            filteredList
                        )
                    }

                    override fun afterTextChanged(
                        s: Editable?
                    ) {}
                }
            )
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
