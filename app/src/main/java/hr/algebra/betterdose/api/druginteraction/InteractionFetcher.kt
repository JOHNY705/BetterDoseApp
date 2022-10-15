package hr.algebra.betterdose.api.druginteraction

import android.content.Context
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InteractionFetcher(private val context: Context) {
    private var drugInteractionApi: DrugInteractionApi
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        drugInteractionApi = retrofit.create(DrugInteractionApi::class.java)
    }

    fun fetchInteractions(rxcuis: String, onCompleteListener: ((DrugInteraction) -> Unit)) {
        val request = drugInteractionApi.fetchInteractions(rxcuis)

        request.enqueue(object: Callback<DrugInteraction> {
            override fun onResponse(call: Call<DrugInteraction>, response: Response<DrugInteraction>) {
                if (!response.isSuccessful) {
                    Log.e("INTERACTIONS", "code: " + response.code())
                    return
                }
                response.body()?.let {
                    onCompleteListener.invoke(it)
                }
            }

            override fun onFailure(call: Call<DrugInteraction>, t: Throwable) {
                Log.w(javaClass.name, t.message, t)
                Toast.makeText(context, "An error occurred while fetching interactions", Toast.LENGTH_LONG).show()
            }
        })
    }
}