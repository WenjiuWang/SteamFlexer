package com.example.steamflexer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import SteamLadder
import com.example.steamflexer.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_steam_profile.*
import java.io.File
import android.R.attr.data
import android.content.Intent
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.steamflexer.SteamLadder.SteamLadderAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_ladder.*
import kotlinx.android.synthetic.main.ladder_item.*
import java.io.OutputStreamWriter


val category_array = arrayOf("Highest Level","Most Games","Most Playtime","Most Badges","Longest on Steam","Most VAC Bans","Most Game Bans")
val country_array = arrayOf("Worldwide","Europe","North America","South America","Asia","Africa","Oceania","Antarctica")

val type_map = mapOf(
    "Highest Level" to "xp",
    "Most Games" to "games",
    "Most Playtime" to "playtime",
    "Most Badges" to "badges",
    "Longest on Steam" to "steam_age",
    "Most VAC Bans" to "vac",
    "Most Game Bans" to "game_ban"
)

val category_map = mapOf(
    "xp" to "Level",
    "games" to "Games",
    "playtime" to "Playtime(H)",
    "badges" to "Badges",
    "steam_age" to "Steam Since",
    "vac" to "VAC Bans",
    "game_ban" to "Game Bans"
)

val country_map = mapOf(
    "Worldwide" to "",
    "Europe" to "europe",
    "North America" to "north_america",
    "South America" to "south_america",
    "Asia" to "asia",
    "Africa" to "africa",
    "Oceania" to "oceania",
    "Antarctica" to "antarctica"
)

var cur_type = ""

class LadderActivity : AppCompatActivity() {

    lateinit var steam_ladder_info : SteamLadder

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ladder)

        var title =  intent.getStringExtra("type") + " Ladder"
        var country = country_map[intent.getStringExtra("country")]
        cur_type = type_map[intent.getStringExtra("type")].toString()
        val ladder_url  = "https://steamladder.com/api/v1/ladder/" +  cur_type + "/" + country

        val category_adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, category_array)
        category_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        select_category.adapter = category_adapter

        val country_adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, country_array)
        country_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        select_country.adapter = country_adapter

        select_category.setSelection(category_adapter.getPosition(intent.getStringExtra("type")))
        select_country.setSelection(country_adapter.getPosition(intent.getStringExtra("country")))

        val queue = Volley.newRequestQueue(this)
        val SteamLadderRequest = object : JsonObjectRequest(
            Request.Method.GET, ladder_url, null,
            Response.Listener { response ->
                Log.d("SteamLadder Response", "Success")

                steam_ladder_info = Gson().fromJson(response.toString(), SteamLadder::class.java)
                ladder_title.text = title
                category.text = category_map[cur_type]

                recycler_view.layoutManager = LinearLayoutManager(this)
                val adapter = SteamLadderAdapter(steam_ladder_info.ladder)
                recycler_view.adapter = adapter
            },
            Response.ErrorListener { error ->
                Log.d("SteamLadder Response", "Fail")
                Toast.makeText(this, "Ladder request fail", Toast.LENGTH_SHORT).show()
            }
        ) {
            // Providing Request Headers
            override fun getHeaders(): Map<String, String> {
                // Create HashMap of your Headers as the example provided below
                val headers = HashMap<String, String>()
                headers["Authorization"] = steam_ladder_api_key
                return headers
            }
        }
        queue.add(SteamLadderRequest)

        reload_button.setOnClickListener() {
            val refresh = Intent(this, LadderActivity::class.java)
            refresh.putExtra("country", select_country.selectedItem.toString())
            refresh.putExtra("type", select_category.selectedItem.toString())
            finish()
            startActivity(refresh)

        }




    }


}
