package upch.jamesss.finanfacil

import android.os.Bundle
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

        val txtTopCategory =
            findViewById<TextView>(R.id.txtTopCategory)

        val btnBack =
            findViewById<Button>(R.id.btnBack)

        val db =
            AppDatabase.getDatabase(applicationContext)

        lifecycleScope.launch {

            val expenses =
                db.transactionDao()
                    .getAllTransactions()

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
                    "Gastos por categoría"
                )

            val pieData =
                PieData(dataSet)

            pieChart.data =
                pieData

            pieChart.description.isEnabled =
                false

            pieChart.centerText =
                "FinanFácil"

            pieChart.animateY(1200)

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
                    "🏆 Categoría principal: ${topCategory.key} (S/ %.2f)"
                        .format(totalTop)
            }
        }

        btnBack.setOnClickListener {

            finish()
        }
    }
}