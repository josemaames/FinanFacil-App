package upch.jamesss.finanfacil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import upch.jamesss.finanfacil.data.local.database.AppDatabase
import upch.jamesss.finanfacil.data.local.entity.TransactionEntity

class ExpenseDetailActivity : AppCompatActivity() {

    private val db by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    private var currentExpense: TransactionEntity? = null
    private var expenseId: Int = INVALID_EXPENSE_ID

    private lateinit var txtAmount: TextView
    private lateinit var txtCategory: TextView
    private lateinit var txtDescription: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtEmptyState: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_expense_detail)

        txtAmount =
            findViewById(R.id.txtDetailAmount)

        txtCategory =
            findViewById(R.id.txtDetailCategory)

        txtDescription =
            findViewById(R.id.txtDetailDescription)

        txtDate =
            findViewById(R.id.txtDetailDate)

        txtEmptyState =
            findViewById(R.id.txtDetailEmptyState)

        btnEdit =
            findViewById(R.id.btnEditExpense)

        btnDelete =
            findViewById(R.id.btnDeleteExpense)

        val btnBack =
            findViewById<Button>(R.id.btnBackExpenses)

        expenseId =
            intent.getIntExtra(EXTRA_EXPENSE_ID, INVALID_EXPENSE_ID)

        btnEdit.setOnClickListener {

            val expense =
                currentExpense ?: return@setOnClickListener

            val intent =
                Intent(
                    this,
                    RegisterExpenseActivity::class.java
                )

            intent.putExtra(
                RegisterExpenseActivity.EXTRA_EXPENSE_ID,
                expense.id
            )

            startActivity(intent)
        }

        btnDelete.setOnClickListener {

            val expense =
                currentExpense ?: return@setOnClickListener

            lifecycleScope.launch {

                db.transactionDao()
                    .deleteTransaction(expense)

                Toast.makeText(
                    this@ExpenseDetailActivity,
                    "Gasto eliminado",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }

        btnBack.setOnClickListener {

            finish()
        }
    }

    override fun onResume() {

        super.onResume()

        loadExpense()
    }

    private fun loadExpense() {

        if (expenseId == INVALID_EXPENSE_ID) {

            showMissingExpense()
            return
        }

        lifecycleScope.launch {

            val expense =
                db.transactionDao()
                    .getTransactionById(expenseId)

            if (expense == null) {

                showMissingExpense()
                return@launch
            }

            currentExpense =
                expense

            txtEmptyState.visibility =
                View.GONE

            btnEdit.visibility =
                View.VISIBLE

            btnDelete.visibility =
                View.VISIBLE

            txtAmount.text =
                "S/ %.2f".format(expense.amount)

            txtCategory.text =
                expense.category

            txtDescription.text =
                expense.description

            txtDate.text =
                expense.date
        }
    }

    private fun showMissingExpense() {

        currentExpense =
            null

        txtEmptyState.visibility =
            View.VISIBLE

        btnEdit.visibility =
            View.GONE

        btnDelete.visibility =
            View.GONE
    }

    companion object {

        const val EXTRA_EXPENSE_ID =
            "extra_expense_id"

        private const val INVALID_EXPENSE_ID =
            -1
    }
}
