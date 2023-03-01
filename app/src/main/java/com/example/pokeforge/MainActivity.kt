package com.example.pokeforge

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pokeforge.APIClient.client
import com.example.pokeforge.databinding.ActivityMainBinding
import com.example.pokeforge.pojo.MultipleResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val call: Call<MultipleResource?>? = client!!.create(APIInterface::class.java).doGetListResources(1)
        if (call != null) {
            call.enqueue(object : Callback<MultipleResource?> {
                override fun onResponse(call: Call<MultipleResource?>?, response: Response<MultipleResource?>) {
                    Log.d("TAG", response.code().toString() + "")
                    var displayResponse = ""
                    val resource: MultipleResource? = response.body()
                    val res = resource?.name
                    displayResponse += """${res}"""
                    //loop in results
                    //for (i in res!!.indices) {
                        //val text = res[i].name
                        //val url = res[i].url
                        //displayResponse += """${text} ${url}"""

                    //}
                    Log.d("TAG", response.code().toString() + "")
                    Log.d("TAG", "onResponse: $displayResponse")
                    //displayResponse += """${text.toString()} ${url.toString()}
                    //"""

                }

                override fun onFailure(call: Call<MultipleResource?>, t: Throwable?) {
                    call.cancel()
                }
            })
        }
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }
}