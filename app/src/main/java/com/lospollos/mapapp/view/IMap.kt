package com.lospollos.mapapp.view

import android.app.Activity
import android.location.Location
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.Marker

interface IMap {
    fun getMap(fragment: Fragment, callback: (map: IMap) -> Unit)
    fun addMarker(lat: Double, lng: Double, color: Float): Marker
    fun clearMap()
    fun doOnGettingCurrentLocation(activity: Activity, callback: (location: Location) -> Unit)
    fun zoomCamera(latitude: Double, longitude: Double)
}