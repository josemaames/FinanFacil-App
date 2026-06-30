package upch.jamesss.finanfacil

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
import upch.jamesss.finanfacil.data.local.database.AppDatabase
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.widget.ProgressBar
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.XAxis
import upch.jamesss.finanfacil.utils.PdfExporter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

import upch.jamesss.finanfacil.utils.StatisticsUtils

class StatisticsActivity : AppCompatActivity() {

    private lateinit var pieChart: PieChart

    private lateinit var txtTotalGeneral: TextView
    private lateinit var txtCantidad: TextView
    private lateinit var txtPromedio: TextView
    private lateinit var txtMes: TextView
    private lateinit var txtTopCategoria: TextView
    private lateinit var txtTopCategory: TextView
    private lateinit var txtRanking: TextView
    private lateinit var txtEmptyState: TextView
    private lateinit var barChart: BarChart
    private lateinit var txtInsights: TextView
    private lateinit var txtMonthlyComparison: TextView
    private lateinit var btnExportPdf: Button
    private lateinit var txtDeductible: TextView

    private lateinit var txtNoDeductible: TextView

    private lateinit var layoutRanking: LinearLayout

    private lateinit var btnBack: Button


    private lateinit var db: AppDatabase
    private var totalGeneral = 0.0

    private var presupuesto = 0.0

    private var disponible = 0.0

    private var promedio = 0.0

    private var porcentaje = 0

    private var topCategory = ""

    private var nombreUsuario = "Usuario"

