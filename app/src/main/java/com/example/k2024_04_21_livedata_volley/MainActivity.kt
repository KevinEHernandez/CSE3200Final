package com.example.k2024_04_21_livedata_volley

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.k2024_04_21_livedata_volley.databinding.ActivityMainBinding
import com.example.k2024_04_21_livedata_volley.models.JSON_MetMuseum
import com.example.k2024_04_21_livedata_volley.view_models.UrlViewModel
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val gson = Gson()
    private val metPublicDomainUrl = "https://collectionapi.metmuseum.org/public/collection/v1/objects/"
    private var imageData : JSON_MetMuseum? = null
    private lateinit var volleyQueue: RequestQueue


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        volleyQueue = Volley.newRequestQueue(this)

        val uriViewModel: UrlViewModel by viewModels<UrlViewModel>()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loadImageMetaDataButton.setOnClickListener{
            val nextIndex = uriViewModel.nextImageNumber()
            val metUrl = metPublicDomainUrl + nextIndex.toString()
            uriViewModel.setMetaDataUrl(metUrl)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                uriViewModel.getMetaDataUrl(),
                null,
                { response ->
                    imageData = gson.fromJson(response.toString(), JSON_MetMuseum::class.java )
                    uriViewModel.setImageUrl(imageData?.primaryImage.toString() ?: "Foobar")
                },
                { error ->  Log.i("PGB" ,"Error: ${error}") })
            volleyQueue.add(jsonObjectRequest)


        }

        binding.nextImageButton.setOnClickListener {
            val urlIMAGE = uriViewModel.getImageUrl().toString()
            val imageRequest = ImageRequest(
                urlIMAGE,
                { response: Bitmap ->
                    binding.imageView.setImageBitmap(response)
                },
                0,0,
                ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                { error ->  Log.i("PGB", "Error: ${error}" )})

            volleyQueue.add(imageRequest)
        }
    }
}