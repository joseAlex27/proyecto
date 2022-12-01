package com.proyecto.ui.medicamento

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lugares.utiles.ImagenUtiles
import com.proyecto.R
import com.proyecto.databinding.FragmentAddMedicamentoBinding
import com.proyecto.databinding.FragmentMedicamentoBinding
import com.proyecto.model.Medicamento
import com.proyecto.utiles.AudioUtiles
import com.proyecto.viewmodel.MedicamentoViewModel


class AddMedicamentoFragment : Fragment() {

    private var _binding: FragmentAddMedicamentoBinding? = null
    private val binding get() = _binding!!
    private lateinit var medicamentoViewModel: MedicamentoViewModel

    //Para grabar nota de audio
    private lateinit var audioUtiles: AudioUtiles

    //Para capturar la imagen del lugar
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>
    private lateinit var imagenUtiles: ImagenUtiles



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        medicamentoViewModel =
            ViewModelProvider(this)[MedicamentoViewModel::class.java]
        _binding = FragmentAddMedicamentoBinding.inflate(inflater,container,false)
        binding.btAgregar.setOnClickListener{
            binding.progressBar.visibility = ProgressBar.VISIBLE
            binding.msgMensaje.text = "Subiendo Nota Audio"
            subeAudio()
        }

        //Se inicializa el objeto audioutiles
        audioUtiles = AudioUtiles(
            requireActivity(),
            requireContext(),
            binding.btAccion,
            binding.btPlay,
            binding.btDelete,
            getString(R.string.msg_graba_audio),
            getString(R.string.msg_detener_audio)
        )

        //Se inincializa el activity para tomar la foto
        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                imagenUtiles.actualizaFoto()
            }
        }

        //Se inicializa el objeto para gestionar foto del lugar
        imagenUtiles = ImagenUtiles(
            requireContext(),
            binding.btPhoto,
            binding.btRotaL,
            binding.btRotaR,
            binding.imagen,
            tomarFotoActivity)

        return binding.root

    }

    private fun subeAudio() {
        val audioFile = audioUtiles.audioFile
        if(audioFile.exists() && audioFile.isFile && audioFile.canRead()) {
            val rutaLocal = Uri.fromFile(audioFile)
            val rutaNube = "medicamentos/${Firebase.auth.currentUser?.email}/audios/${audioFile.name}"
            val referencia : StorageReference = Firebase.storage.reference.child(rutaNube)

            referencia.putFile(rutaLocal).addOnSuccessListener {
                referencia.downloadUrl.addOnSuccessListener {
                    val rutaPublicaAudio = it.toString()
                    subeImagen(rutaPublicaAudio)
                }
            }
                .addOnCanceledListener {
                    subeImagen("")
                }
        } else {
            subeImagen("")
        }
    }

    private fun subeImagen(rutaPublicaAudio: String) {
        binding.msgMensaje.text = getString(R.string.msg_subiendo_audio)
        val imagenFile = imagenUtiles.imagenFile
        if(imagenFile.exists() && imagenFile.isFile && imagenFile.canRead()) {
            val rutaLocal = Uri.fromFile(imagenFile)
            val rutaNube = "medicamentos/${Firebase.auth.currentUser?.email}/imagenes/${imagenFile.name}"
            val referencia : StorageReference = Firebase.storage.reference.child(rutaNube)

            referencia.putFile(rutaLocal)
                .addOnSuccessListener {
                    referencia.downloadUrl.addOnSuccessListener {
                        val rutaPublicaImagen = it.toString()
                        subeLugar(rutaPublicaAudio, rutaPublicaImagen)
                    }
                }
                .addOnCanceledListener {
                    subeLugar(rutaPublicaAudio,"")
                }
        } else {
            subeLugar(rutaPublicaAudio,"")
        }

    }


    private fun subeLugar(rutaPublicaAudio: String, rutaPublicaImagen: String) {
        binding.msgMensaje.text = "Subiendo Lugar..."
        val nombre = binding.etNombre.text.toString()
        val descripcion = binding.etDescripcion.text.toString()
        val unidades = binding.etUnidades.text.toString()
        val precio = binding.etPrecio.text.toString().toDouble()
        val fecha = binding.etFechaCadu.text.toString()


        if(nombre.isNotEmpty()) {// si puedo agregar un lugar
            val medicamento = Medicamento("", nombre,descripcion,unidades,
                precio,fecha,rutaPublicaAudio,rutaPublicaImagen)
            medicamentoViewModel.saveMedicamento(medicamento)
            Toast.makeText(requireContext(),"Medicamento Agregado", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addMedicamentoFragment_to_nav_home)
        } else { //mensaje de error
            Toast.makeText(requireContext(),"Faltan datos!!", Toast.LENGTH_LONG).show()
        }
    }


}