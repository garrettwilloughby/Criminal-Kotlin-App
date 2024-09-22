package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import java.net.URI
import java.util.Date
import java.util.UUID

private const val DATE_FORMAT = "EEE, MMM, dd"
//making this a subclass of fragment
class CrimeDetailFragment : Fragment() {

    private var _binding : FragmentCrimeDetailBinding? = null
    private val args: CrimeDetailFragmentArgs by navArgs()
    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels{
        CrimeDetailViewModelFactory(args.crimeId)
    }

    private val binding
        get() = checkNotNull(_binding){
            "Binding is null, can you see the view?"
        }

//    private val selectSuspect =
//        registerForActivityResult(ActivityResultContracts.PickContact()){
//            uri: URI ->
//            //TODO
//        }

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
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply{
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                //don't need the other vars

                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }
//
//            crimeDate.apply{
//                isEnabled = false
//            }

//            crimeSuspect.setOnClickListener {
//                selectSuspect.launch(null)
//            }

            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }

                //crime = crime.copy(isSolved = isChecked)
            }


        }





        viewLifecycleOwner.lifecycleScope.launch{

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                crimeDetailViewModel.crime.collect{ crime->
                    crime?.let{updateUi(it)}
                }
            }

        }
        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ){requestKey, bundle ->
            val newDate = bundle.getSerializable(
                DatePickerFragment.BUNDLE_KEY_DATE
            ) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }
    }
    private fun updateUi(crime: Crime){
        binding.apply {
            if(crimeTitle.text.toString() != crime.title){
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeDate.setOnClickListener {
                findNavController().navigate(CrimeDetailFragmentDirections.selectDate(crime.date))
            }
            crimeSolved.isChecked = crime.isSolved

            crimeReport.setOnClickListener {
                val reportIntent = Intent(Intent.ACTION_SEND).apply{
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
                }
                //startActivity(reportIntent)
                val chooserIntent = Intent.createChooser(
                    reportIntent,
                    //replace with report text
                    getString(R.string.send_report)
                )
                startActivity(chooserIntent)
            }
            crimeSuspect.text = crime.suspect.ifEmpty {
                getString(R.string.crime_suspect_text)
            }

            //set listener
            deleteReport.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    crimeDetailViewModel.deleteCrime(args.crimeId)
                    findNavController().navigateUp()
                }

            }
        }
    }

    //new
//    private fun parseContactSelection(contractUri: Uri){
//        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
//
//        val queryCursor = requireActivity().contentResolver.query(
//            contractUri, queryFields, null, null, null
//        )
//
//        queryCursor?.use { cursor ->
//            if(cursor.moveToFirst()){
//                //fetch name
//                val suspect = cursor.getString(0)
//                crimeDetailViewModel.updateCrime {oldCrime ->
//                    oldCrime.copy(suspect = suspect)
//                }
//            }
//
//        }
//    }

    //delete function


    private fun getCrimeReport(crime : Crime) : String{
        val solvedString = if(crime.isSolved){
            getString(R.string.crime_report_solved)
        }else{
            getString(R.string.crime_report_unsolved)
        }

        val suspectText = if (crime.suspect.isBlank()){
            getString(R.string.crime_report_no_suspect)
        }else{
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()

        return getString(
            R.string.crime_report,
            crime.title,
            dateString,
            solvedString,
            suspectText
        )
    }

}