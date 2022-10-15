package hr.algebra.betterdose.api.druginteraction

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

const val API_URL = "https://rxnav.nlm.nih.gov/REST/interaction/"
interface DrugInteractionApi {
    @GET("list.json")
    fun fetchInteractions(@Query(value = "rxcuis", encoded = true) rxcuis: String): Call<DrugInteraction>
}