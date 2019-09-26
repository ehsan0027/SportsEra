package view.team

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sportsplayer.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_teams_player_ready_to_play_match.*
import kotlinx.android.synthetic.main.player_in_selected_team_to_start_inning.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import view.GlobalVariable

class TeamsPlayerReadyToPlayMatch : AppCompatActivity() {

    val groupAdapter= GroupAdapter<ViewHolder>().apply { spanCount=3 }
    lateinit var teamId:String
    lateinit var newMatchId:String
    lateinit var bowler:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams_player_ready_to_play_match)

        //recycler View initialization
        recyclerView_TeamsPlayerReadyToPlayMatch.apply {
            layoutManager= GridLayoutManager(context,groupAdapter.spanCount).apply {
                spanSizeLookup=groupAdapter.spanSizeLookup
            }
            adapter=groupAdapter
        }
//groupAdapter OnItemClick Listener
        groupAdapter.setOnItemClickListener { player, view ->
            val team_player = player as SelectedTeamPlayer
            val name=team_player.name
            val playerId=team_player.player_id
            val player_img =team_player.player_img
            Log.d("GroupAdapter","Clicked")
            toast("Clicked")
            if(GlobalVariable.BOWLING_TEAM_ID==teamId)
            {
                setPlayer(playerId,name,player_img)
            }
             isPlayerAlreadySelected(playerId,name,player_img)
          }
    }


    private fun isPlayerAlreadySelected(playerId:String, name: String,player_img:String) {
        val matchRef=FirebaseDatabase.getInstance().getReference("/MatchScore/$newMatchId/$teamId")
 matchRef.addListenerForSingleValueEvent(object :ValueEventListener{
     override fun onCancelled(p0: DatabaseError) {}
     override fun onDataChange(p0: DataSnapshot) {

     if (p0.exists())
     {
         var found =false
         for(it in p0.children){
             Log.d("PlayersMatch",it.key)
             if(playerId==it.key)
             { found=true
                 break
             }
         }
         if(found)
         {Log.d("Reselection","found")
             alert {
                 title="Player Reselection"
                 message="$name Already Selected"
                 okButton { dialog -> dialog.dismiss() }
             }.show()
         } else
         {Log.d("Reselection","not found")
             setPlayer(playerId,name,player_img)
         }

     }else{
         Log.d("ONDATACHANGE","no data found")
          setPlayer(playerId,name,player_img)
     }
     }
 })
    }


    fun setPlayer(playerId: String,name: String,player_img:String)
    {
        val intent = Intent()
        intent.putExtra("name",name)
        intent.putExtra("playerId",playerId)
        intent.putExtra("player_img",player_img)
        setResult(Activity.RESULT_OK, intent)
        finish()

    }
    override fun onResume() {
        super.onResume()
        teamId=intent.getStringExtra("teamId")
        newMatchId=intent.getStringExtra("newMatchId")
        groupAdapter.clear()
        getTeamSquad(teamId)
    }


    private fun getTeamSquad(teamId:String) {
        Log.d("FetchTeam_ID", teamId)
        val playerRef= FirebaseDatabase.getInstance()
        val teamsPlayerRef = FirebaseDatabase.getInstance().getReference("/Team/$teamId/TeamSquad")
        teamsPlayerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { Log.d("FetchTeam_ID", "onCancelled") }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    Log.d("FetchTeam_ID", "team exist")
                    p0.children.forEach {
                        val playerId = it.key
                        Log.d("FetchPlayer_ID",playerId)

                        playerRef.getReference("/PlayerBasicProfile/$playerId").also { task ->
                            task.addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    //get the actual player
                                    val playerId = p0.child("playerId").value.toString()
                                    val playerName = p0.child("name").value.toString()
                                    val profile_img = p0.child("profile_img").value.toString()

                                    groupAdapter.add(SelectedTeamPlayer(playerName, playerId,profile_img))

                                }

                            })
                        }

                    }
                }
            }
        })
    }


        class SelectedTeamPlayer(val name: String,val player_id:String, val player_img:String) : Item<ViewHolder>() {
            override fun getLayout(): Int {
                return R.layout.player_in_selected_team_to_start_inning
            }

            override fun bind(viewHolder: ViewHolder, position: Int) {
                viewHolder.itemView.playerName_PlayerInTeamToStartInning.text = name
                Picasso.get().load(player_img).into(viewHolder.itemView.player_img_player_selection)

            }

            override fun getSpanSize(spanCount: Int, position: Int): Int {
                return spanCount / 3
            }

        }

}
