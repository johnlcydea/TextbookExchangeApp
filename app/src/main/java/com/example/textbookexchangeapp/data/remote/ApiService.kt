package com.example.textbookexchangeapp.data.remote

    import com.example.textbookexchangeapp.data.local.Book
    import retrofit2.Response
    import retrofit2.http.GET

    interface ApiService {
        @GET("books") // Replace with your actual endpoint
        suspend fun getBooks(): Response<List<Book>>
    }