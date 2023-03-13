package com.example.pokeforge.ui.dashboard

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeforge.APISpritesClient
import com.example.pokeforge.MainActivity
import com.example.pokeforge.R
import com.example.pokeforge.SpriteSelectionAdapter
import com.example.pokeforge.databinding.FragmentDashboardBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding:FragmentDashboardBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textDashboard
        //dashboardViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}
        binding.buyLegendaryButton.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmer l'achat")
            builder.setMessage("Voulez-vous acheter cet oeuf ?")
            builder.setPositiveButton("Oui", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(activity, "Oeuf ajouté !", Toast.LENGTH_SHORT).show()
                binding.textViewMoney.text = (binding.textViewMoney.text.toString().toInt() - 1000).toString()

                var poke = ""
                val db = Firebase.firestore
                val UID = (activity as MainActivity).userUID
                val collectionRef = db.collection("pokemon_available")

                collectionRef.whereEqualTo("isMythical", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        collectionRef.whereEqualTo("isLegendary", true)
                            .get()
                            .addOnSuccessListener { documents2 ->
                                val listeLegend = documents.documents
                                val listeMyth = documents2.documents
                                val liste = listeLegend + listeMyth
                                val taille = liste.size
                                val random = (0 until taille).random()
                                val pokemon = liste[random]
                                println("DocumentSnapshot data: ${pokemon.data}")
                                poke = pokemon.data?.get("name").toString()
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("TAG", "Error getting documents: ", exception)
                    }

            })
            builder.setNegativeButton("Non", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(activity, "Oeuf non ajouté !", Toast.LENGTH_SHORT).show()
            })
            builder.show()
        }
        val main = activity as MainActivity

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}