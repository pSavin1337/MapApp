package com.lospollos.mapapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.lospollos.mapapp.R
import com.lospollos.mapapp.view.RecyclerViewMarkersAdapter
import com.lospollos.mapapp.view.activity.MainActivity
import com.lospollos.mapapp.viewmodels.MapViewModel

class AllMarkersFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_markers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
        val backButton = activity?.findViewById<Button>(R.id.back_button)
        backButton?.setOnClickListener {
            (activity as MainActivity).navController.popBackStack()
        }
        mapViewModel.onMarkersListCreate()
        mapViewModel.markersOptions.observe(viewLifecycleOwner) {
            recyclerView = activity?.findViewById(R.id.recycler_view)!!
            recyclerView.layoutManager = LinearLayoutManager(activity)
            val adapter = RecyclerViewMarkersAdapter(it)
            recyclerView.adapter = adapter
        }
    }

}