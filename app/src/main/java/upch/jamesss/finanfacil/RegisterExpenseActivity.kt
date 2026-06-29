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
import android.widget.TextView
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterExpenseActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    private var editingTransaction: TransactionEntity? = null
    private lateinit var auth: FirebaseAuth

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_register_expense)
        auth = FirebaseAuth.getInstance()

        firestore = FirebaseFirestore.getInstance()

        val txtTitle =
            findViewById<TextView>(R.id.txtTitle)

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

        val firebaseId =
            intent.getStringExtra(EXTRA_FIREBASE_ID)

        fun selectCategory(category: String) {

            for (index in 0 until spCategory.count) {

                if (spCategory.getItemAtPosition(index).toString() == category) {

                    spCategory.setSelection(index)
                    return
                }
            }

            spCategory.setSelection(spCategory.count - 1)
            etOtherCategory.setText(category)
        }

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

        if (!firebaseId.isNullOrEmpty()) {

            txtTitle.text =
                "Editar gasto"

            btnSave.text =
                "Actualizar gasto"

        }

        btnSave.setOnClickListener {

            val amountText = etAmount.text.toString().trim()

            val category =
                if (spCategory.selectedItem.toString() == "Otros") {
                    etOtherCategory.text.toString().trim()
                } else {
                    spCategory.selectedItem.toString()
                }

            val description = etDescription.text.toString().trim()

            val amount = amountText.toDoubleOrNull()

            if (amount == null || amount <= 0.0) {
                etAmount.error = "Ingresa un monto válido"
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
                spCategory.selectedItem.toString() == "Otros"
                && category.isEmpty()
            ) {
                etOtherCategory.error = "Especifica la categoría"
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                etDescription.error = "Ingresa una descripción"
                return@setOnClickListener
            }

            lifecycleScope.launch {

                val currentEditingTransaction = editingTransaction

                // NUEVO GASTO
                if (currentEditingTransaction == null) {

                    val currentDate = SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    ).format(Date())

                    val uid = auth.currentUser?.uid

                    if (uid == null) {

                        Toast.makeText(
                            this@RegisterExpenseActivity,
                            "Debes iniciar sesión",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@launch
                    }

                    val document = firestore.collection("gastos").document()

                    val firebaseId = document.id

                    val gasto = hashMapOf(

                        "uidUsuario" to uid,
                        "monto" to amount,
                        "categoria" to category,
                        "descripcion" to description,
                        "fecha" to currentDate,
                        "timestamp" to System.currentTimeMillis()

                    )

                    document.set(gasto)
                        .addOnSuccessListener {

                            lifecycleScope.launch {

                                val transaction = TransactionEntity(

                                    firebaseId = firebaseId,
                                    amount = amount,
                                    category = category,
                                    description = description,
                                    date = currentDate,
                                    timestamp = System.currentTimeMillis()

                                )

                                db.transactionDao().insertTransaction(transaction)

                                Toast.makeText(
                                    this@RegisterExpenseActivity,
                                    "Gasto guardado correctamente",
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
                        .addOnFailureListener {

                            Toast.makeText(
                                this@RegisterExpenseActivity,
                                "No se pudo guardar en Firebase",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    return@launch
                }

                // EDITAR (por ahora solo Room)
                val gastoActualizado = hashMapOf(

                    "uidUsuario" to auth.currentUser!!.uid,

                    "monto" to amount,

                    "categoria" to category,

                    "descripcion" to description,

                    "fecha" to currentEditingTransaction.date,

                    "timestamp" to currentEditingTransaction.timestamp

                )

                firestore.collection("gastos")
                    .document(currentEditingTransaction.firebaseId)
                    .set(gastoActualizado)
                    .addOnSuccessListener {

                        lifecycleScope.launch {

                            val updatedTransaction =
                                currentEditingTransaction.copy(

                                    amount = amount,
                                    category = category,
                                    description = description

                                )

                            db.transactionDao()
                                .updateTransaction(updatedTransaction)

                            Toast.makeText(
                                this@RegisterExpenseActivity,
                                "Gasto actualizado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        }

                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this@RegisterExpenseActivity,
                            "No se pudo actualizar en Firebase",
                            Toast.LENGTH_SHORT
                        ).show()

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

    companion object {

        const val EXTRA_EXPENSE_ID =
            "extra_expense_id"
        const val EXTRA_FIREBASE_ID =
            "extra_firebase_id"

        private const val INVALID_EXPENSE_ID =
            -1
    }
}
