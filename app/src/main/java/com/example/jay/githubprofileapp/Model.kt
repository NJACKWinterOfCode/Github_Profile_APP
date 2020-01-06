package com.example.jay.githubprofileapp

object Model {

    data class Result(val login: String,
                      val bio:String,
                      val url: String,
                      val avatar_url: String,
                      val location: String,
                      val created_at: String,
                      val updated_at: String,
                      val public_repos: Int,
                      val followers: Int,
                      val following: Int,
                      val public_gists: Int
    ) {

        fun getUserFromTo(): String {
            val start = created_at.substring(0, created_at.indexOf('T'))
            val end = updated_at.substring(0, updated_at.indexOf('T'))
            return "from $start to $end ."
        }
    }

}

data class Repo(
    val full_name: String,
    val name: String,
    val fork: Boolean,
    val forks_count: Int,
    val language: String?,
    val size: Int,
    val watchers_count: Int,
    val stargazers_count: Int
)