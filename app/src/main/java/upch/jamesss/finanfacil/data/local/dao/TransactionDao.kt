package upch.jamesss.finanfacil.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import upch.jamesss.finanfacil.data.local.entity.TransactionEntity

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(
        transaction: TransactionEntity
    )

    @Delete
    suspend fun deleteTransaction(
        transaction: TransactionEntity
    )

    @Update
    suspend fun updateTransaction(
        transaction: TransactionEntity
    )

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(
        id: Int
    ): TransactionEntity?

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("""
SELECT *
FROM transactions
WHERE isDeductible = 1
ORDER BY timestamp DESC
""")
    suspend fun getDeductibleExpenses(): List<TransactionEntity>
}