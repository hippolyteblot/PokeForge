package com.example.pokeforge.ui.maps

import android.content.ContentValues.TAG
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pokeforge.MainActivity
import com.example.pokeforge.Pokemon
import com.example.pokeforge.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        var arrayPoint = ArrayList<LatLng>()
        val db = Firebase.firestore
        val collectionRef = db.collection("users")
        Log.d("A", "A")
        collectionRef
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("A", "${document.id} => ${document.data}")
                    if (document.id == "TtZDNUvduLgIJR3C7igOXodhHJ02") {
                        Log.d("A", "${document.id} => ${document.data}}")
                        val geometry = document.data["geometry"] as GeoPoint
                        googleMap.addMarker(MarkerOptions().position(LatLng(geometry.latitude, geometry.longitude)).title("Marker"))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(geometry.latitude, geometry.longitude)))

                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("A", "Error getting documents.", exception)
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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}