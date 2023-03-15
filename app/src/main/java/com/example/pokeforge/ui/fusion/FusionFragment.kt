package com.example.pokeforge.ui.fusion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pokeforge.MainActivity
import com.example.pokeforge.RemoteFusionActivity
import com.example.pokeforge.com.example.pokeforge.LocalSelectionActivity
import com.example.pokeforge.com.example.pokeforge.RemoteSelectionActivity
import com.example.pokeforge.databinding.FragmentFusionBinding

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
            // Start the remoteFusionActivity from this fragment
            val intent = Intent(activity, RemoteFusionActivity::class.java)
            intent.putExtra("pokepieces", (activity as MainActivity).binding.balance.text.toString().toInt())
            intent.putExtra("name", (activity as MainActivity).userName)
            intent.putExtra("sprite", (activity as MainActivity).userSprite)
            startActivity(intent)
        }

        btnLocalFusion.setOnClickListener {
            // Start the localFusionActivity from this fragment
            val intent = Intent(activity, LocalSelectionActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}