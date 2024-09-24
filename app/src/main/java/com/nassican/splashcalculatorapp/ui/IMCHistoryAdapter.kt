package com.nassican.splashcalculatorapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nassican.splashcalculatorapp.R
import com.nassican.splashcalculatorapp.database.model.IMCRecord

class IMCHistoryAdapter(
    private val context: Context,
    private val imcRecords: MutableList<IMCRecord>
) :
    RecyclerView.Adapter<IMCHistoryAdapter.IMCViewHolder>() {

    class IMCViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTimeTextView: TextView = itemView.findViewById(R.id.dateTimeTextView)
        val weightHeightTextView: TextView = itemView.findViewById(R.id.weightHeightTextView)
        val bmiTextView: TextView = itemView.findViewById(R.id.bmiTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IMCViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_imc_record, parent, false)
        return IMCViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IMCViewHolder, position: Int) {
        val currentItem = imcRecords[position]
        val weightHeightText = context.getString(
            R.string.weight_height_text,
            currentItem.weight,
            currentItem.height
        )
        val bmiText = context.getString(R.string.bmi_text, currentItem.bmi)
        holder.dateTimeTextView.text = "${currentItem.date} ${currentItem.time}"
        holder.weightHeightTextView.text = weightHeightText
        holder.bmiTextView.text = bmiText
    }

    override fun getItemCount() = imcRecords.size

    fun updateRecords(newRecords: List<IMCRecord>) {
        imcRecords.clear()
        imcRecords.addAll(newRecords)
        notifyDataSetChanged()
    }
}