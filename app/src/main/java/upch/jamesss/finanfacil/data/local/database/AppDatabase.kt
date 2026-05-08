package upch.jamesss.finanfacil.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import upch.jamesss.finanfacil.data.local.dao.TransactionDao
import upch.jamesss.finanfacil.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
}