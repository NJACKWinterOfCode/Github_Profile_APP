package com.example.jay.githubprofileapp
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
    }

    private fun beginSearch(searchString: String) {
        startLoading()
        disposable = githubApiSevice.Check(searchString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    finishLoading()
                    setProfileData(result)
             //       loadRepos()
                },
                { error ->
                    edit_search.error = "Not Found!!"
                }
            )
    }

    private fun finishLoading() {
        spHome.visibility = View.GONE
        layoutProfile.visibility  = View.VISIBLE

    }

    private fun startLoading() {
        spHome.visibility = View.VISIBLE
        layoutProfile.visibility  = View.GONE
    }

    private fun loadRepos() {

        disposable = githubApiSevice.getRepos(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    finishLoading() },
                { error ->
                    edit_search.error = "Not Found!!"
                }
            )
    }

    private fun setProfileData(result: Model.Result?) {
        if (result != null){
            tvName.text = result.login
            tvBio.text = result.bio
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}