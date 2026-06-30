package upch.jamesss.finanfacil

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import upch.jamesss.finanfacil.data.local.database.AppDatabase

class DeductibleExpensesActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var btnBack: Button
    private lateinit var db: AppDatabase
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_deductible_expenses)

        recycler = findViewById(R.id.recyclerDeductible)
        btnBack = findViewById(R.id.btnBack)

        recycler.layoutManager = LinearLayoutManager(this)

        adapter = ExpenseAdapter(

            onItemClick = {
                // No hacemos nada por ahora
            },

            onDeleteClick = {
                // No permitimos eliminar desde esta pantalla
            }

        )

        recycler.adapter = adapter

        db = AppDatabase.getDatabase(applicationContext)

        lifecycleScope.launch {

            val expenses =
                db.transactionDao()
                    .getDeductibleExpenses()

            adapter.updateData(expenses)

        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}