    private var ranking =
        mutableListOf<Pair<String, Double>>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_statistics)

        initViews()


        lifecycleScope.launch {

            val expenses =
                db.transactionDao()
                    .getAllTransactions()

            if (expenses.isEmpty()) {

                pieChart.visibility =
                    View.GONE

                txtTopCategory.visibility =
                    View.GONE

                txtRanking.visibility =
                    View.GONE

                txtEmptyState.visibility =
                    View.VISIBLE

                txtTotalGeneral.text =
                    "Total gastado: S/ 0.00"

                return@launch
            }

            totalGeneral =
                expenses.sumOf { it.amount }

            txtTotalGeneral.text =
                "S/ %.2f".format(totalGeneral)

            txtCantidad.text =
                expenses.size.toString()

            promedio =
                if (expenses.isNotEmpty()) {
                    totalGeneral / expenses.size
                } else {
                    0.0
                }

            txtPromedio.text =
                "S/ %.2f".format(promedio)

            val inicioMes =
                java.util.Calendar.getInstance().apply {

                    set(java.util.Calendar.DAY_OF_MONTH, 1)
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)

                }.timeInMillis

            val totalMes =
                expenses
                    .filter {
                        it.timestamp >= inicioMes
                    }
                    .sumOf {
                        it.amount
                    }

            txtMes.text =
                "S/ %.2f".format(totalMes)

            val groupedExpenses =
                expenses.groupBy { it.category }

            val entries =
                mutableListOf<PieEntry>()

            groupedExpenses.forEach { (category, list) ->

                val total =
                    list.sumOf { it.amount }

                entries.add(
                    PieEntry(
                        total.toFloat(),
                        category
                    )
                )
            }

            val pieDataSet =
                PieDataSet(
                    entries,
                    "Gastos por categoria"
                )
            val deductibleCategories =
                setOf(

                    "Salud",

                    "Educación"

                )

            pieDataSet.valueTextSize = 14f
            pieDataSet.sliceSpace = 3f

            pieDataSet.colors = listOf(
                android.graphics.Color.parseColor("#7E57C2"),
                android.graphics.Color.parseColor("#5E35B1"),
                android.graphics.Color.parseColor("#9575CD"),
                android.graphics.Color.parseColor("#26A69A"),
                android.graphics.Color.parseColor("#42A5F5"),
                android.graphics.Color.parseColor("#FFA726"),
                android.graphics.Color.parseColor("#EF5350")
            )

            val totalDeductible =
                expenses
                    .filter {

                        it.category in deductibleCategories

                    }
                    .sumOf {

                        it.amount

                    }

            val totalNoDeductible =
                expenses
                    .filter {

                        it.category !in deductibleCategories

                    }
                    .sumOf {

                        it.amount

                    }

            val pieData =
                PieData(pieDataSet)

            pieChart.data =
                pieData

            pieChart.description.isEnabled = false

            pieChart.centerText =
                "FinanFacil"

            pieChart.setCenterTextSize(18f)

            pieChart.setEntryLabelTextSize(12f)

            pieChart.isDrawHoleEnabled = true

            pieChart.holeRadius = 45f

            pieChart.transparentCircleRadius = 50f

            pieChart.animateY(1500)

            pieChart.invalidate()

            val monthlyData =
                StatisticsUtils.getExpensesByMonth(expenses)

            val barEntries =
                ArrayList<BarEntry>()

            val labels =
                ArrayList<String>()

            monthlyData.entries.forEachIndexed { index, item ->

                labels.add(item.key)

                barEntries.add(
                    BarEntry(
                        index.toFloat(),
                        item.value.toFloat()
                    )
                )

            }

            val dataSet =
                BarDataSet(
                    barEntries,
                    "Gastos por mes"
                )

            dataSet.valueTextSize = 12f

            val barData =
                BarData(dataSet)

            barChart.data =
                barData

            barChart.xAxis.valueFormatter =
                IndexAxisValueFormatter(labels)

            barChart.xAxis.granularity = 1f

            barChart.xAxis.position =
                XAxis.XAxisPosition.BOTTOM

            barChart.description.isEnabled = false

            barChart.animateY(1500)

            barChart.invalidate()

            if (groupedExpenses.isNotEmpty()) {

                val topCategoryData =
                    groupedExpenses.maxBy {
                        it.value.sumOf { expense ->
                            expense.amount
                        }
                    }

                topCategory = topCategoryData.key

                val totalTop =
                    topCategoryData.value.sumOf {
                        it.amount
                    }

                porcentaje =
                    ((totalTop / totalGeneral) * 100).toInt()

                txtTopCategoria.text =
                    topCategory

                txtTopCategory.text =
                    "🏆 Categoría principal\n$topCategory\nS/ %.2f"
                        .format(totalTop)

                ranking =
                    groupedExpenses
                        .map {
                            Pair(
                                it.key,
                                it.value.sumOf { expense ->
                                    expense.amount
                                }
                            )
                        }
                        .sortedByDescending {
                            it.second
                        }
                        .toMutableList()

                layoutRanking.removeAllViews()

                ranking.forEachIndexed { index, item ->

                    val view =
                        layoutInflater.inflate(
                            R.layout.item_category_progress,
                            layoutRanking,
                            false
                        )

                    val txtName =
                        view.findViewById<TextView>(R.id.txtCategoryName)

                    val txtAmount =
                        view.findViewById<TextView>(R.id.txtCategoryAmount)

                    val txtPercent =
                        view.findViewById<TextView>(R.id.txtPercent)

                    val progress =
                        view.findViewById<ProgressBar>(R.id.progressCategory)

                    val percent =
                        ((item.second / totalGeneral) * 100).toInt()

                    val medal =
                        when (index) {
                            0 -> "🥇"
                            1 -> "🥈"
                            2 -> "🥉"
                            else -> "📌"
                        }

                    txtName.text =
                        "$medal ${item.first}"

                    txtAmount.text =
                        "S/ %.2f".format(item.second)

                    txtPercent.text =
                        "$percent% del total"

                    progress.progress =
                        percent

                    layoutRanking.addView(view)
                }

                txtDeductible.text =
                    "🟢 Deducibles: S/ %.2f"
                        .format(totalDeductible)

                txtNoDeductible.text =
                    "🔴 No deducibles: S/ %.2f"
                        .format(totalNoDeductible)

                val recomendacion =
                    if (porcentaje >= 50)
                        "💡 Considera reducir gastos en $topCategory para equilibrar tu presupuesto."
                    else
                        "✅ Tus gastos están bien distribuidos entre las categorías."

                txtInsights.text =
                    """
📌 Gastos registrados: ${expenses.size}

💰 Promedio por gasto:
S/ %.2f

🏆 Categoría principal:
$topCategory

📈 Representa el $porcentaje%% del total.

💵 Total invertido:
S/ %.2f

$recomendacion
""".trimIndent()
                        .format(
                            promedio,
                            totalGeneral
                        )
            }

            val currentMonth =
                StatisticsUtils.getCurrentMonthExpenses(expenses)

            val previousMonth =
                StatisticsUtils.getPreviousMonthExpenses(expenses)

            val difference =
                currentMonth - previousMonth

            val icon =
                when {
                    difference > 0 -> "📈"
                    difference < 0 -> "📉"
                    else -> "➡"
                }

            txtMonthlyComparison.text =
                """
Mes actual:
S/ %.2f

Mes anterior:
S/ %.2f

$icon Diferencia:
S/ %.2f
""".trimIndent()
                    .format(
                        currentMonth,
                        previousMonth,
                        kotlin.math.abs(difference)
                    )

        }


        btnBack.setOnClickListener {

            finish()
        }

    }
    private fun initViews() {

        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)

        txtTotalGeneral = findViewById(R.id.txtTotalGeneral)
        txtCantidad = findViewById(R.id.txtCantidad)
        txtPromedio = findViewById(R.id.txtPromedio)
        txtMes = findViewById(R.id.txtMes)

        txtTopCategoria = findViewById(R.id.txtTopCategoria)
        txtTopCategory = findViewById(R.id.txtTopCategory)
        txtRanking = findViewById(R.id.txtRanking)

        txtInsights = findViewById(R.id.txtInsights)
        txtMonthlyComparison = findViewById(R.id.txtMonthlyComparison)

        txtEmptyState = findViewById(R.id.txtEmptyStatistics)

        layoutRanking = findViewById(R.id.layoutRanking)

        btnBack = findViewById(R.id.btnBack)
        txtDeductible =
            findViewById(R.id.txtDeductible)

        txtNoDeductible =
            findViewById(R.id.txtNoDeductible)

        // ← ESTA LÍNEA FALTABA
        btnExportPdf = findViewById(R.id.btnExportPdf)

        btnExportPdf.setOnClickListener {

            val pdf =
                PdfExporter.exportReport(
                    context = this,
                    usuario = nombreUsuario,
                    total = totalGeneral,
                    presupuesto = presupuesto,
                    disponible = disponible,
                    promedio = promedio,
                    categoria = topCategory,
                    porcentaje = porcentaje,
                    ranking = ranking,
                    comparacion = txtMonthlyComparison.text.toString()
                )

            Toast.makeText(
                this,
                "PDF generado:\n${pdf.absolutePath}",
                Toast.LENGTH_LONG
            ).show()
        }

        db = AppDatabase.getDatabase(applicationContext)

// Obtener el nombre del usuario desde Firebase
        val auth = FirebaseAuth.getInstance()

        val firestore = FirebaseFirestore.getInstance()

        val uid = auth.currentUser?.uid

        if (uid != null) {

            firestore.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener { document: com.google.firebase.firestore.DocumentSnapshot ->

                    nombreUsuario =
                        document.getString("nombre") ?: "Usuario"

                }

        }
    }

}
