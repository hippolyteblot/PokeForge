package com.example.pokeforge.ui.fusion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.pokeforge.MainActivity
import com.example.pokeforge.RemoteFusionActivity
import com.example.pokeforge.com.example.pokeforge.LocalSelectionActivity
import com.example.pokeforge.com.example.pokeforge.RemoteSelectionActivity
import com.example.pokeforge.databinding.FragmentFusionBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FusionFragment : Fragment() {

    private var _binding: FragmentFusionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(FusionViewModel::class.java)

        _binding = FragmentFusionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val btnLocalFusion = binding.btnFusionLocal
        val btnRemoteFusion = binding.btnFusionRemote

        btnRemoteFusion.setOnClickListener {
            // Check if the user has enough dna points to start a remote fusion
            lifecycleScope.launch {
                if(hasFusionItems()) {
                    // Start the remoteFusionActivity from this fragment
                    val intent = Intent(activity, RemoteFusionActivity::class.java)
                    intent.putExtra("pokepieces", (activity as MainActivity).binding.balance.text.toString().toInt())
                    intent.putExtra("name", (activity as MainActivity).userName)
                    intent.putExtra("sprite", (activity as MainActivity).userSprite)
                    startActivity(intent)
                } else {
                    val toast = Toast.makeText(activity, "Vous n'avez pas de pointeau ADN !", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }

        btnLocalFusion.setOnClickListener {
            lifecycleScope.launch {
                if(hasFusionItems()) {
                    // Start the localFusionActivity from this fragment
                    val intent = Intent(activity, LocalSelectionActivity::class.java)
                    startActivity(intent)
                } else {
                    val toast = Toast.makeText(activity, "Vous n'avez pas de pointeau ADN !", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }

        return root
    }

    private suspend fun hasFusionItems(): Boolean {
        val db = Firebase.firestore
        val userUID = (activity as MainActivity).userUID
        val userRef = db.collection("users").document(userUID)
        try {
            val document = userRef.get().await()
            if (document != null) {
                val fusionItems = document.data?.get("fusionItems").toString().toInt()
                if (fusionItems > 0) {
                    return true
                }
            }
        } catch (e: Exception) {
            Log.d("poke", "get failed with ", e)
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}