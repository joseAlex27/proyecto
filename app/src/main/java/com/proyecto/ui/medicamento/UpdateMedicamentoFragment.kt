package com.proyecto.ui.medicamento

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lugares.utiles.ImagenUtiles
import com.proyecto.R
import com.proyecto.databinding.FragmentAddMedicamentoBinding
import com.proyecto.databinding.FragmentUpdateMedicamentoBinding
import com.proyecto.model.Medicamento
import com.proyecto.utiles.AudioUtiles
import com.proyecto.viewmodel.MedicamentoViewModel


class UpdateMedicamentoFragment : Fragment() {

    private var _binding: FragmentUpdateMedicamentoBinding? = null
    private val binding get() = _binding!!
    private lateinit var medicamentoViewModel: MedicamentoViewModel

    //Defino un objeto para obtener los argumentos
    private val args by navArgs<UpdateMedicamentoFragmentArgs>()

    private lateinit var mediaPlayer : MediaPlayer


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        medicamentoViewModel =
            ViewModelProvider(this)[MedicamentoViewModel::class.java]
        _binding = FragmentUpdateMedicamentoBinding.inflate(inflater,container,false)

        binding.etNombre.setText(args.medicamento.nombre)
        binding.etDescripcion.setText(args.medicamento.descripcion)
        binding.etUnidades.setText(args.medicamento.unidades)
        binding.etPrecio.setText(args.medicamento.precio.toString())
        binding.etFechaCadu.setText(args.medicamento.fechaCaducidad)


        binding.etFechaCadu.setOnClickListener {
            showDatePickerDialog()
        }
        binding.btAtualizarMedicamento.setOnClickListener{ updateMedicamento() }
        binding.btDeleteMedicamento.setOnClickListener() { deleteMedicamento() }
        //binding.btEmail.setOnClickListener() { escribirCorreo() }
        //binding.btPhone.setOnClickListener() { llamarLugar() }
        //binding.btWhatsapp.setOnClickListener() { mensajeWhatsApp() }
        //binding.btWeb.setOnClickListener() { verWeb() }
        //binding.btLocation.setOnClickListener() { verMapa() }

        if(args.medicamento.rutaAudio?.isNotEmpty() == true) {
            mediaPlayer= MediaPlayer()
            mediaPlayer.setDataSource(args.medicamento.rutaAudio)
            mediaPlayer.prepare()
            binding.btPlay.isEnabled = true
        } else {
            binding.btPlay.isEnabled = false
        }

        if(args.medicamento.rutaImagen?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(args.medicamento.rutaImagen)
                .fitCenter().into(binding.imagen)
        }

        binding.btPlay.setOnClickListener { mediaPlayer.start() }

        return binding.root
    }

    /*private fun verMapa() {
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val logitud = binding.tvLongitud.text.toString().toDouble()
        val altura = binding.tvAltura.text.toString().toDouble()

        if(latitud.isFinite() && logitud.isFinite()) { //puedo ver el lugar en el mapa

            val uri = Uri.parse("geo:$latitud,$logitud?z14")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.msg_data),
                Toast.LENGTH_LONG).show()
        }
    }*/

    /*private fun verWeb() {
        val para= binding.etWeb.text.toString()
        if(para.isNotEmpty()) { //puedo ver el sitio web

            val uri = Uri.parse("http://$para")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.msg_data),
                Toast.LENGTH_LONG).show()
        }
    }*/

    /*private fun mensajeWhatsApp() {
        val para = binding.etTelefono.text
        if(para.isNotEmpty()) { //puedo enviar el msj whatsapp
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp:://send?phone=506$para&text=" +
                    getString(R.string.msg_saludos)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(uri)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.msg_data),Toast.LENGTH_LONG).show()
        }
    }*/

    /*private fun llamarLugar() {
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
    }*/

    /*private fun escribirCorreo() {
        val para= binding.etCorreo.text.toString()
        if(para.isNotEmpty()) { //puedo enviar el correo
            val intent = Intent(Intent.ACTION_SEND)
            intent.type="message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(para))
            intent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.msg_saludos)+" "+binding.etNombre.text)

            intent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.msg_mensaje_correo))

            startActivity(intent)


        } else {
            Toast.makeText(requireContext(),
                getString(R.string.msg_data),
                Toast.LENGTH_LONG).show()

        }
    }*/

    private fun deleteMedicamento() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setPositiveButton(getString(R.string.msg_si)) {_,_ ->
            medicamentoViewModel.deleteMedicamento(args.medicamento)
            Toast.makeText(requireContext(),
                getString(R.string.msg_medicamento_deleted),
                Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateMedicamentoFragment2_to_nav_home)
        }
        builder.setNegativeButton(getString(R.string.msg_no)) {_,_ ->}
        builder.setTitle(getString(R.string.bt_deleteMedicamento))
        builder.setMessage(getString(R.string.msg_seguro_borrado)+" ${args.medicamento.nombre}")
        builder.create().show()
    }

    private fun updateMedicamento() {
        val nombre = binding.etNombre.text.toString()
        if(nombre.isNotEmpty()) {// si puedo agregar un medicamento
            val nombre = binding.etNombre.text.toString()
            val descripcion = binding.etDescripcion.text.toString()
            val unidades = binding.etUnidades.text.toString()
            val precio = binding.etPrecio.text.toString().toDouble()
            val fecha = binding.etFechaCadu.text.toString()

            val medicamento = Medicamento(
                args.medicamento.id,
                nombre,
                descripcion,
                unidades,
                precio,
                fecha,
                args.medicamento.rutaAudio,
                args.medicamento.rutaImagen)
            medicamentoViewModel.saveMedicamento(medicamento)

            Toast.makeText(requireContext(),getText(R.string.msg_medicamento_updated),Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateMedicamentoFragment2_to_nav_home)
        } else { //mensaje de error
            Toast.makeText(requireContext(),"Faltan datos!!",Toast.LENGTH_LONG).show()
        }
    }
    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment{ day, month, year -> onDateSelected(day,month,year) }
        datePicker.show(childFragmentManager, "datePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.etFechaCadu.setText("$day/${month+1}/$year")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}