package com.proyecto.ui.persona

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyecto.R
import com.proyecto.adapter.MedicamentoAdapter
import com.proyecto.adapter.PersonaAdapter
import com.proyecto.databinding.FragmentPersonaBinding
import com.proyecto.viewmodel.MedicamentoViewModel
import com.proyecto.viewmodel.PersonaViewModel

class PersonaFragment : Fragment() {


    private var _binding: FragmentPersonaBinding? = null
    private val binding get() = _binding!!
    private lateinit var personaViewModel: PersonaViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var personaViewModel =
            ViewModelProvider(this).get(PersonaViewModel::class.java)

        _binding = FragmentPersonaBinding.inflate(inflater, container, false)

        binding.addPersona.setOnClickListener {
            findNavController().navigate(R.id.action_nav_gallery_to_addPersonaFragment)
        }

        //Activar el RecyclerView
        val personaAdapter = PersonaAdapter()
        val reciclador = binding.reciclador
        reciclador.adapter = personaAdapter
        reciclador.layoutManager = LinearLayoutManager(requireContext())

        personaViewModel = ViewModelProvider(this)[PersonaViewModel::class.java]

        personaViewModel.getPersonas.observe(viewLifecycleOwner) { personas ->
            personaAdapter.setData(personas)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}