package com.example.text_it.fragment

import CallAdapter
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.text_it.R
import com.example.text_it.dataClass.CallInfo
import com.google.firebase.firestore.FirebaseFirestore

class Search : Fragment() {

    private lateinit var adapter: CallAdapter

    private val callList = mutableListOf<CallInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewCallList)
        adapter = CallAdapter(callList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchDataFromFirebase()

        val crossButton: ImageView = view.findViewById(R.id.imageButtonCross)
        crossButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val editTextSearch: EditText = view.findViewById(R.id.editTextSearch)
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCallList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun filterCallList(query: String) {
        val filteredList = mutableListOf<CallInfo>()
        for (call in callList) {
            if (call.name.contains(query, ignoreCase = true)) {
                filteredList.add(call)
            }
        }
        adapter.updateList(filteredList)
    }

    private fun fetchDataFromFirebase() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("USERS").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.data["name"].toString()
                    val phone = document.data["phone"].toString()
                    val profileImage = document.data["profileImage"].toString()
                    val call = CallInfo(name, phone, profileImage)
                    callList.add(call)
                }
                adapter.updateList(callList)
            }
            .addOnFailureListener { exception ->
                Log.d("Search", "Error getting documents: ", exception)
            }
    }
}


