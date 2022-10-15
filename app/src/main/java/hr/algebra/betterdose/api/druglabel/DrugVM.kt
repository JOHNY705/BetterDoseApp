package hr.algebra.betterdose.api.druglabel

import com.google.gson.annotations.SerializedName

data class DrugLabelVM (
    @SerializedName("meta") val meta: Any,
    @SerializedName("results") val data: List<DrugVM>
)

data class DrugVM (
    @SerializedName("description") val description: List<String>?,
    @SerializedName("dosage_and_administration") val dosage: List<String>?,
    @SerializedName("purpose") val purpose: List<String>?,
    @SerializedName("stop_use") val caution: List<String>?,
    @SerializedName("openfda") val additionalData: OpenFdaVM
)

data class OpenFdaVM (
    @SerializedName("brand_name") val brandName: List<String>?,
    @SerializedName("manufacturer_name") val manufacturerName: List<String>?,
    @SerializedName("rxcui") val rxcui: List<String>?
)

data class DrugDiary (
    @SerializedName("description") val description: String?,
    @SerializedName("dosage_and_administration") val dosage: String?,
    @SerializedName("purpose") val purpose: String?,
    @SerializedName("stop_use") val caution: String?,
    @SerializedName("openfda") val additionalData: OpenFdaVM,
    @SerializedName("medication") val medication: String?,
    @SerializedName("dosage_to_take") val dosage_to_take: Int?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("finish_date") val finishDate: String?,
    @SerializedName("start_time_day") val startTimeDay: String?,
    @SerializedName("times_per_day") val timesPerDay: Int?,
    @SerializedName("hourly_frequency") val hourlyFrequency: Int?
        )
