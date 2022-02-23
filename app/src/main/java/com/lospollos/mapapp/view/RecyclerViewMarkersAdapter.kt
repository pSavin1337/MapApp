package com.lospollos.mapapp.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.MarkerOptions
import com.lospollos.mapapp.R

class RecyclerViewMarkersAdapter(private val markers: List<MarkerOptions>) :
    RecyclerView.Adapter<RecyclerViewMarkersAdapter.MarkersViewHolder>() {

    class MarkersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var markerTextView: TextView? = null

        init {
            markerTextView = itemView.findViewById(R.id.marker_text_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkersViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_markers_item, parent, false)
        return MarkersViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MarkersViewHolder, position: Int) {
        holder.markerTextView?.text =
            "${markers[position].position.latitude} ${markers[position].position.longitude}"
    }

    override fun getItemCount(): Int = markers.size

}