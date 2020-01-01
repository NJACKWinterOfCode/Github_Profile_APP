package com.example.jay.githubprofileapp
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
            }else{
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
                    val msg =  if (error.message!!.contains("404"))  "Not Found" else "Network Issue"
                    edit_search.error = msg
                    erroLoading()
                }
            )
    }

    private fun erroLoading(){
        btnSearch.isEnabled = true
        hideIndicator()
    }

    private fun hideIndicator() {
        spHome.visibility = View.GONE
    }

    private fun successLoading() {
        hideIndicator()

        layoutProfile.visibility  = View.VISIBLE
        rvRepos.visibility = View.VISIBLE
        btnSearch.isEnabled = true

    }

    private fun startLoading() {
        spHome.visibility = View.VISIBLE

        layoutProfile.visibility  = View.GONE
        rvRepos.visibility = View.GONE

        btnSearch.isEnabled = false
    }

    private fun loadRepos() {
        Log.d(TAG, "loadRepos")
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
        if (result != null){
            tvName.text = result.login
            tvBio.text = result.bio
            tvUrl.text = result.url
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}