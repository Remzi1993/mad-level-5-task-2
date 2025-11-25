package info.remzi.madlevel5_task2.data.api

import info.remzi.madlevel5_task2.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MovieApi {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val tmdbToken: String by lazy {
        BuildConfig.TMDB_TOKEN.takeIf { it.isNotBlank() }
            ?: error("TMDB_TOKEN not set in BuildConfig, check apikey.properties")
    }

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $tmdbToken")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    val service: MovieApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)
    }
}