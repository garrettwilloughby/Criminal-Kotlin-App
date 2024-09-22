package com.bignerdranch.android.criminalintent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.criminalintent.databinding.ListItemCrimeBinding
import java.util.UUID

//maybe comment out this code if it doesn't work
class CrimeHolder(val binding : ListItemCrimeBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(crime: Crime, onCrimeClicked: (crimeId: UUID) -> Unit) {
        binding.crimeTitle.text = crime.title
        binding.crimeDate.text = crime.date.toString()

        binding.root.setOnClickListener {
            onCrimeClicked(crime.id)
        }
        binding.crimeSolved.visibility = if (crime.isSolved) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}




class CrimeListAdapter (
    private val crimes : List<Crime>,
    private val onCrimeClicked: (crimeId : UUID) -> Unit
    ): RecyclerView.Adapter<CrimeHolder>(){

    //know these for the exam?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        //context is an activity
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCrimeBinding.inflate(inflater, parent, false)
        return CrimeHolder(binding)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = crimes[position]
//        holder.apply {
//            binding.crimeTitle.text = crime.title
//            binding.crimeDate.text = crime.date.toString()
//        }
        holder.bind(crime, onCrimeClicked)


    }
    override fun getItemCount() = crimes.size

}