package upch.jamesss.finanfacil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import upch.jamesss.finanfacil.data.local.database.AppDatabase
import upch.jamesss.finanfacil.data.local.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterExpenseActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_register_expense)

        val etAmount =
            findViewById<EditText>(R.id.etAmount)

        val spCategory =
            findViewById<Spinner>(R.id.spCategory)

        val etOtherCategory =
            findViewById<EditText>(R.id.etOtherCategory)

        val tilOtherCategory =
            findViewById<TextInputLayout>(R.id.tilOtherCategory)

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

        val db =
            AppDatabase.getDatabase(applicationContext)

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

        spCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val isOther =
                        spCategory.selectedItem.toString() == "Otros"

                    tilOtherCategory.visibility =
                        if (isOther) View.VISIBLE else View.GONE

                    if (!isOther) {

                        etOtherCategory.text.clear()
                    }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {}
            }

        btnSave.setOnClickListener {

            val amountText =
                etAmount.text.toString().trim()

            val category =
                if (spCategory.selectedItem.toString() == "Otros") {
                    etOtherCategory.text.toString().trim()
                } else {
                    spCategory.selectedItem.toString()
                }

            val description =
                etDescription.text.toString().trim()

            val amount =
                amountText.toDoubleOrNull()

            if (amount == null || amount <= 0.0) {

                etAmount.error =
                    "Ingresa un monto válido"

                return@setOnClickListener
            }

            if (spCategory.selectedItemPosition == 0) {

                Toast.makeText(
                    this,
                    "Selecciona una categoría",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (
                spCategory.selectedItem.toString() == "Otros" &&
                category.isEmpty()
            ) {

                etOtherCategory.error =
                    "Especifica la categoría"

                return@setOnClickListener
            }

            if (description.isEmpty()) {

                etDescription.error =
                    "Ingresa una descripción"

                return@setOnClickListener
            }

            val currentDate = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())

            val transaction = TransactionEntity(
                amount = amount,
                category = category,
                description = description,
                date = currentDate
            )

            lifecycleScope.launch {

                db.transactionDao()
                    .insertTransaction(transaction)

                Toast.makeText(
                    this@RegisterExpenseActivity,
                    "Gasto guardado 😎",
                    Toast.LENGTH_SHORT
                ).show()

                etAmount.text.clear()
                spCategory.setSelection(0)
                etOtherCategory.text.clear()
                etDescription.text.clear()

                selectedImageUri = null
                imgVoucher.setImageDrawable(null)
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
