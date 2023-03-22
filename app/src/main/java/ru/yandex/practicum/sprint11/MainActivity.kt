package ru.yandex.practicum.sprint11

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SPRINT_11"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/avanisimov/practicum-sprint-11/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
                        .create()
                )
            )
            .build()
        val serverApi = retrofit.create(Sprint11ServerApi::class.java)

        serverApi.getNews1().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                Log.d(TAG, "onResponse: ${response.body()}")
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
            }

        })
    }
}

// https://raw.githubusercontent.com/avanisimov/practicum-sprint-11/main/jsons/news_1.json

interface Sprint11ServerApi {


    @GET("main/jsons/news_1.json")
    fun getNews1(): Call<NewsResponse>
}


data class NewsResponse(
    val result: String,
    val data: NewsData,
)

data class NewsData(
    val title: String,
    val items: List<NewsItem>
)

data class NewsItem(
    val id: Int,
    val title: String,
    val type: String,
    val created: Date,
)


class CustomDateTypeAdapter : JsonDeserializer<Date>, JsonSerializer<Date> {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss:SSS")
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Date {
        val dateString = json.asString
        // 2023-01-01T11:12:13:123
        // https://ru.wikipedia.org/wiki/ISO_8601

        val date = sdf.parse(dateString)
        return date
    }

    override fun serialize(
        src: Date,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val string = sdf.format(src)
        return JsonPrimitive(string)
    }

}