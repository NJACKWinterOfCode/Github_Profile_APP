package com.example.jay.githubprofileapp

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nefrit.ratingview.model.Scale
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.acount_detail_result.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.repo_details.*
import kotlinx.android.synthetic.main.statisticsview.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private var repoList: List<Repo> = ArrayList()
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
        initWatcher()
    }

    private fun initWatcher() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                var toFilter: String? = edFilter.text.toString().trim()
                println("TAG = ${TAG} ::$toFilter::")
                if (toFilter.isNullOrBlank() || toFilter.isEmpty()) {
                    adapter.list = repoList
                } else {
                    println("toFilter = ${toFilter}")
                    val filtered =
                        repoList.filter { it.language != null }
                            .filter { it.language!!.contains(toFilter, true) }
                    adapter.list = filtered
                }
            }

        }
        edFilter.addTextChangedListener(watcher)

    }

    private fun beginSearch(searchString: String) {
        startLoading()
        //Thread.sleep(1500)
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
        repoDetails.visibility = View.GONE
    }

    private fun showDataLayout() {
        layoutProfile.visibility = View.VISIBLE
        repoDetails.visibility = View.VISIBLE
    }


    private fun hideIndicator() {
        spHome.visibility = View.GONE
    }

    private fun successLoading() {
        hideIndicator()
        showDataLayout()
        btnSearch.isEnabled = true

        //  super.onBackPressed() // to hide keyboard
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
                    }
                )
        } catch (e: Exception) {

            Log.d(TAG, "loadRepos ${e.message}")
        }
    }

    private fun fillRV(result: List<Repo>) {
        Log.d(TAG, "size ${result.size}")
        repoList = result
        adapter.list = repoList // notify rv with list of data
        makeRatingPerLang()
        makeRatingPerLangAndTotal()

    }

    private fun makeRatingPerLang() {
        val data = repoList.groupBy { it.language }
            .mapValues { it.value.size } // after group map size of repo into number
            .mapKeys { entry -> if (entry.key == null) "NA" else entry.key }
            .toList() // list with each item is (lang,count)
            .map { entry ->
                Scale(
                    entry.first,
                    entry.second,
                    Color.WHITE
                )
            } // map them to scale (name, count ,color)

        langRating.setScales(data)

    }

    private fun makeRatingPerLangAndTotal() {
        val data = repoList.groupBy { it.language }
            .mapValues { getSum(it.value) } // after group map size of repo into number
            .mapKeys { entry -> if (entry.key == null) "NA" else entry.key }
            .toList() // list with each item is (lang,count)
            .map { entry ->
                Scale(
                    entry.first,
                    entry.second,
                    Color.WHITE
                )
            } // map them to scale (name, count ,color)

        langRatingTotal.setScales(data)

    }

    private fun getSum(value: List<Repo>): Int {
        return value.sumBy { repo -> repo.size }
    }


    private fun setProfileData(result: Model.Result?) {
        if (result != null) {
            tvName.text = result.login
            tvBio.text = result.bio

            GlideApp.with(this)
                .load(result.avatar_url)
                .placeholder(R.drawable.ic_person)
                .into(imUser)


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

    private fun log(msg: String) {
        Log.d("MainActivity", msg)
    }
}