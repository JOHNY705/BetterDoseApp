package hr.algebra.betterdose.api.druglabel

import android.content.Context
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DrugFetcher(private val context: Context) {
    private var drugLabelApi: DrugLabelApi
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        drugLabelApi = retrofit.create(DrugLabelApi::class.java)
    }
    
    fun fetchDrugData(rxcui: String, onCompleteListener: ((DrugVM) -> Unit)) {
        val request = drugLabelApi.fetchDrugData(rxcui)

        request.enqueue(object: Callback<DrugLabelVM>{
            override fun onResponse(call: Call<DrugLabelVM>, response: Response<DrugLabelVM>) {
                if (!response.isSuccessful) {
                    Log.e("Drug fetching error", "code: " + response.code())
                    return
                }
                response.body()?.let {
                    onCompleteListener.invoke(it.data[0])
                }
            }

            override fun onFailure(call: Call<DrugLabelVM>, t: Throwable) {
                Log.w(javaClass.name, t.message, t)
                Toast.makeText(context, "An error occurred while fetching drug data", Toast.LENGTH_LONG).show()
            }
        })
    }
}