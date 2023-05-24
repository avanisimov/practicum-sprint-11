package ru.yandex.practicum.sprint11

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


data class NewsResponse(
    val result: String,
    val data: Data
)

data class Data(
    val title: String,
    val items: List<NewsItem>
)


sealed class NewsItem {
    abstract val id: String
    abstract val title: String
    abstract val type: NewsItemType
    abstract val created: Date

    data class SportNewsItem(
        override val id: String,
        override val title: String,
        override val type: NewsItemType,
        override val created: Date,
        val specificPropertyForSport: String,
    ) : NewsItem()

    data class ScienceNewsItem(
        override val id: String,
        override val title: String,
        override val type: NewsItemType,
        override val created: Date,
        @SerializedName("specific_property_for_science")
        val specificPropertyForScience: String,
    ) : NewsItem()


    data class Unknown(
        override val id: String,
        override val title: String,
        override val type: NewsItemType,
        override val created: Date,
    ) : NewsItem()


}

enum class NewsItemType {
    @SerializedName("sport")
    SPORT,

    @SerializedName("science")
    SCIENCE
}


class CustomDateTypeAdapter : TypeAdapter<Date>() {

    // https://ru.wikipedia.org/wiki/ISO_8601
    companion object {
        const val FORMAT_PATTERN = "yyyy-MM-DD'T'hh:mm:ss:SSS"
    }

    private val formatter = SimpleDateFormat(FORMAT_PATTERN, Locale.getDefault())
    override fun write(out: JsonWriter, value: Date) {
        out.value(formatter.format(value))
    }

    override fun read(`in`: JsonReader): Date {
        return formatter.parse(`in`.nextString())
    }

}

class NewsItemTypeAdapter : JsonDeserializer<NewsItem> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): NewsItem {
        val type = context.deserialize<NewsItemType>(
            json.asJsonObject.get("type"),
            NewsItemType::class.java
        )
        return when (type) {
            NewsItemType.SPORT -> context.deserialize(json, NewsItem.SportNewsItem::class.java)
            NewsItemType.SCIENCE -> context.deserialize(json, NewsItem.ScienceNewsItem::class.java)
            else -> context.deserialize(json, NewsItem.Unknown::class.java)
        }
    }

}