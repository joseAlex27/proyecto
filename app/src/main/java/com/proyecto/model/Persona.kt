package com.proyecto.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Persona(
    var id: String,
    val nombre: String,
    val primerApellido : String,
    val segundoApellido : String,
    val telefono : String,
    val direccion : String,
    val rutaImagen: String?


): Parcelable {
    constructor() :
            this(
                "",
                "",
                "",
                "",
                "",
                "",
                "")
}