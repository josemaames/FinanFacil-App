package upch.jamesss.finanfacil.utils

import upch.jamesss.finanfacil.data.local.entity.TransactionEntity
import java.util.Calendar

object StatisticsUtils {

    fun getTotalExpenses(
        expenses: List<TransactionEntity>
    ): Double {

        return expenses.sumOf {
            it.amount
        }
    }

    fun getAverageExpense(
        expenses: List<TransactionEntity>
    ): Double {

        if (expenses.isEmpty()) return 0.0

        return expenses.sumOf {
            it.amount
        } / expenses.size
    }

    fun getMonthlyExpenses(
        expenses: List<TransactionEntity>
    ): Double {

        val startOfMonth =
            Calendar.getInstance().apply {

                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

            }.timeInMillis

        return expenses
            .filter {
                it.timestamp >= startOfMonth
            }
            .sumOf {
                it.amount
            }
    }

    fun getTopCategory(
        expenses: List<TransactionEntity>
    ): String {

        if (expenses.isEmpty()) return "-"

        return expenses
            .groupBy {
                it.category
            }
            .maxByOrNull {
                it.value.sumOf { expense ->
                    expense.amount
                }
            }?.key ?: "-"
    }

    fun getExpensesByMonth(
        expenses: List<TransactionEntity>
    ): LinkedHashMap<String, Double> {

        val formatter =
            java.text.SimpleDateFormat(
                "MMM",
                java.util.Locale("es", "PE")
            )

        val grouped =
            expenses.groupBy {

                formatter.format(
                    java.util.Date(it.timestamp)
                )
            }

        val result =
            LinkedHashMap<String, Double>()

        grouped.forEach { (month, list) ->

            result[month] =
                list.sumOf { it.amount }

        }

        return result
    }

    fun getCurrentMonthExpenses(
        expenses: List<TransactionEntity>
    ): Double {

        val calendar = Calendar.getInstance()

        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        return expenses.filter {

            val c = Calendar.getInstance()

            c.timeInMillis = it.timestamp

            c.get(Calendar.MONTH) == month &&
                    c.get(Calendar.YEAR) == year

        }.sumOf {

            it.amount

        }

    }

    fun getPreviousMonthExpenses(
        expenses: List<TransactionEntity>
    ): Double {

        val calendar = Calendar.getInstance()

        calendar.add(Calendar.MONTH, -1)

        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        return expenses.filter {

            val c = Calendar.getInstance()

            c.timeInMillis = it.timestamp

            c.get(Calendar.MONTH) == month &&
                    c.get(Calendar.YEAR) == year

        }.sumOf {

            it.amount

        }

    }

}

