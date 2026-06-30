package upch.jamesss.finanfacil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import upch.jamesss.finanfacil.data.local.database.AppDatabase

class TaxAssistantActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tax_assistant)

        db = AppDatabase.getDatabase(applicationContext)

        val txtTotal =
            findViewById<TextView>(R.id.txtTotalDeducible)

        val txtInfo =
            findViewById<TextView>(R.id.txtTopCategory)

        val txtRecommendations =
            findViewById<TextView>(R.id.txtInsights)

        val btnViewDeductibles =
            findViewById<Button>(R.id.btnViewDeductibles)

        val btnBack =
            findViewById<Button>(R.id.btnBack)

        btnViewDeductibles.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    DeductibleExpensesActivity::class.java
                )
            )

        }

        btnBack.setOnClickListener {

            finish()

        }

        lifecycleScope.launch {

            val expenses =
                db.transactionDao()
                    .getAllTransactions()

            val deductible =
                expenses.filter {
                    it.isDeductible
                }

            val noDeductible =
                expenses.filter {
                    !it.isDeductible
                }

            val totalDeductible =
                deductible.sumOf {
                    it.amount
                }

            val totalNoDeductible =
                noDeductible.sumOf {
                    it.amount
                }

            val porcentaje =
                if ((totalDeductible + totalNoDeductible) > 0) {

                    ((totalDeductible /
                            (totalDeductible + totalNoDeductible)) * 100).toInt()

                } else {

                    0

                }

            txtTotal.text =
                "S/ %.2f".format(totalDeductible)

            txtInfo.text =
                """
🟢 Gastos potencialmente deducibles:
S/ %.2f

🔴 Gastos no deducibles:
S/ %.2f

📈 Porcentaje deducible:
%d%%

📄 Gastos marcados como deducibles:
%d
                """.trimIndent().format(
                    totalDeductible,
                    totalNoDeductible,
                    porcentaje,
                    deductible.size
                )

            val recomendacion =
                when {

                    totalDeductible == 0.0 ->
                        "⚠️ No has registrado gastos potencialmente deducibles."

                    porcentaje >= 50 ->
                        "✅ Excelente. Una gran parte de tus gastos podría ser deducible."

                    porcentaje >= 25 ->
                        "💡 Tienes algunos gastos potencialmente deducibles. Conserva siempre tus comprobantes."

                    else ->
                        "📌 Considera identificar y registrar correctamente tus gastos deducibles para mejorar tu planificación tributaria."

                }

            txtRecommendations.text =
                """
💡 RECOMENDACIONES

$recomendacion

────────────────────────

Recuerda:

• Conserva tus comprobantes de pago.

• Solicita comprobantes válidos.

• Revisa periódicamente tus gastos deducibles.

• La información mostrada es únicamente referencial.

• La deducibilidad final dependerá de la normativa tributaria vigente.
                """.trimIndent()

        }

    }

}