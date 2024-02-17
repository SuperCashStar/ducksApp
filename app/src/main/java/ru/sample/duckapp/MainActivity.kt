package ru.sample.duckapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api


class MainActivity : AppCompatActivity() {
    lateinit var imageView: ImageView;
    lateinit var editText: EditText;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById(R.id.editTextNumber)
        imageView = findViewById(R.id.imageView)
        imageView.setImageBitmap(BitmapFactory.decodeStream(assets.open("img.png")))
    }

    fun errorMessage() {
        Toast.makeText(applicationContext, "I can't find some \uD83E\uDD86 ...", Toast.LENGTH_SHORT).show()
    }

    fun fetchImageByCode(code: String) {
        Api.ducksApi.getDuckImageByCode(code).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())
                    imageView.setImageBitmap(bitmap)
                } else errorMessage()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    fun fetchRandomImage() {
        Api.ducksApi.getDuckImageRandom().enqueue(object : Callback<Duck> {
            override fun onResponse(call: Call<Duck>, response: Response<Duck>) {
                if (response.isSuccessful) {
                    Picasso.get().load(response.body()?.url).into(imageView)
                } else errorMessage()
            }

            override fun onFailure(call: Call<Duck>, t: Throwable) {}
        })
    }

    fun updateImage(num: String) {
        if (!num.isBlank()) {
            fetchImageByCode(num)
        } else {
            fetchRandomImage()
        }
    }

    fun onSendClick(view: View) {
        updateImage(editText.text.toString())
    }
}