package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeListBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

//know why this needs to be at the top level, always private
private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment(){
    private val crimeListViewModel : CrimeListViewModel by viewModels()

    private var _binding : FragmentCrimeListBinding? = null
    private lateinit var crime : Crime
    //private lateinit var binding : FragmentCrimeDetailBinding
    private val binding
        get() = checkNotNull(_binding){
            "Binding is null, can you see the view?"
        }

////comment this in/out
//    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


//        job = viewLifecycleOwner.lifecycleScope.launch {
//            val crimes = crimeListViewModel.loadCrimes()
//            binding.crimeRecyclerView.adapter = CrimeListAdapter(crimes)
//        }
    }


    //comment this in/out

//    override fun onStart() {
//
//
//        super.onStart()
//        job = viewLifecycleOwner.lifecycleScope.launch {
//            val crimes = crimeListViewModel.loadCrimes()
//            binding.crimeRecyclerView.adapter = CrimeListAdapter(crimes)
//        }
//
//
//        Log.d(TAG, "Job launching in onStart")
//    }
//
//
//    override fun onStop(){
//
//        super.onStop()
//        job?.cancel()
//
//
//        //use back out button
//        Log.d(TAG, "Job getting canceled in onStop")
//    }




    override fun onDestroyView() {
        super.onDestroyView()
        //quick way to free up is to release the binding
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeListBinding.inflate(layoutInflater, container, false)
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)
//why did we comment out???
//        val crimes = crimeListViewModel.crimes
//        val adapter = CrimeListAdapter(crimes)
//        binding.crimeRecyclerView.adapter = adapter
        return binding.root
    }

    //built in repeat on lifecycle, allows to repeat the code while its still in one specific lifecycle state
    //also a suspending function

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                //val crimes = crimeListViewModel.loadCrimes()
                crimeListViewModel.crimes.collect{ crimes ->
                    binding.crimeRecyclerView.adapter = CrimeListAdapter(crimes) { crimeId ->

                        findNavController().navigate(
                            CrimeListFragmentDirections.showCrimeDetail(crimeId)
                        )
                    }
                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.crime_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //when acts like a loop, if we make it through the loop, it will execute the else
        return when(item.itemId){
            R.id.new_crime -> {
                showNewCrime()
                //if it doesn't it will execute the true
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showNewCrime(){ //make a new crime at very least, possibly do other stuff
        viewLifecycleOwner.lifecycleScope.launch {
            val newCrime = Crime(
                id = UUID.randomUUID(),
                title = "",
                date = Date(),
                isSolved = false
            )
            crimeListViewModel.addCrime(newCrime)
            findNavController().navigate(CrimeListFragmentDirections.showCrimeDetail(newCrime.id))
        }
    }




}



