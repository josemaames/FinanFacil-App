package upch.jamesss.finanfacil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch
import upch.jamesss.finanfacil.data.local.database.AppDatabase
import upch.jamesss.finanfacil.data.local.entity.TransactionEntity

class RegisterExpenseActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_register_expense)

        val etAmount =
            findViewById<EditText>(R.id.etAmount)

        val etCategory =
            findViewById<EditText>(R.id.etCategory)

        val etDescription =
            findViewById<EditText>(R.id.etDescription)

        val btnSave =
            findViewById<Button>(R.id.btnSaveExpense)

        val btnGoExpenses =
            findViewById<Button>(R.id.btnGoExpenses)

        val btnBackHome =
            findViewById<Button>(R.id.btnBackHome)

        val btnSelectVoucher =
            findViewById<Button>(R.id.btnSelectVoucher)

        val imgVoucher =
            findViewById<ImageView>(R.id.imgVoucher)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "finanfacil_db"
        ).build()

        val imagePicker =
            registerForActivityResult(
                ActivityResultContracts.GetContent()
            ) { uri ->

                if (uri != null) {

                    selectedImageUri = uri

                    imgVoucher.setImageURI(uri)
                }
            }

        btnSelectVoucher.setOnClickListener {

            imagePicker.launch("image/*")
        }

        btnSave.setOnClickListener {

            val amountText =
                etAmount.text.toString()

            val category =
                etCategory.text.toString()

            val description =
                etDescription.text.toString()

            if (
                amountText.isNotEmpty()
                && category.isNotEmpty()
                && description.isNotEmpty()
            ) {

                val transaction = TransactionEntity(
                    amount = amountText.toDouble(),
                    category = category,
                    description = description
                )

                lifecycleScope.launch {

                    db.transactionDao()
                        .insertTransaction(transaction)

                    runOnUiThread {

                        Toast.makeText(
                            this@RegisterExpenseActivity,
                            "Gasto guardado 😎",
                            Toast.LENGTH_SHORT
                        ).show()

                        etAmount.text.clear()
                        etCategory.text.clear()
                        etDescription.text.clear()

                        imgVoucher.setImageDrawable(null)
                    }
                }
            }
        }

        btnGoExpenses.setOnClickListener {

            val intent = Intent(
                this,
                ExpensesActivity::class.java
            )

            startActivity(intent)
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