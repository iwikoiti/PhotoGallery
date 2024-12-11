package com.bignerdranch.android.photogallery.api

import retrofit2.Call
import retrofit2.http.GET

interface FlickrApi {
    @GET("/")
    fun fetchContents(): Call<String>
}