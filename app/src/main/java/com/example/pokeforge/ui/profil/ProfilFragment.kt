package com.example.pokeforge.ui.profil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeforge.MainActivity
import com.example.pokeforge.PokemonTeam
import com.example.pokeforge.R
import com.example.pokeforge.databinding.FragmentProfilBinding
import com.example.pokeforge.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfilFragment : Fragment(){
    private var _binding: FragmentProfilBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val db = Firebase.firestore
        //get the user id from the main activity
        val activity = requireActivity() as MainActivity
        val userUID = activity.userUID
        //get the user document from the database
        val userRef = db.collection("users").document(userUID)
        userRef.get().addOnSuccessListener { document ->
            if (document != null) {
                //set the username
                Log.d("poke", "DocumentSnapshot data: ${document.data})")
                binding.textViewPseudo.text = document.data?.get("name").toString()
                //set the image
                val image = document.data?.get("sprite").toString()

                val spriteRsc = resources.getIdentifier(image, "drawable", activity.packageName)

                binding.imageViewProfilPic.setImageResource(spriteRsc)

        } else {
            val i=1
            }
            }
                .addOnFailureListener { exception ->
                Log.d("poke", "get failed with ", exception)
            }
        val nbPokemonRef = db.collection("pokemons").whereEqualTo("owner", userUID)
        nbPokemonRef.get().addOnSuccessListener { documents ->
            if (documents != null) {
                //set the username
                Log.d("poke", "DocumentSnapshot data: ${documents.size()})")
                binding.textViewNiveau.text = documents.size().toString()
            } else {
                val i=1
            }
        }
            .addOnFailureListener { exception ->
                Log.d("poke", "get failed with ", exception)
            }

        binding.buttonDeconnexion.setOnClickListener {
            //firebase auth signout
            FirebaseAuth.getInstance().signOut()
            //go back to the login activity
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)

        }




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}