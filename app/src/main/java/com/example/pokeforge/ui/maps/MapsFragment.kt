package com.example.pokeforge.ui.maps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pokeforge.R
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        val db = Firebase.firestore
        val collectionRef = db.collection("users")
        Log.d("A", "A")
        collectionRef
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.data["latitude"] != null && document.data["longitude"] != null) {
                        Log.d("Arderdrdrxxdxdxdxdx", "${document.id} => ${document.data}")
                        val geoPointLat = document.data["latitude"] as Double
                        val geoPointLong = document.data["longitude"] as Double
                        Log.d("A", geoPointLat.toString())
                        val lat = geoPointLat
                        val long = geoPointLong
                        Log.d("A", lat.toString() + " " + long)
                        val point = LatLng(lat, long)
                        googleMap.addMarker(MarkerOptions().position(point).title(document.id))
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

private operator fun Any.get(s: String): Any {
    return s
}
