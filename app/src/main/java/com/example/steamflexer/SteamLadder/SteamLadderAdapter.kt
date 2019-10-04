package com.example.steamflexer.SteamLadder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import com.example.steamflexer.R
import kotlinx.android.synthetic.main.ladder_item.view.*
import Ladder
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.steamflexer.SteamProfile
import com.example.steamflexer.cur_type
import com.example.steamflexer.type_map
import kotlinx.android.synthetic.main.ladder_item.*

class SteamLadderAdapter(private val ladder: List<Ladder>) : RecyclerView.Adapter<SteamLadderAdapter.ViewHolder>() {

    class ViewHolder(ladderView: View) : RecyclerView.ViewHolder(ladderView) {
        val user_name = ladderView.user_name
        val user_avatar = ladderView.user_avatar
        val user_num = ladderView.user_num
        val user_content = ladderView.user_content
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SteamLadderAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ladder_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SteamLadderAdapter.ViewHolder, position: Int) {
        Glide
            .with(holder.itemView.context)
            .load(ladder[position].steam_user.steam_avatar_src)
            .apply(RequestOptions().circleCrop())
            .into(holder.user_avatar)
        holder.user_name.text = ladder[position].steam_user.steam_name
        holder.user_num.text = (position + 1).toString()
        holder.user_content.text =
            when(cur_type){
                "xp" -> ladder[position].steam_stats.level.toString()
                "games" -> ladder[position].steam_stats.games.total_games.toString()
                "playtime" -> (ladder[position].steam_stats.games.total_playtime_min / 60).toString()
                "badges" -> ladder[position].steam_stats.badges.total.toString()
                "steam_age" -> ladder[position].steam_user.steam_join_date
                "vac" -> ladder[position].steam_stats.bans.vac_bans.toString()
                else -> ladder[position].steam_stats.bans.game_bans.toString()
        }


        holder.user_name.setOnClickListener() {
            val intent = Intent(holder.itemView.context,SteamProfile::class.java)
            intent.putExtra("steamID64",ladder[holder.user_num.text.toString().toInt() - 1].steam_user.steam_id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = ladder.size

}