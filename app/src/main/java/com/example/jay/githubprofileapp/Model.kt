package com.example.jay.githubprofileapp

object Model {

    data class Result(val login: String,
                      val bio:String,
                      val url:String)

}

data class Repo(
    val full_name: String,
    val name: String,
    val fork: Boolean,
    val forks_count: Int,
    val language: String,
    val size: Int,
    val watchers_count: Int,
    val stargazers_count: Int
)