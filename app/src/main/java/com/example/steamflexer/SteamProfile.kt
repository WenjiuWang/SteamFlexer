package com.example.steamflexer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import PlayerSummaries
import SteamLadderProfile
import android.content.Intent
import android.view.View
import kotlinx.android.synthetic.main.activity_steam_profile.*
import kotlinx.android.synthetic.main.profile_content.*


val steam_api_key = ""
val steam_ladder_api_key = ""
val steam_ladder_url = "https://steamladder.com/api/v1/profile/"
val steam_url = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2?key=ADA1791773FA7CC704941CB95212F441&steamids="

val game_badge = mapOf(
    1 to "One-Stop Shopper",
    5 to "Selected Collector",
    10 to  "Adept Accumulator",
    25 to  "Sharp-Eyed Stockpiler",
    50 to  "Collection Agent",
    100 to  "Power Player",
    250 to  "Game Mechanic",
    500 to  "Director of Acquisitions",
    1000 to  "Game Industry Guardian",
    2000 to  "Game God",
    3000 to  "Accrual Expert",
    4000 to  "Ambassador of Amassment",
    5000 to  "Digital Deity",
    6000 to  "Collection King",
    7000 to  "Magnate of Amassment",
    8000 to "Stockpiler Supreme",
    9000 to "Almighty Aggregator",
    10000 to "Master Gatherer",
    11000 to "Omnipotent Assembler",
    12000 to "Acquisition Idol",
    13000 to "Game Collector: ",
    14000 to "Game Collector: ",
    15000 to "Game Collector: ",
    16000 to "Game Collector: ",
    17000 to "Game Collector: ",
    18000 to "Game Collector: ",
    19000 to "Game Collector: ",
    20000 to "Game Collector: ",
    21000 to "Game Collector: ",
    22000 to "Game Collector: ",
    23000 to "Game Collector: ",
    24000 to "Game Collector: ",
    25000 to "Game Collector: "
    )



class SteamProfile : AppCompatActivity() {

    lateinit var steam_ladder_profile_info : SteamLadderProfile
    lateinit var player_summeries : PlayerSummaries

