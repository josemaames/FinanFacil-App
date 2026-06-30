package upch.jamesss.finanfacil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val firebaseId: String = "",

    val amount: Double,

    val category: String,

    val description: String,

    val date: String,

    val timestamp: Long = System.currentTimeMillis(),

    val imageUrl: String = "",

    // Indica si el gasto fue marcado por el usuario
    // como potencialmente deducible para Impuesto a la Renta
    val isDeductible: Boolean = false

)
