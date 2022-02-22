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

import com.lospollos.mapapp.Constants.INTERVAL_TIME_IN_MILLIS
import com.lospollos.mapapp.Constants.MY_PERMISSIONS_REQUEST_LOCATION
import com.lospollos.mapapp.Constants.ZOOM_VALUE
import com.google.android.gms.tasks.OnSuccessListener
import com.lospollos.mapapp.App
import com.lospollos.mapapp.Constants
import com.lospollos.mapapp.viewmodels.MapViewModel


class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    //private var mCurrLocationMarker: Marker? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var mapViewModel: MapViewModel
    private val markers = ArrayList<Marker>()

    private fun setMarkerOnLocation(location: Location) : Marker {
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        return googleMap.addMarker(markerOptions)!!
    }

    @SuppressLint("MissingPermission")
    fun onPermissionsGrantedCallback() {

        mapViewModel.onMapCreate()
        mapViewModel.markersOptions.observe(viewLifecycleOwner) { markersOptionsList ->
            markersOptionsList.forEach {
                markers.add(googleMap.addMarker(it)!!)
            }
        }

        mFusedLocationClient?.lastLocation?.addOnSuccessListener(activity!!) { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                val cameraPosition = CameraPosition.Builder()
                    .target(latLng)
                    .zoom(ZOOM_VALUE)
                    .build()
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
                googleMap.animateCamera(cameraUpdate)
            }
        }
        val button = activity?.findViewById<Button>(R.id.new_marker_button)
        button?.setOnClickListener {
            mFusedLocationClient?.lastLocation?.addOnSuccessListener(activity!!) { location ->
                if (location != null) {
                    markers.add(setMarkerOnLocation(location))
                }
            }
        }
        googleMap.isMyLocationEnabled = true
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { map ->

        this.googleMap = map
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
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

    @SuppressLint("MissingPermission")
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
        //App.context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mFusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
        mapFragment?.getMapAsync(callback)
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        mapViewModel.onFragmentDestroy(markers)
    }

}