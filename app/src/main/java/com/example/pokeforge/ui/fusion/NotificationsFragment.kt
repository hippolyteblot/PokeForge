package com.example.pokeforge.ui.fusion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pokeforge.LocalFusionActivity
import com.example.pokeforge.MainActivity
import com.example.pokeforge.databinding.FragmentFusionBinding

class NotificationsFragment : Fragment() {

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
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentFusionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val btnLocalFusion = binding.btnFusionLocal
        val btnRemoteFusion = binding.btnFusionRemote

        btnLocalFusion.setOnClickListener {
            // Start the localFusionActivity from this fragment
            val intent = Intent(activity, LocalFusionActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}