    override fun onCreate(savedInstanceState: Bundle?) {
        var cur_id_64 = intent.getStringExtra("steamID64")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steam_profile)
        after_load.isVisible = false
        //Read Steam Api Data
        val queue = Volley.newRequestQueue(this)
        val SteamRequest = object : JsonObjectRequest(Request.Method.GET, steam_url+cur_id_64, null,
            Response.Listener { response ->
                after_load.isVisible = true
                Log.d("SteamRequest Response", "Success")
                warning.isVisible = false
                player_summeries = Gson().fromJson(response.toString(), PlayerSummaries::class.java)
            },
            Response.ErrorListener { error ->
                warning.isVisible = true
                after_load.isVisible = false
                Log.d("SteamRequest Response", "Fail")
                Toast.makeText(this, "Invalid SteamID, No steam user info found", Toast.LENGTH_SHORT).show()
            }
        ) {}
        //Read Steam Ladder Api Data
        val SteamLadderRequest = object : JsonObjectRequest(Request.Method.GET, steam_ladder_url+cur_id_64, null,
            Response.Listener { response ->
                Log.d("SteamLadder Response", "Success")
                warning.isVisible = false
                after_load.isVisible = true
                steam_ladder_profile_info = Gson().fromJson(response.toString(), SteamLadderProfile::class.java)
                setLadderView()
            },
            Response.ErrorListener { error ->
                warning.isVisible = true
                after_load.isVisible = false
                Log.d("SteamLadder Response", "Fail")
                Toast.makeText(this, "Invalid SteamID, No steam user info found", Toast.LENGTH_SHORT).show()
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
        queue.add(SteamRequest)
        queue.add(SteamLadderRequest)

    }

    fun setLadderView() {
        //profile pic
        Glide.with(this)
            .load(steam_ladder_profile_info.steam_user.steam_avatar_src)
            .apply(RequestOptions().circleCrop())
            .into(avatar)

        //steam_id
        steam_id.text = steam_ladder_profile_info.steam_user.steam_name

        //country_flag and country
        if (!steam_ladder_profile_info.steam_user.is_steam_private) {
            Glide.with(this)
                .load("https://www.countryflags.io/" + steam_ladder_profile_info.steam_user.steam_country_code + "/flat/32.png")
                .into(flag)
        }
        //Profile Info
        if_public.text = if (steam_ladder_profile_info.steam_user.is_steam_private) "No" else "Yes"
        steam_since.text = if (steam_ladder_profile_info.steam_user.is_steam_private) "Private" else steam_ladder_profile_info.steam_user.steam_join_date.substring(0,10)
        steam_years.text = if (steam_ladder_profile_info.steam_user.is_steam_private) "Private" else (2019 -  steam_ladder_profile_info.steam_user.steam_join_date.substring(0,4).toInt()).toString()
        steam_friends.text = if (steam_ladder_profile_info.steam_user.is_steam_private) "Private" else steam_ladder_profile_info.steam_stats.friends.toString()
        steam_status.text = "Online"

        steam_years_badge.visibility = View.GONE
        if (!steam_ladder_profile_info.steam_user.is_steam_private) {
            steam_years_badge.visibility = View.VISIBLE
            Glide.with(this)
                .load("https://steamcommunity-a.akamaihd.net/public/images/badges/02_years/steamyears" + steam_years.text + "02_80.png")
                .into(steam_years_badge)
        }

        //Level Stat
        steam_level_xp.text = "Level " + steam_ladder_profile_info.steam_stats.level.toString() + " * XP " + steam_ladder_profile_info.steam_stats.xp.toString()
        badges.text = steam_ladder_profile_info.steam_stats.badges.total.toString()
        xp_world_rank.text = "#" + steam_ladder_profile_info.ladder_rank.worldwide_xp.toString()
        xp_region_rank.text = "#" + steam_ladder_profile_info.ladder_rank.region.region_xp.toString()
        xp_country_rank.text = "#" + steam_ladder_profile_info.ladder_rank.country.country_xp.toString()

        //Playtime Stats
        total_time.text = if (steam_ladder_profile_info.steam_user.is_steam_private || steam_ladder_profile_info.steam_stats.games.total_playtime_min == 0) "Private" else (steam_ladder_profile_info.steam_stats.games.total_playtime_min/ 60).toString() + " h"
        avg_time.text = if (steam_ladder_profile_info.steam_user.is_steam_private || steam_ladder_profile_info.steam_stats.games.total_playtime_min == 0) "Private" else (steam_ladder_profile_info.steam_stats.games.total_playtime_min / steam_ladder_profile_info.steam_stats.games.total_games).toString() + " min"
        time_world_rank.text = if (steam_ladder_profile_info.steam_user.is_steam_private) "Private" else "#" + steam_ladder_profile_info.ladder_rank.worldwide_playtime.toString()
        time_region_rank.text = if (steam_ladder_profile_info.steam_user.is_steam_private) "Private" else "#" + steam_ladder_profile_info.ladder_rank.region.region_playtime.toString()
        time_country_rank.text = if (steam_ladder_profile_info.steam_user.is_steam_private) "Private" else "#" + steam_ladder_profile_info.ladder_rank.country.country_playtime.toString()

        //Game Stats
        val pos = getGameBadge(steam_ladder_profile_info.steam_stats.games.total_games)
        steam_game_badge.visibility = View.GONE
        game_badge_title.visibility = View.GONE
        if(pos != 0) {
            steam_game_badge.visibility = View.VISIBLE
            game_badge_title.visibility = View.VISIBLE
            Glide.with(this)
                .load("https://steamcommunity-a.akamaihd.net/public/images/badges/13_gamecollector/" + pos.toString() + "_80.png")
                .into(steam_game_badge)
            game_badge_title.text = if (pos >= 13000) game_badge[pos] + pos.toString() + "+" else game_badge[pos]
        }

        steam_game_num.text = if (steam_ladder_profile_info.steam_stats.games.total_games == 0) "Private" else steam_ladder_profile_info.steam_stats.games.total_games.toString() +  " Games"
        game_world_rank.text = "#" + steam_ladder_profile_info.ladder_rank.worldwide_games.toString()
        game_region_rank.text = "#" + steam_ladder_profile_info.ladder_rank.region.region_games.toString()
        game_country_rank.text = "#" + steam_ladder_profile_info.ladder_rank.country.country_games.toString()
    }


    fun getGameBadge(num : Int) : Int {
        var result = 0
        for(cur in game_badge) {
            if(num >= cur.key) {
                result = cur.key
            } else {
                break
            }
        }
        return result
    }

}





