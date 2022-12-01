package com.proyecto.ui.medicamento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyecto.R
import com.proyecto.adapter.MedicamentoAdapter
import com.proyecto.databinding.FragmentMedicamentoBinding
import com.proyecto.viewmodel.MedicamentoViewModel

class MedicamentoFragment : Fragment() {

    private var _binding: FragmentMedicamentoBinding? = null
    private val binding get() = _binding!!
    private lateinit var medicamentoViewModel: MedicamentoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var medicamentoViewModel =
            ViewModelProvider(this).get(MedicamentoViewModel::class.java)
        _binding = FragmentMedicamentoBinding.inflate(inflater, container, false)

        binding.addMedicamento.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_addMedicamentoFragment)
        }

        //Activar el RecyclerView
        val medicamentoAdapter = MedicamentoAdapter()
        val reciclador = binding.reciclador
        reciclador.adapter = medicamentoAdapter
        reciclador.layoutManager = LinearLayoutManager(requireContext())

        medicamentoViewModel = ViewModelProvider(this)[MedicamentoViewModel::class.java]

        medicamentoViewModel.getMedicamentos.observe(viewLifecycleOwner) { medicamentos ->
            medicamentoAdapter.setData(medicamentos)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}