package com.example.jay.githubprofileapp


import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApiSevice {

    @GET("users/{user}")
    fun Check(@Path("user") user: String): Observable<Model.Result>
    // fun Check(): Observable<Model.Result>

    companion object {
        fun create(): GithubApiSevice {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.github.com")
                .build()

            return retrofit.create(GithubApiSevice::class.java)
        }
    }

}