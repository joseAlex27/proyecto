package com.proyecto.repository

import androidx.lifecycle.MutableLiveData
import com.proyecto.model.Medicamento
import com.proyecto.data.MedicamentoDao

class MedicamentoRepository(private val medicamentoDao: MedicamentoDao) {

    val getMedicamentos : MutableLiveData<List<Medicamento>> = medicamentoDao.getMedicamentos()

    fun saveMedicamento(medicamento: Medicamento) {
        medicamentoDao.saveMedicamento(medicamento)
    }

    fun deleteMedicamento(medicamento: Medicamento) {
        medicamentoDao.deleteMedicamento(medicamento)
    }

}