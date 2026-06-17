package upch.jamesss.finanfacil

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import upch.jamesss.finanfacil.data.local.database.AppDatabase
import upch.jamesss.finanfacil.data.local.entity.TransactionEntity
import java.util.Calendar
import java.util.Locale

class ExpensesActivity : AppCompatActivity() {

    private val db by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    private val allExpenses =
        mutableListOf<TransactionEntity>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private lateinit var txtTotal: TextView
    private lateinit var etSearch: EditText
    private lateinit var spDateFilter: Spinner
    private lateinit var txtEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_expenses)

        recyclerView =
            findViewById(R.id.rvExpenses)

        val btnBackHome =
            findViewById<Button>(R.id.btnBackHome)

        txtTotal =
            findViewById(R.id.txtTotal)

        etSearch =
            findViewById(R.id.etSearch)

        spDateFilter =
            findViewById(R.id.spDateFilter)

        txtEmptyState =
            findViewById(R.id.txtEmptyState)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        adapter =
            ExpenseAdapter(
                onItemClick = { expense ->

                    val intent =
                        Intent(
                            this,
                            ExpenseDetailActivity::class.java
                        )

                    intent.putExtra(
                        ExpenseDetailActivity.EXTRA_EXPENSE_ID,
                        expense.id
                    )

                    startActivity(intent)
                },
                onDeleteClick = { expense ->

                    lifecycleScope.launch {

                        db.transactionDao()
                            .deleteTransaction(expense)

                        allExpenses.remove(expense)

                        applyFilters()
                    }
                }
            )

        recyclerView.adapter =
            adapter

        spDateFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    applyFilters()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {}
            }

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

                    applyFilters()
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )

        btnBackHome.setOnClickListener {

            val intent = Intent(
                this,
                MainActivity::class.java
            )

            startActivity(intent)
        }
    }

    override fun onResume() {

        super.onResume()

        loadExpenses()
    }

    private fun loadExpenses() {

        lifecycleScope.launch {

            val expenses =
                db.transactionDao()
                    .getAllTransactions()

            allExpenses.clear()
            allExpenses.addAll(expenses)

            applyFilters()
        }
    }

    private fun applyFilters() {

        if (!::adapter.isInitialized) {

            return
        }

        val query =
            etSearch.text.toString().lowercase()

        val dateFiltered =
            filterByDate(
                allExpenses,
                spDateFilter.selectedItemPosition
            )

        val filteredList =
            dateFiltered.filter {

                it.category.lowercase()
                    .contains(query)

                        ||

                        it.description.lowercase()
                            .contains(query)
            }

        adapter.updateData(filteredList)
        updateTotal(filteredList)
        updateEmptyState(filteredList)
    }

    private fun filterByDate(
        expenses: List<TransactionEntity>,
        filterPosition: Int
    ): List<TransactionEntity> {

        if (filterPosition == 0) {

            return expenses
        }

        val calendar =
            Calendar.getInstance()

        when (filterPosition) {
            1 -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            2 -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            3 -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        }

        val startTime =
            calendar.timeInMillis

        return expenses.filter {
            it.timestamp >= startTime
        }
    }

    private fun updateTotal(expenses: List<TransactionEntity>) {

        val total =
            expenses.sumOf { it.amount }

        txtTotal.text =
            String.format(
                Locale.getDefault(),
                "Total gastado: S/ %.2f",
                total
            )
    }

    private fun updateEmptyState(
        visibleExpenses: List<TransactionEntity>
    ) {

        val hasVisibleExpenses =
            visibleExpenses.isNotEmpty()

        recyclerView.visibility =
            if (hasVisibleExpenses) View.VISIBLE else View.GONE

        txtEmptyState.visibility =
            if (hasVisibleExpenses) View.GONE else View.VISIBLE

        txtEmptyState.text =
            if (allExpenses.isEmpty()) {
                "Aún no tienes gastos registrados"
            } else {
                "No se encontraron gastos"
            }
    }
}
