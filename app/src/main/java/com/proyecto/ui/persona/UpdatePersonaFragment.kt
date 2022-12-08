package com.proyecto.ui.persona

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.proyecto.R
import com.proyecto.databinding.FragmentUpdatePersonaBinding
import com.proyecto.model.Persona
import com.proyecto.viewmodel.PersonaViewModel


class UpdatePersonaFragment : Fragment() {

    private var _binding: FragmentUpdatePersonaBinding? = null
    private val binding get() = _binding!!
    private lateinit var personaViewModel: PersonaViewModel


    //Defino un objeto para obtener los argumentos
    private val args by navArgs<UpdatePersonaFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        personaViewModel =
            ViewModelProvider(this).get(PersonaViewModel::class.java)
        _binding = FragmentUpdatePersonaBinding.inflate(inflater,container,false)


        binding.etNombre.setText(args.persona.nombre)
        binding.etPrimerApe.setText(args.persona.primerApellido)
        binding.etSegundoApell.setText(args.persona.segundoApellido)
        binding.etTelefono.setText(args.persona.telefono)
        binding.etDireccion.setText(args.persona.direccion)


        binding.btActualizarContacto.setOnClickListener{ updateContacto() }
        binding.btEliminarContacto.setOnClickListener() { deleteContacto() }
        binding.btPhone.setOnClickListener() { llamarContacto() }
        binding.btWhatsapp.setOnClickListener() { mensajeWhatsApp() }

        if(args.persona.rutaImagen?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(args.persona.rutaImagen)
                .fitCenter().into(binding.imagen)
        }

        return binding.root
    }

    private fun mensajeWhatsApp() {
        val para = binding.etTelefono.text.toString()
        if(para.isNotEmpty()){ //puedo enviar Mensaje WhatsApp
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp://send?phone506$para&text=" + getString(R.string.msg_saludos)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(uri)
            startActivity(intent)
        }
        else{
            Toast.makeText(requireContext(),getString(R.string.msg_data),Toast.LENGTH_LONG).show()
        }
    }

    private fun llamarContacto() {
        val para= binding.etTelefono.text.toString()
        if(para.isNotEmpty()) { //puedo Llamar
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$para")

            //Se solicitan los permisos para hacer la llamada
            if(requireActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
                //Se deben pedir los permisos
                requireActivity().
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 105)
            } else {
                requireActivity().startActivity(intent)
            }
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.msg_data),
                Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteContacto() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.msg_delete_lugar))
        builder.setMessage(getString(R.string.msg_seguro_borrado_contacto))
        builder.setNegativeButton(getString(R.string.msg_no)){_,_ ->}
        builder.setPositiveButton(getString(R.string.msg_si)){_,_ ->
            personaViewModel.deletePersona(args.persona)
            Toast.makeText(requireContext(),getString(R.string.msg_contacto_deleted),Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updatePersonaFragment_to_nav_gallery)
        }
        builder.show()
    }

    private fun updateContacto() {
        val nombre = binding.etNombre.text.toString()
        if(nombre.isNotEmpty()){//se puede agregar un contacto
            val primerApell = binding.etPrimerApe.text.toString()
            val segundoApe = binding.etSegundoApell.text.toString()
            val telefono = binding.etTelefono.text.toString()
            val direccion = binding.etDireccion.text.toString()

            val persona = Persona(args.persona.id,
                nombre,primerApell,segundoApe,telefono,
                direccion,args.persona.rutaImagen)
            personaViewModel.savePersona(persona)
            Toast.makeText(requireContext(),getText(R.string.msg_contacto_updated),Toast.LENGTH_SHORT)
            findNavController().navigate(R.id.action_updatePersonaFragment_to_nav_gallery)
        }
        else{//sino no se puede modificar el lugar


            Toast.makeText(requireContext(),getText(R.string.msg_data),Toast.LENGTH_LONG)

        }

    }









}