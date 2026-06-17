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

class StatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_statistics)

        val pieChart =
            findViewById<PieChart>(R.id.pieChart)

        val txtTotalGeneral =
            findViewById<TextView>(R.id.txtTotalGeneral)

        val txtTopCategory =
            findViewById<TextView>(R.id.txtTopCategory)

        val txtRanking =
            findViewById<TextView>(R.id.txtRanking)

        val txtEmptyState =
            findViewById<TextView>(R.id.txtEmptyStatistics)

        val btnBack =
            findViewById<Button>(R.id.btnBack)

        val db =
            AppDatabase.getDatabase(applicationContext)

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

            val totalGeneral =
                expenses.sumOf { it.amount }

            txtTotalGeneral.text =
                "Total gastado: S/ %.2f"
                    .format(totalGeneral)

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

            val dataSet =
                PieDataSet(
                    entries,
                    "Gastos por categoria"
                )

            dataSet.valueTextSize = 14f

            dataSet.sliceSpace = 3f

            dataSet.colors = listOf(
                android.graphics.Color.parseColor("#7E57C2"),
                android.graphics.Color.parseColor("#5E35B1"),
                android.graphics.Color.parseColor("#9575CD"),
                android.graphics.Color.parseColor("#26A69A"),
                android.graphics.Color.parseColor("#42A5F5"),
                android.graphics.Color.parseColor("#FFA726"),
                android.graphics.Color.parseColor("#EF5350")
            )

            val pieData =
                PieData(dataSet)

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

            if (groupedExpenses.isNotEmpty()) {

                val topCategory =
                    groupedExpenses.maxBy {
                        it.value.sumOf { expense ->
                            expense.amount
                        }
                    }

                val totalTop =
                    topCategory.value.sumOf {
                        it.amount
                    }

                txtTopCategory.text =
                    "Categoria principal: ${topCategory.key} (S/ %.2f)"
                        .format(totalTop)

                val ranking =
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

                val rankingText =
                    buildString {

                        append("Ranking de categorias\n\n")

                        ranking.forEachIndexed { index, item ->

                            append(
                                "${index + 1}. ${item.first}: S/ %.2f\n"
                                    .format(item.second)
                            )
                        }
                    }

                txtRanking.text =
                    rankingText
            }
        }

        btnBack.setOnClickListener {

            finish()
        }
    }
}
