package com.lospollos.mapapp.viewmodels

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.lospollos.mapapp.Constants
import com.lospollos.mapapp.R
import com.lospollos.mapapp.App.Companion.context
import com.lospollos.mapapp.data.MarkersRepository

class MapViewModel : ViewModel() {

    private val markersRepository = MarkersRepository()

    val markersOptions = MutableLiveData<List<MarkerOptions>>()
    val permissionsGranted = MutableLiveData<Boolean>()

    fun onFragmentDestroy(markers: List<Marker>) {
        markersRepository.saveMarkersList(markers)
    }

    fun onMapCreate() {
        markersOptions.value = markersRepository.getMarkersList()
    }

    fun onDeleteButtonClick() {
        markersRepository.clearMarkersStorage()
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionsGranted.value = true
                    }

                } else {
                    permissionsGranted.value = false
                }
            }
        }
    }

}