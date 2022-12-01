package com.proyecto.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.proyecto.data.MedicamentoDao
import com.proyecto.model.Medicamento
import com.proyecto.repository.MedicamentoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicamentoViewModel(application: Application) : AndroidViewModel(application) {

    val getMedicamentos : MutableLiveData<List<Medicamento>>

    private val repository: MedicamentoRepository = MedicamentoRepository(MedicamentoDao())

    init {
        getMedicamentos = repository.getMedicamentos
    }

    fun saveMedicamento (medicamento: Medicamento) {
        viewModelScope.launch(Dispatchers.IO) { repository.saveMedicamento(medicamento) }
    }

    fun deleteMedicamento (medicamento: Medicamento) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteMedicamento(medicamento) }
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}