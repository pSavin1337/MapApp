package com.lospollos.mapapp.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.*
import com.lospollos.mapapp.R

import com.lospollos.mapapp.Constants.MY_PERMISSIONS_REQUEST_LOCATION
import com.lospollos.mapapp.Constants.ZOOM_VALUE
import com.google.android.gms.tasks.OnSuccessListener
import com.lospollos.mapapp.App
import com.lospollos.mapapp.Constants
import com.lospollos.mapapp.view.IMap
import com.lospollos.mapapp.view.activity.MainActivity
import com.lospollos.mapapp.viewmodels.MapViewModel


class MapsFragment : Fragment() {

    private var map: IMap = GoogleIMapImpl()
    private lateinit var mapViewModel: MapViewModel
    private val markers = ArrayList<Marker>()

    private fun onPermissionsGrantedCallback() {

        mapViewModel.onMapCreate()
        mapViewModel.markersOptions.observe(viewLifecycleOwner) { markersOptionsList ->
            markersOptionsList.forEach {
                markers.add(
                    map.addMarker(
                        it.position.latitude,
                        it.position.longitude,
                        BitmapDescriptorFactory.HUE_RED
                    )
                )
            }
        }

        val deleteMarkersButton = activity?.findViewById<Button>(R.id.delete_markers_button)
        deleteMarkersButton?.setOnClickListener {
            markers.clear()
            map.clearMap()
            mapViewModel.onDeleteButtonClick()
        }

        val showMarkersButton = activity?.findViewById<Button>(R.id.all_markers_button)
        showMarkersButton?.setOnClickListener {
            mapViewModel.onAllMarkersButtonClick(markers)
            (activity as MainActivity).navController
                .navigate(R.id.action_mapsFragment_to_allMarkersFragment)
        }

        map.doOnGettingCurrentLocation(activity!!) { location ->
            map.zoomCamera(location.latitude, location.longitude)
        }
        val button = activity?.findViewById<Button>(R.id.new_marker_button)
        button?.setOnClickListener {
            map.doOnGettingCurrentLocation(activity!!) { location ->
                markers.add(
                    map.addMarker(
                        location.latitude,
                        location.longitude,
                        BitmapDescriptorFactory.HUE_GREEN
                    )
                )
            }
        }
    }

    private fun checkLocationPermission() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(context!!)
                    .setTitle(getString(R.string.alert_dialog_title))
                    .setMessage(getString(R.string.alert_dialog_message))
                    .setPositiveButton(getString(R.string.positive_button_text)) { _, _ ->
                        ActivityCompat.requestPermissions(
                            activity!!,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        mapViewModel.onRequestPermissionsResult(requestCode, grantResults)
        mapViewModel.permissionsGranted.observe(viewLifecycleOwner) {
            if (it) {
                onPermissionsGrantedCallback()
            } else {
                Toast.makeText(
                    App.context,
                    getString(R.string.permission_denied_text),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //context.getSharedPreferences(SP_NAME, MODE_PRIVATE).edit().clear().apply()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map)
        map.getMap(mapFragment!!) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    onPermissionsGrantedCallback()
                } else {
                    checkLocationPermission()
                }
            } else {
                onPermissionsGrantedCallback()
            }
        }
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
    }

    override fun onPause() {
        super.onPause()
        mapViewModel.onFragmentPause(markers)
        markers.clear()
    }

}