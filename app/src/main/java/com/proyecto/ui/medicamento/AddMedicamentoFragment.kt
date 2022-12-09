package com.proyecto.ui.medicamento

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
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
import androidx.core.content.ContextCompat.getSystemService
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
import java.util.*


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

        binding.etFechaCadu.setOnClickListener {
            showDatePickerDialog()
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

            createNotificationChannel()

            Toast.makeText(requireContext(),"Medicamento Agregado", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addMedicamentoFragment_to_nav_home)
        } else { //mensaje de error
            Toast.makeText(requireContext(),"Faltan datos!!", Toast.LENGTH_LONG).show()
        }
    }

    private fun schudeleNotification(year: Int,month: Int,day: Int) {
        val intent = Intent(requireContext().applicationContext, Notification::class.java)
        val title = "Caducidad del Medicamento"
        val message = "La fecha de caducidad del medicamento"+" " + binding.etNombre?.text.toString()
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext().applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManger = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime(year,month,day)
        alarmManger.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getDateFormat(requireContext())

        AlertDialog.Builder(requireContext())
            .setTitle("Notificación Programada")
            .setMessage(
                "Título: " + title +
                "\nMensaje: " + message +
                "es: " + dateFormat.format(date))
            .setPositiveButton("ok"){_,_ ->}
            .setIcon(com.google.android.material.R.drawable.ic_clock_black_24dp)
            .show()
    }

    private fun getTime(year: Int,month: Int,day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year,month,day)

        return calendar.timeInMillis
    }

    private fun createNotificationChannel() {
        val name = "Notification Channel"
        val desc = "A description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID,name,importance)
        channel.description= desc
        val notificationManager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment{ day, month, year -> onDateSelected(day,month,year) }
        datePicker.show(childFragmentManager, "datePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.etFechaCadu.setText("$day/${month+1}/$year")
        schudeleNotification(year,month,day)
    }


}