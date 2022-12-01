package com.proyecto.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Medicamento(
    var id: String,
    val nombre: String,
    val descripcion: String,
    val unidades: String?,
    val precio: Double?,
    val fechaCaducidad: String?,
    val rutaAudio: String?,
    val rutaImagen: String?


): Parcelable {
    constructor() :
            this("",
                "",
                "",
                "",
                0.0,
                "",
                "",
                "")
}
