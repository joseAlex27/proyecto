package com.proyecto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proyecto.databinding.MedicamentoFilaBinding
import com.proyecto.model.Medicamento
import com.proyecto.ui.medicamento.MedicamentoFragmentDirections

class MedicamentoAdapter : RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder>() {
    //Una lista para almacenar la informacion de lugares
    private var listaMedicamentos= emptyList<Medicamento>()

    inner class MedicamentoViewHolder(private val itemBinding: MedicamentoFilaBinding) :
        RecyclerView.ViewHolder(itemBinding.root){
        fun bind(medicamento: Medicamento) {
            itemBinding.tvNombre.text = medicamento.nombre
            itemBinding.tvDescripcion.text = medicamento.descripcion
            itemBinding.tvUnidades.text = medicamento.unidades
            itemBinding.tvPrecio.text = medicamento.precio.toString()
            itemBinding.tvFechaCaduc.text = medicamento.fechaCaducidad


            Glide.with(itemBinding.root.context)
                .load(medicamento.rutaImagen)
                .centerInside()
                .into(itemBinding.imagen)

           /* itemBinding.vistaFila.setOnClickListener{
                val action = MedicamentoFragmentDirections.
                actionNavLugarToUpdateLugarFragment2(lugar)
                itemView.findNavController().navigate(action)
            }*/
        }
    }

    //Para crear una vista de cada fila de lugares
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicamentoViewHolder {
        val itemBinding = MedicamentoFilaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,false)
        return MedicamentoViewHolder(itemBinding)
    }

    //Para dibujar la informacion de cada lugar
    override fun onBindViewHolder(holder: MedicamentoViewHolder, position: Int) {
        val medicamentoActual = listaMedicamentos[position]
        holder.bind(medicamentoActual)
    }

    override fun getItemCount(): Int {
        return listaMedicamentos.size

    }

    fun setData(medicamentos: List<Medicamento>) {
        this.listaMedicamentos = medicamentos
        notifyDataSetChanged() //Provoca que se redibuje la lista
    }
}