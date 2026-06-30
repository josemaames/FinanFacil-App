package upch.jamesss.finanfacil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import upch.jamesss.finanfacil.data.local.entity.TransactionEntity
import java.util.Locale

class ExpenseAdapter(

    private val onItemClick: (TransactionEntity) -> Unit,
    private val onDeleteClick: (TransactionEntity) -> Unit

) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val expenses = mutableListOf<TransactionEntity>()

    inner class ExpenseViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val txtCategory: TextView =
            view.findViewById(R.id.txtCategory)

        val txtAmount: TextView =
            view.findViewById(R.id.txtAmount)

        val txtDescription: TextView =
            view.findViewById(R.id.txtDescription)

        val txtDate: TextView =
            view.findViewById(R.id.txtDate)

        val btnDelete: Button =
            view.findViewById(R.id.btnDelete)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_expense,
                parent,
                false
            )

        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ExpenseViewHolder,
        position: Int
    ) {

        val expense = expenses[position]

        holder.txtCategory.text =
            expense.category

        holder.txtAmount.text =
            String.format(
                Locale.getDefault(),
                "S/ %.2f",
                expense.amount
            )

        holder.txtDescription.text =
            expense.description

        holder.txtDate.text =
            expense.date

        holder.itemView.setOnClickListener {

            onItemClick(expense)

        }

        holder.btnDelete.setOnClickListener {

            onDeleteClick(expense)

        }

    }

    override fun getItemCount(): Int {

        return expenses.size

    }

    fun updateData(
        newExpenses: List<TransactionEntity>
    ) {

        expenses.clear()

        expenses.addAll(newExpenses)

        notifyDataSetChanged()

    }

    fun removeExpense(
        expense: TransactionEntity
    ) {

        val position =
            expenses.indexOf(expense)

        if (position != -1) {

            expenses.removeAt(position)

            notifyItemRemoved(position)

        }

    }

}