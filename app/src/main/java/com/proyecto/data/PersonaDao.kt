package com.proyecto.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.proyecto.model.Medicamento
import com.proyecto.model.Persona

class PersonaDao {

    //Definiendo la jerarquia donde se gestionan contactos
    private val colecion1 = "proyectoApp"
    private val usuario = Firebase.auth.currentUser?.email.toString()
    private val coleccion2 = "misContactos"

    ///Conexion a la base de datos en la nube
    private  val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()

    init {
        //para conectarse a la DB
        firestore.firestoreSettings = FirebaseFirestoreSettings
            .Builder().build()
    }

    fun savePersona(persona: Persona) {
        //Un DocumentReference es un enlace a un documento en la nube  json en la nube
        val documento: DocumentReference
        if(persona.id.isEmpty()) { //Es un contacto nuevo... pues no tiene id
            documento = firestore
                .collection(colecion1).
                document(usuario).
                collection(coleccion2)
                .document()
            persona.id = documento.id
        } else { //el contacto tiene id existe en la nube
            documento = firestore
                .collection(colecion1)
                .document(usuario)
                .collection(coleccion2)
                .document(persona.id)
        }
        //ahora se inserta o actualiza el archivo
        documento.set(persona)
            .addOnSuccessListener {
                Log.d("saveContacto", "Contacto agregado o modificado")
            }
            .addOnCanceledListener {
                Log.e("saveContacto","Contacto no agregado o modificado")
            }
    }

    fun deletePersona(persona: Persona) {
        //se valida si el id del contacto tiene no está vacio... si es asi se borra
        if(persona.id.isNotEmpty()) {
            firestore
                .collection(colecion1)
                .document(usuario)
                .collection(coleccion2)
                .document(persona.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("deletePersona", "deletePersona Eliminado")
                }
                .addOnCanceledListener {
                    Log.e("deletePersona","deletePersona No Eliminado")
                }
        }
    }

    fun getPersonas() : MutableLiveData<List<Persona>> {
        //Lista para registrar la info de los contactos tomados de la coleccion
        val listaPersonas = MutableLiveData<List<Persona>>()

        firestore
            .collection(colecion1)
            .document(usuario)
            .collection(coleccion2)
            .addSnapshotListener {instantanea, error ->
                if(error != null) { //hubo un error en la recuperacion de los medicamentos del usuario
                    return@addSnapshotListener
                }
                if(instantanea != null){ // se logro recuperar la info
                    //y hay informacion
                    val lista = ArrayList<Persona>()
                    //Recorro la instantanea el json a Lugar
                    instantanea.documents.forEach{
                        val persona = it.toObject(Persona::class.java)
                        if(persona !=null) { //su se pudo convertir a un lugar
                            lista.add(persona)
                        }
                    }
                    listaPersonas.value = lista
                }
            }
        return listaPersonas
    }
}