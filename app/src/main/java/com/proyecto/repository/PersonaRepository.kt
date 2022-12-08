package com.proyecto.repository

import androidx.lifecycle.MutableLiveData

import com.proyecto.model.Persona
import com.proyecto.data.PersonaDao


class PersonaRepository(private val personaDao: PersonaDao)  {

    val getPersonas : MutableLiveData<List<Persona>> = personaDao.getPersonas()

    fun savePersona(persona: Persona) {
        personaDao.savePersona(persona)
    }

    fun deletePersona(persona: Persona) {
        personaDao.deletePersona(persona)
    }
}