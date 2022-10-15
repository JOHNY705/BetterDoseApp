package hr.algebra.betterdose.model

import com.google.gson.annotations.SerializedName

data class Allergy (
    var allergy : String = "",
    var id : String = ""
    ) {
    constructor() : this("")
}