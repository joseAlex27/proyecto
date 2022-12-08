package com.proyecto.ui.persona

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lugares.utiles.ImagenUtiles
import com.proyecto.R
import com.proyecto.databinding.FragmentAddPersonaBinding
import com.proyecto.model.Persona
import com.proyecto.utiles.AudioUtiles
import com.proyecto.viewmodel.MedicamentoViewModel
import com.proyecto.viewmodel.PersonaViewModel


class AddPersonaFragment : Fragment() {

    private var _binding: FragmentAddPersonaBinding? = null
    private val binding get() = _binding!!
    private lateinit var personaViewModel: PersonaViewModel

    //Para grabar nota de audio
    private lateinit var audioUtiles: AudioUtiles

    //Para capturar la imagen del lugar
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>
    private lateinit var imagenUtiles: ImagenUtiles


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        personaViewModel =
            ViewModelProvider(this)[PersonaViewModel::class.java]
        _binding = FragmentAddPersonaBinding.inflate(inflater,container,false)

        binding.btAgregar.setOnClickListener{
            binding.progressBar.visibility = ProgressBar.VISIBLE
            binding.msgMensaje.text = "Subiendo Contacto"
            subeImagen()
        }


        //Se inincializa el activity para tomar la foto
        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                imagenUtiles.actualizaFoto()
            }
        }

        //Se inicializa el objeto para gestionar foto de la persona
        imagenUtiles = ImagenUtiles(
            requireContext(),
            binding.btPhoto,
            binding.btRotaL,
            binding.btRotaR,
            binding.imagen,
            tomarFotoActivity)

        return binding.root
    }

    private fun subeImagen() {
        binding.msgMensaje.text = getString(R.string.msg_subiendo_imagen)
        val imagenFile = imagenUtiles.imagenFile
        if(imagenFile.exists() && imagenFile.isFile && imagenFile.canRead()) {
            val rutaLocal = Uri.fromFile(imagenFile)
            val rutaNube = "contactos/${Firebase.auth.currentUser?.email}/imagenes/${imagenFile.name}"
            val referencia : StorageReference = Firebase.storage.reference.child(rutaNube)

            referencia.putFile(rutaLocal)
                .addOnSuccessListener {
                    referencia.downloadUrl.addOnSuccessListener {
                        val rutaPublicaImagen = it.toString()
                        subeContacto(rutaPublicaImagen)
                    }
                }
                .addOnCanceledListener {
                    subeContacto("")
                }
        } else {
            subeContacto("")
        }

    }

    private fun subeContacto(rutaPublicaImagen: String) {
        binding.msgMensaje.text = "Subiendo Lugar..."
        val nombre = binding.etNombre.text.toString()
        val primerApe = binding.etPrimerApe.text.toString()
        val segundoApe = binding.etSegundoApell.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val direccion = binding.etDireccion.text.toString()

        if(nombre.isNotEmpty()) {// si puedo agregar un contacto
            val persona = Persona("", nombre,primerApe,segundoApe,
                telefono,direccion,rutaPublicaImagen)
            personaViewModel.savePersona(persona)
            Toast.makeText(requireContext(),"Contacto Agregado", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addPersonaFragment_to_nav_gallery)
        } else { //mensaje de error
            Toast.makeText(requireContext(),"Faltan datos!!", Toast.LENGTH_LONG).show()
        }
    }






}