package com.example.textbookexchangeapp.data.remote

        import com.google.gson.GsonBuilder
        import retrofit2.Retrofit
        import retrofit2.converter.gson.GsonConverterFactory
        import okhttp3.OkHttpClient
        import okhttp3.logging.HttpLoggingInterceptor
        import java.util.concurrent.TimeUnit

        object ApiClient {
            // Use a valid URL with http/https scheme
            private const val BASE_URL = "https://firestore.googleapis.com/v1/"

            // Create a logging interceptor to debug API calls
            private val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Create OkHttpClient with logging and timeouts
            private val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            // Create a lenient Gson instance that can handle malformed JSON
            private val gson = GsonBuilder()
                .setLenient()
                .create()

            // Create Retrofit instance with our custom configurations
            private val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            // Create API service
            val apiService: ApiService by lazy {
                retrofit.create(ApiService::class.java)
            }
        }