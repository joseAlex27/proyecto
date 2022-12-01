package com.proyecto.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.proyecto.model.Medicamento

class MedicamentoDao {

    //Definiendo la jerarquia donde se gestionan lugares
    private val colecion1 = "proyectoApp"
    private val usuario = Firebase.auth.currentUser?.email.toString()
    private val coleccion2 = "misMedicamentos"

    ///Conexion a la base de datos en la nube
    private  val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()

    init {
        //para conectarse a la DB
        firestore.firestoreSettings = FirebaseFirestoreSettings
            .Builder().build()
    }

    fun saveMedicamento(medicamento: Medicamento) {
        //Un DocumentReference es un enlace a un documento en la nube  json en la nube
        val documento: DocumentReference
        if(medicamento.id.isEmpty()) { //Es un medicamento nuevo... pues no tiene id
            documento = firestore
                .collection(colecion1).
                document(usuario).
                collection(coleccion2)
                .document()
                medicamento.id = documento.id
        } else { //el lugar tiene id existe en la nube
            documento = firestore
                .collection(colecion1)
                .document(usuario)
                .collection(coleccion2)
                .document(medicamento.id)
        }
        //ahora se inserta o actualiza el archivo
        documento.set(medicamento)
            .addOnSuccessListener {
                Log.d("saveMedicamento", "Medicamento agregado o modificado")
            }
            .addOnCanceledListener {
                Log.e("saveMedicamento","Medicamento no agregado o modificado")
            }
    }

    fun deleteMedicamento(medicamento: Medicamento) {
        //se valida si el id del medicamento tiene no está vacio... si es asi se borra
        if(medicamento.id.isNotEmpty()) {
            firestore
                .collection(colecion1)
                .document(usuario)
                .collection(coleccion2)
                .document(medicamento.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("deleteMedicamento", "deleteMedicamento Eliminado")
                }
                .addOnCanceledListener {
                    Log.e("deleteMedicamento","deleteMedicamento No Eliminado")
                }
        }
    }

    fun getMedicamentos() : MutableLiveData<List<Medicamento>> {
        //Lista para registrar la info de los medicamentos tomados de la coleccion
        val listaMedicamentos = MutableLiveData<List<Medicamento>>()

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
                    val lista = ArrayList<Medicamento>()
                    //Recorro la instantanea el json a Lugar
                    instantanea.documents.forEach{
                        val medicamento = it.toObject(Medicamento::class.java)
                        if(medicamento !=null) { //su se pudo convertir a un lugar
                            lista.add(medicamento)
                        }
                    }
                    listaMedicamentos.value = lista
                }
            }
        return listaMedicamentos
    }





}