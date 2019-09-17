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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_teams_player_ready_to_play_match.*
import kotlinx.android.synthetic.main.player_in_selected_team_to_start_inning.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast

class TeamsPlayerReadyToPlayMatch : AppCompatActivity() {

    val groupAdapter= GroupAdapter<ViewHolder>().apply { spanCount=3 }
    lateinit var teamId:String
    lateinit var newMatchId:String
    lateinit var teamA_Id:String
    lateinit var teamB_Id:String

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
            Log.d("GroupAdapter","Clicked")
            toast("Clicked")
   isPlayerAlreadySelected(playerId,name)
          }
    }


    private fun isPlayerAlreadySelected(playerId:String, name: String) {
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
                 title="PlayerBasicProfile Reselection"
                 message="$name already selected for Bating"
                 okButton { dialog -> dialog.dismiss() }
             }.show()
         } else
         {Log.d("Reselection","not found")
             setPlayer(playerId,name)
         }

     }else{
         Log.d("ONDATACHANGE","no data found")
          setPlayer(playerId,name)
     }
     }
 })
    }


    fun setPlayer(playerId: String,name: String)
    {
        val intent = Intent()
        intent.putExtra("name",name)
        intent.putExtra("playerId",playerId)
        setResult(Activity.RESULT_OK, intent)
        finish()

    }
    override fun onResume() {
        super.onResume()
        teamId=intent.getStringExtra("teamId")
        newMatchId=intent.getStringExtra("newMatchId")
        teamA_Id=intent.getStringExtra("teamA_Id")
        teamB_Id=intent.getStringExtra("teamB_Id")

        Log.d("FetchMatch_ID", newMatchId)

        groupAdapter.clear()
    getTeamSquad(teamId,newMatchId)
    }


    private fun fetchTeamSquadData(teamSquad: String, newMatchId: String)
    {
        val matchRef = FirebaseDatabase.getInstance().getReference("Match/$newMatchId")
        matchRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { Log.d("FetchTeam_ID", "onCancelled") }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    Log.d("FetchTeam_Squad", "exist")
                    val teamMember=p0.child("$teamSquad").value as ArrayList<*>
                    val playerRef=FirebaseDatabase.getInstance().getReference("PlayerBasicProfile")
                    var p_key:String
                    teamMember.forEach {
                        p_key=it.toString()
                        Log.d("FetchTeam_LIST",p_key)
                        playerRef.child(p_key).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) { toast(p0.message) }
                            override fun onDataChange(p0: DataSnapshot) {
                                val playerId = p0.child("playerId").value.toString()
                                val playerName = p0.child("name").value.toString()
                                Log.d("FetchTeam_Data",playerName)
                                groupAdapter.add(SelectedTeamPlayer(playerName,playerId))

                            }
                        }
                        )
                    }

                }
            }
        })
    }
    private fun getTeamSquad(teamId:String,newMatchId:String) {
        Log.d("FetchTeam_ID", teamId)
        when (teamId) {
            teamA_Id -> { fetchTeamSquadData("team_A_Squad", newMatchId) }
            teamB_Id->{ fetchTeamSquadData("team_B_Squad",newMatchId)}
        }
    }


        class SelectedTeamPlayer(val name: String,val player_id:String) : Item<ViewHolder>() {
            override fun getLayout(): Int {
                return R.layout.player_in_selected_team_to_start_inning
            }

            override fun bind(viewHolder: ViewHolder, position: Int) {
                viewHolder.itemView.playerName_PlayerInTeamToStartInning.text = name

            }

            override fun getSpanSize(spanCount: Int, position: Int): Int {
                return spanCount / 3
            }

        }

}
