package com.lospollos.mapapp.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.lospollos.mapapp.Constants
import com.lospollos.mapapp.view.IMap

class GoogleIMapImpl : IMap {

    lateinit var googleMap: GoogleMap
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    @SuppressLint("MissingPermission")
    override fun getMap(fragment: Fragment, callback: (map: IMap) -> Unit) {
        val onMapReadyCallback = OnMapReadyCallback {
            this.googleMap = it
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            googleMap.isMyLocationEnabled = true
            callback(this)
        }
        mFusedLocationClient =
            fragment.activity?.let { LocationServices.getFusedLocationProviderClient(it) }
        (fragment as SupportMapFragment).getMapAsync(onMapReadyCallback)
    }

    override fun addMarker(lat: Double, lng: Double, color: Float): Marker {
        val latLng = LatLng(lat, lng)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color))
        return googleMap.addMarker(markerOptions)!!
    }

    override fun clearMap() {
        googleMap.clear()
    }

    @SuppressLint("MissingPermission")
    override fun doOnGettingCurrentLocation(
        activity: Activity,
        callback: (location: Location) -> Unit
    ) {
        mFusedLocationClient?.lastLocation?.addOnSuccessListener(activity) {
            callback(it)
        }
    }

    override fun zoomCamera(latitude: Double, longitude: Double) {
        val latLng = LatLng(latitude, longitude)
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(Constants.ZOOM_VALUE)
            .build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        googleMap.animateCamera(cameraUpdate)
    }

}