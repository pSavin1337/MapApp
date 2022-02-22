package com.lospollos.mapapp.data

import android.content.Context.MODE_PRIVATE
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.lospollos.mapapp.App.Companion.context
import com.lospollos.mapapp.Constants.LAT
import com.lospollos.mapapp.Constants.LNG
import com.lospollos.mapapp.Constants.SP_NAME

class MarkersRepository {

    fun saveMarkersList(markerList: List<Marker>) {
        val markersSharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE).edit()
        var markerParams = ""
        markerList.forEach {
            markerParams += "${it.position.latitude};${it.position.longitude}!"
        }
        val countSymbolsToDelete = 1
        markersSharedPreferences.putString("markers", markerParams.dropLast(countSymbolsToDelete))
        markersSharedPreferences.apply()
    }

    fun getMarkersList(): List<MarkerOptions> {
        val markersSharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE)
        val result = ArrayList<MarkerOptions>()
        markersSharedPreferences
            .getString("markers", null)
            ?.split('!')
            ?.forEach { markerInString ->
            val markerLatLng = markerInString.split(';')
            result.add(
                MarkerOptions().position(
                    LatLng(
                        markerLatLng[LAT].toDouble(),
                        markerLatLng[LNG].toDouble()
                    )
                )
            )
        }
        return result
    }

}