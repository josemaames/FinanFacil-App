package upch.jamesss.finanfacil

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import upch.jamesss.finanfacil.data.local.database.AppDatabase
import upch.jamesss.finanfacil.databinding.ActivityMainBinding
import java.util.Calendar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import upch.jamesss.finanfacil.data.local.entity.TransactionEntity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private val db by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    private val preferences by lazy {
        getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
    }

    private lateinit var txtTotalSpent: TextView
    private lateinit var txtExpensesCount: TextView
    private lateinit var txtLastCategory: TextView
    private lateinit var etMonthlyBudget: EditText
    private lateinit var btnSaveBudget: Button
    private lateinit var txtBudgetSummary: TextView
    private lateinit var txtBudgetAlert: TextView
    private lateinit var progressBudget: ProgressBar
    private lateinit var txtWelcome: TextView

    private lateinit var auth: FirebaseAuth

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        firestore = FirebaseFirestore.getInstance()

        txtWelcome = findViewById(R.id.txtWelcome)

        txtTotalSpent =
            findViewById(R.id.txtTotalSpent)

        txtExpensesCount =
            findViewById(R.id.txtExpensesCount)

        txtLastCategory =
            findViewById(R.id.txtLastCategory)

        etMonthlyBudget =
            findViewById(R.id.etMonthlyBudget)

        btnSaveBudget =
            findViewById(R.id.btnSaveBudget)

        txtBudgetSummary =
            findViewById(R.id.txtBudgetSummary)

        txtBudgetAlert =
            findViewById(R.id.txtBudgetAlert)

        progressBudget =
            findViewById(R.id.progressBudget)
        val txtWelcome =
            findViewById<TextView>(R.id.txtWelcome)

        val txtUserEmail =
            findViewById<TextView>(R.id.txtUserEmail)
        val uid = auth.currentUser?.uid

        if (uid != null) {

            firestore.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->

                    txtWelcome.text =
                        "👋 Hola, ${document.getString("nombre")}"

                    txtUserEmail.text =
                        document.getString("correo")

                }

        }

        btnSaveBudget.setOnClickListener {

            saveMonthlyBudget()
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

            // Cerrar sesión de Firebase
            FirebaseAuth.getInstance().signOut()

            // Volver al Login
            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }
    }

    override fun onResume() {

        super.onResume()

        syncExpensesFromFirebase()

    }

    private fun loadDashboard() {

        val monthlyBudget =
            preferences.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()

        if (monthlyBudget > 0.0) {

            etMonthlyBudget.setText(
                "%.2f".format(monthlyBudget)
            )
        }

        lifecycleScope.launch {

            val expenses =
                db.transactionDao()
                    .getAllTransactions()

            val total =
                expenses.sumOf { it.amount }

            val monthlySpent =
                expenses
                    .filter { it.timestamp >= getStartOfCurrentMonth() }
                    .sumOf { it.amount }

            txtTotalSpent.text =
                "Total gastado: S/ %.2f"
                    .format(total)

            txtExpensesCount.text =
                "Gastos registrados: ${expenses.size}"

            txtLastCategory.text =
                if (expenses.isNotEmpty()) {
                    "Última categoría: ${expenses.first().category}"
                } else {
                    "Última categoría: -"
                }

            updateBudgetStatus(
                monthlyBudget,
                monthlySpent
            )
        }
    }

    private fun syncExpensesFromFirebase() {

        val uid = auth.currentUser?.uid ?: return

        firestore.collection("gastos")
            .whereEqualTo("uidUsuario", uid)
            .get()
            .addOnSuccessListener { result ->

                lifecycleScope.launch {

                    db.transactionDao().deleteAllTransactions()

                    for (document in result.documents) {

                        val expense = TransactionEntity(

                            amount = document.getDouble("monto") ?: 0.0,

                            category = document.getString("categoria") ?: "",

                            description = document.getString("descripcion") ?: "",

                            date = document.getString("fecha") ?: "",

                            timestamp = document.getLong("timestamp")
                                ?: System.currentTimeMillis()

                        )

                        db.transactionDao()
                            .insertTransaction(expense)

                    }

                    loadDashboard()

                }

            }

    }

    private fun saveMonthlyBudget() {

        val budget =
            etMonthlyBudget.text.toString()
                .trim()
                .toDoubleOrNull()

        if (budget == null || budget <= 0.0) {

            etMonthlyBudget.error =
                "Ingresa un presupuesto valido"

            return
        }

        preferences.edit()
            .putFloat(KEY_MONTHLY_BUDGET, budget.toFloat())
            .apply()

        Toast.makeText(
            this,
            "Presupuesto guardado",
            Toast.LENGTH_SHORT
        ).show()

        loadDashboard()
    }

    private fun updateBudgetStatus(
        monthlyBudget: Double,
        monthlySpent: Double
    ) {

        if (monthlyBudget <= 0.0) {

            progressBudget.progress =
                0

            txtBudgetSummary.text =
                "Presupuesto mensual: sin configurar"

            txtBudgetAlert.text =
                "Define un presupuesto para activar las alertas"

            txtBudgetAlert.setTextColor(
                Color.parseColor("#64748B")
            )

            return
        }

        val remaining =
            monthlyBudget - monthlySpent

        val percentage =
            ((monthlySpent / monthlyBudget) * 100)
                .toInt()
                .coerceAtMost(100)

        progressBudget.progress =
            percentage

        txtBudgetSummary.text =
            "Gastado este mes: S/ %.2f | Restante: S/ %.2f"
                .format(monthlySpent, remaining)

        val alertColor: Int
        val alertText: String

        when {
            monthlySpent > monthlyBudget -> {
                alertColor = Color.parseColor("#DC2626")
                alertText = "Presupuesto superado"
            }

            percentage >= 80 -> {
                alertColor = Color.parseColor("#EA580C")
                alertText = "Cuidado: ya usaste el $percentage% del presupuesto"
            }

            percentage >= 50 -> {
                alertColor = Color.parseColor("#CA8A04")
                alertText = "Vas por el $percentage% del presupuesto mensual"
            }

            else -> {
                alertColor = Color.parseColor("#0F766E")
                alertText = "Presupuesto bajo control"
            }
        }

        txtBudgetAlert.text =
            alertText

        txtBudgetAlert.setTextColor(
            alertColor
        )
    }

    private fun getStartOfCurrentMonth(): Long {

        val calendar =
            Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

    companion object {

        private const val PREFERENCES_NAME =
            "finanfacil_preferences"

        private const val KEY_MONTHLY_BUDGET =
            "monthly_budget"
    }
    private fun loadUserName() {

        val uid = auth.currentUser?.uid ?: return

        firestore.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val nombre =
                        document.getString("nombre") ?: "Usuario"

                    val primerNombre =
                        nombre.split(" ").first()

                    txtWelcome.text =
                        "¡Hola, $primerNombre! 👋"

                }

            }

    }

}

