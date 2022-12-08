package com.proyecto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proyecto.databinding.PersonaFilaBinding
import com.proyecto.model.Persona
import com.proyecto.ui.persona.PersonaFragmentDirections


class PersonaAdapter: RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder>() {
    //Una lista para almacenar la informacion de contactos
    private var listaContactos= emptyList<Persona>()

    inner class PersonaViewHolder(private val itemBinding: PersonaFilaBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(persona: Persona) {
            itemBinding.tvNombre.text = persona.nombre
            itemBinding.tvPrimerApell.text = persona.primerApellido
            itemBinding.tvSegundoApell.text = persona.segundoApellido
            itemBinding.tvTelefono.text = persona.telefono
            itemBinding.tvDireccion.text = persona.direccion


            Glide.with(itemBinding.root.context)
                .load(persona.rutaImagen)
                .circleCrop()
                .into(itemBinding.imagen)

             itemBinding.vistaFila.setOnClickListener {
                val action = PersonaFragmentDirections.
                actionNavGalleryToUpdatePersonaFragment(persona)
                itemView.findNavController().navigate(action)
            }
        }
    }

    //Para crear una vista de cada fila de lugares
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaViewHolder {
        val itemBinding = PersonaFilaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,false)
        return PersonaViewHolder(itemBinding)
    }

    //Para dibujar la informacion de cada contacto
    override fun onBindViewHolder(holder: PersonaViewHolder, position: Int) {
        val personaActual = listaContactos[position]
        holder.bind(personaActual)
    }

    override fun getItemCount(): Int {
        return listaContactos.size

    }

    fun setData(personas: List<Persona>) {
        this.listaContactos = personas
        notifyDataSetChanged() //Provoca que se redibuje la lista
    }

}