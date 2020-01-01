package com.example.jay.githubprofileapp

object Model {

    data class Result(val login: String,
                      val bio:String,
                      val url:String)

}

data class Repo (val full_name:String)