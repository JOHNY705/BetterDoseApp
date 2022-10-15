package hr.algebra.betterdose.api.druglabel

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

const val API_URL = "https://api.fda.gov/drug/"
interface DrugLabelApi {
    //?search=rxcui={rxcui}
    @GET("label.json")
    fun fetchDrugData(@Query(value = "search", encoded = true) rxcui: String): Call<DrugLabelVM>
}