package com.proyecto.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.proyecto.data.PersonaDao
import com.proyecto.model.Persona
import com.proyecto.repository.PersonaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonaViewModel(application: Application) : AndroidViewModel(application) {

    val getPersonas : MutableLiveData<List<Persona>>

    private val repository: PersonaRepository = PersonaRepository(PersonaDao())

    init {
        getPersonas = repository.getPersonas
    }

    fun savePersona(persona: Persona) {
        viewModelScope.launch(Dispatchers.IO) { repository.savePersona(persona) }
    }

    fun deletePersona(persona: Persona) {
        viewModelScope.launch(Dispatchers.IO) { repository.deletePersona(persona) }
    }

}