package com.example.jay.githubprofileapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.acount_detail_result.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var disposable: Disposable? = null

    private var name = ""
    val adapter = RepoAdapter()

    private val githubApiSevice by lazy {
        GithubApiSevice.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSearch.setOnClickListener {
            if (edit_search.text.toString().isNotEmpty()) {
                name = edit_search.text.toString()
                beginSearch(name)
            } else {
                edit_search.setError("Please enter valid name")
            }
        }

        rvRepos.adapter = adapter
    }

    private fun beginSearch(searchString: String) {
        startLoading()
        Thread.sleep(1500)
        disposable = githubApiSevice.Check(searchString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    successLoading()
                    setProfileData(result)
                    loadRepos()
                },
                { error ->
                    val msg = if (error.message!!.contains("404")) "Not Found" else "Network Issue"
                    edit_search.error = msg
                    erroLoading()
                }
            )
    }

    private fun erroLoading() {
        btnSearch.isEnabled = true
        hideIndicator()
        hideDataLayout()
    }

    private fun hideDataLayout() {
        layoutProfile.visibility = View.GONE
        rvRepos.visibility = View.GONE
    }

    private fun showDataLayout() {
        layoutProfile.visibility = View.VISIBLE
        rvRepos.visibility = View.VISIBLE
    }


    private fun hideIndicator() {
        spHome.visibility = View.GONE
    }

    private fun successLoading() {
        hideIndicator()
        showDataLayout()
        btnSearch.isEnabled = true

    }

    private fun startLoading() {
        spHome.visibility = View.VISIBLE

        hideDataLayout()
        btnSearch.isEnabled = false
    }

    private fun loadRepos() {
        try {
            disposable = githubApiSevice.getRepos(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        // successLoading()
                        fillRV(result)
                    },
                    { error ->
                        val msg =
                            if (error.message!!.contains("404")) "Not Found" else "Network Issue"
                        edit_search.error = msg
                        // erroLoading()
                    }
                )
        } catch (e: Exception) {

            Log.d(TAG, "loadRepos ${e.message}")
        }
    }

    private fun fillRV(result: List<Repo>) {
        Log.d(TAG, "size ${result.size}")
        adapter.list = result

    }

    private fun setProfileData(result: Model.Result?) {
        if (result != null) {
            tvName.text = result.login
            tvBio.text = result.bio
            Glide.with(this).load(result.avatar_url).into(imUser)

            tvCountOfFollower.text = result.followers.toString()
            tvCountOfFollowing.text = result.following.toString()
            tvLocation.text = result.location
            tvPublicGists.text = result.public_gists.toString()
            tvPublicRepos.text = result.public_repos.toString()
            tvUserFromTo.text = result.getUserFromTo()
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}