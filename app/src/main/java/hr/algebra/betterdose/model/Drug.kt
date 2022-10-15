package hr.algebra.betterdose.model

import com.google.gson.annotations.SerializedName
import hr.algebra.betterdose.api.druglabel.DrugDiary
import hr.algebra.betterdose.api.druglabel.DrugVM
import hr.algebra.betterdose.api.druglabel.OpenFdaVM

data class Drug (
    @SerializedName("description") val description: String?,
    @SerializedName("dosage_and_administration") val dosage: String?,
    @SerializedName("purpose") val purpose: String?,
    @SerializedName("stop_use") val caution: String?,
    @SerializedName("openfda") val additionalData: OpenFDA,
    @SerializedName("medication") val medication: String?,
    @SerializedName("dosage_to_take") val dosage_to_take: Int?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("finish_date") val finishDate: String?,
    @SerializedName("start_time_day") val startTimeDay: String?,
    @SerializedName("times_per_day") val timesPerDay: Int?,
    @SerializedName("hourly_frequency") val hourlyFrequency: Int?
) {
    constructor() : this("", "", "", "", OpenFDA(), "", 1, "", "", "", 1, 1)
    fun getDrugDiary(): DrugDiary {
        return DrugDiary(description, dosage, purpose, caution, OpenFDA().getOpenFDAFromApiPkg(additionalData), medication, dosage_to_take, startDate, finishDate, startTimeDay, timesPerDay, hourlyFrequency)
    }
}

data class OpenFDA (
    @SerializedName("brand_name") val brandName: List<String>?,
    @SerializedName("manufacturer_name") val manufacturerName: List<String>?,
    @SerializedName("rxcui") val rxcui: List<String>?
) {
    constructor() : this(emptyList(), emptyList(), emptyList())
    fun getOpenFDAFromApiPkg(openFda: OpenFDA): OpenFdaVM {
        return OpenFdaVM(openFda.brandName, openFda.manufacturerName, openFda.rxcui)
    }
}

data class DrugFormatted (
    @SerializedName("description") val description: String?,
    @SerializedName("dosage_and_administration") val dosage: String?,
    @SerializedName("purpose") val purpose: String?,
    @SerializedName("stop_use") val caution: String?,
    @SerializedName("openfda") val additionalData: OpenFDA,
    @SerializedName("medication") val medication: String?,
    @SerializedName("dosage_to_take") val dosage_to_take: Int?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("finish_date") val finishDate: String?,
    @SerializedName("start_time_day") val startTimeDay: String?,
    @SerializedName("times_per_day") val timesPerDay: Int?,
    @SerializedName("hourly_frequency") val hourlyFrequency: Int?,
    @SerializedName("time_to_take_medicine") val timeToTakeMedicine: String?
) {
    constructor() : this("", "", "", "", OpenFDA(), "", 1, "", "", "", 1, 1, "")
    fun getDrugFormatted(): DrugFormatted {
        return DrugFormatted(description, dosage, purpose, caution, OpenFDA(), medication, dosage_to_take, startDate, finishDate, startTimeDay, timesPerDay, hourlyFrequency, timeToTakeMedicine)
    }
}

/*data class Drug (
    @SerializedName("description") val description: List<String>?,
    @SerializedName("dosage_and_administration") val dosage: List<String>?,
    @SerializedName("purpose") val purpose: List<String>?,
    @SerializedName("stop_use") val caution: List<String>?,
    @SerializedName("openfda") val additionalData: OpenFDA
) {
    constructor() : this(emptyList(), emptyList(), emptyList(), emptyList(), OpenFDA())
    fun getDrugVM(): DrugVM {
        return DrugVM(description, dosage, purpose, caution, OpenFDA().getOpenFDAFromApiPkg(additionalData))
    }
}*/
