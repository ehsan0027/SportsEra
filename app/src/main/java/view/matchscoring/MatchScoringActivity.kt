package view.matchscoring

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsplayer.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pawegio.kandroid.toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_match_scoring.*
import model.player.PlayerPlayingMatch
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivityForResult
import view.GlobalVariable
import view.team.TeamsPlayerReadyToPlayMatch


@Suppress("DEPRECATION", "PLUGIN_WARNING")
class MatchScoringActivity : AppCompatActivity(), View.OnClickListener {

    private var databaseReference: FirebaseDatabase? = null
    val groupAdapter = GroupAdapter<ViewHolder>().apply { spanCount = 2 }
    lateinit var batting_Team: String
    lateinit var currentMatch_Id: String
    lateinit var bowling_Team: String
    val battingTeamPlayerList = ArrayList<String>()
    val bowlingTeamPlayerList = ArrayList<String>()
    val batting_team_Squad_Name = HashMap<String, String>()//Creating an empty arraylist
    val bowling_team_Squad_Name = HashMap<String, String>()//Creating an empty arraylist
    val ballAdapter = GroupAdapter<ViewHolder>()
    val newplayer_RC = 1
    val newBowler_RC = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_scoring)
        score_detail_textView.text="Toss Won By ${GlobalVariable.BATTING_TEAM_NAME}"
        databaseReference = FirebaseDatabase.getInstance()

        getBattingTeamSquadName(GlobalVariable.BATTING_TEAM_ID)
        getBowlingTeamSquadName(GlobalVariable.BOWLING_TEAM_ID)

        //Assign Click Listeners
        threeRuns.setOnClickListener(this)
        fourRuns.setOnClickListener(this)
        sixRuns.setOnClickListener(this)
        undo.setOnClickListener(this)
        ZeroRun.setOnClickListener(this)
        oneRun.setOnClickListener(this)
        twoRuns.setOnClickListener(this)
        fiveOrSevenRuns.setOnClickListener(this)
        out_button_MatchScoringActivity.setOnClickListener(this)
        legByes.setOnClickListener(this)
        byeRuns.setOnClickListener(this)
        noBall.setOnClickListener(this)
        wideBall.setOnClickListener(this)
//[End]

        //ball recyclerView
        ball_by_ball_score_recyclerView.adapter = ballAdapter
        batsman_1_card.setOnClickListener {
            toast("Clicked")
            batsman_2_card.strokeColor = resources.getColor(R.color.DarkRed)
            batsman_1_card.strokeColor = resources.getColor(R.color.BlueViolet)
            batsman_1_card.isChecked = true

        }
        batsman_2_card.setOnClickListener {
            batsman_1_card.strokeColor = resources.getColor(R.color.DarkRed)
            batsman_2_card.strokeColor = resources.getColor(R.color.BlueViolet)
            batsman_1_card.isChecked = false

        }






        batsman_1_card.setOnCheckedChangeListener { card, isChecked ->
            toast("setOnCheckedChangeListener")
            card.strokeColor = resources.getColor(R.color.DarkRed)
            if (isChecked) {
                toast("CardView is Checked")
            } else {
                toast("UnChecked")

            }
        }

    }


    override fun onStart() {
        super.onStart()
        supportActionBar?.title = GlobalVariable.BATTING_TEAM_NAME
        batsman_1_card.isChecked=true
        Log.d("overs",GlobalVariable.MATCH_OVERS.toString())
        match_total_overs.text = GlobalVariable.MATCH_OVERS.toString()
        match_total_overs.text = GlobalVariable.MATCH_OVERS.toString()


    }

    override fun onResume() {
        super.onResume()
        batsman_1_name.text = GlobalVariable.STRIKER_NAME
        batsman_2_name.text = GlobalVariable.NON_STRIKER_NAME
        bowler_name.text = GlobalVariable.BOWLER_NAME

    }

    fun getTeamAsquadName(squad: ArrayList<String>) {
        val newDatabaseRef = FirebaseDatabase.getInstance().getReference("/PlayerBasicProfileInfo")
        squad.forEach {

            newDatabaseRef.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.exists()) {
                        val playerName = p0.child("name").value.toString()
                        batting_team_Squad_Name[it] = playerName
                    }
                }
            })
        }

    }


    fun getTeamBsquadName(squad: ArrayList<String>) {
        val newDatabaseRef = FirebaseDatabase.getInstance().getReference("/PlayerBasicProfileInfo")
        squad.forEach {

            newDatabaseRef.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.exists()) {
                        val playerName = p0.child("name").value.toString()
                        bowling_team_Squad_Name[it] = playerName
                    }
                }
            })
        }

    }


    private fun showRemovePopUpDialog(p_id: String,pName:String,bId:Int) {
        val outPopUpDialog= Dialog(this)
        outPopUpDialog.setCancelable(true)
        val view = layoutInflater.inflate(R.layout.out_cause, null)
        outPopUpDialog.setContentView(view)
        val cancel_Button = view?.find<Button>(R.id.cancelButton_OutCausePopUp)
        val out_Button = view?.find<Button>(R.id.out_button_OutCause_PopUp)
        val runs = view?.find<TextView>(R.id.runs_TextView_OutCause_PopUp)
        val balls = view?.find<TextView>(R.id.balls_TextView_OutCause_PopUp)
        val name = view?.find<TextView>(R.id.player_Name_outCause_Popup)
        name?.text = pName
        when (bId){
            R.id.batsman_1_card->{
                runs?.text=GlobalVariable.STRIKER_SCORE.toString()
                balls?.text =GlobalVariable.STRIKER_BALL.toString()
            }
            R.id.batsman_2_card->{
                runs?.text=GlobalVariable.NON_STRIKER_SCORE.toString()
                balls?.text =GlobalVariable.NON_STRIKER_BALL.toString()
            }
        }


        cancel_Button?.setOnClickListener { outPopUpDialog.dismiss() }
        out_Button?.setOnClickListener {
            GlobalVariable.TEAM_WICKET = 1 + GlobalVariable.TEAM_WICKET
            val db_Ref = FirebaseDatabase.getInstance().reference
            val newData = HashMap<String, Any>()
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/TeamWicket"] =
                GlobalVariable.TEAM_WICKET.toString()
            db_Ref.updateChildren(newData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("synced")
                }
            }
            outPlayer(p_id)
            outPopUpDialog.dismiss()
        }

        outPopUpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        outPopUpDialog.show()
    }


    fun outPlayer(p_id: String) {
        val newDatabase = FirebaseDatabase.getInstance()
            .getReference("/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/$p_id")
        newDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    var outSquadRef = FirebaseDatabase.getInstance().reference
                    val setOutSquad = HashMap<String, Any?>()
                    setOutSquad["MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/OutSquad/$p_id"] =p0.value
                    outSquadRef.updateChildren(setOutSquad).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            outSquadRef = FirebaseDatabase.getInstance().reference
                            val removeOutPlayer = HashMap<String, Any?>()
                            removeOutPlayer["MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/$p_id"] =
                                null
                            outSquadRef.updateChildren(removeOutPlayer)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        //START ACTIVITY TO SELECT NEW BATSMAN
                                        startActivityForResult<TeamsPlayerReadyToPlayMatch>(
                                            newplayer_RC,
                                            "teamId" to GlobalVariable.BATTING_TEAM_ID,
                                            "newMatchId" to GlobalVariable.MATCH_ID
                                        )
                                    }
                                }.addOnFailureListener { e: Exception -> toast(e.localizedMessage) }

                        }
                    }
                }

            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                newplayer_RC -> {
                    val playerId = data.getStringExtra("playerId")
                    val name = data.getStringExtra("name")
                    if (playerId.isNotEmpty()) {
                        if (batsman_1_card.isChecked) {
                            GlobalVariable.STRIKER_ID = playerId
                            addNewBatsman(playerId)
                            GlobalVariable.STRIKER_NAME = name
                            GlobalVariable.STRIKER_SCORE = 0
                            GlobalVariable.STRIKER_BALL = 0
                            GlobalVariable.STRIKER_doubles = 0
                            GlobalVariable.STRIKER_TRIPLES = 0
                            batsman_1_name.text = GlobalVariable.STRIKER_NAME
                            batsman_1_score.text = GlobalVariable.STRIKER_SCORE.toString()
                        } else {
                            GlobalVariable.NON_STRIKER_ID = playerId
                            addNewBatsman(playerId)
                            GlobalVariable.NON_STRIKER_NAME = name
                            GlobalVariable.STRIKER_TRIPLES = 0
                            GlobalVariable.STRIKER_SCORE = 0
                            GlobalVariable.non_STRIKER_doubles = 0
                            GlobalVariable.NON_STRIKER_BALL = 0
                            batsman_2_name.text = GlobalVariable.NON_STRIKER_NAME
                            batsman_2_score.text = GlobalVariable.NON_STRIKER_SCORE.toString()

                        }
                    }
                }
                newBowler_RC->{
                    val playerId = data.getStringExtra("playerId")
                    val name = data.getStringExtra("name")
                    GlobalVariable.BOWLER_NAME=name
                    GlobalVariable.BOWLER_ID=playerId
                    GlobalVariable.BOWLER_BALLS_BOWLED=0
                    GlobalVariable.BOWLER_RUNS_CONCEDED=0
                    GlobalVariable.BOWLER_OVERS=0
                }
            }
        }
    }

    private fun addNewBatsman(playerId: String?) {
        val batRef = FirebaseDatabase.getInstance().reference
        val bat = HashMap<String,Any>()
        val newP=PlayerPlayingMatch()
        bat["MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/$playerId"]=newP
        batRef.updateChildren(bat).addOnCompleteListener { task->
            if (task.isSuccessful){
                toast("New Player Added")
            }
        }

    }


    private fun setOutPlayer(player_Id: String, nameList: HashMap<String, String>,bId:Int) {
        Log.d("PlayerID**",player_Id)
        val p_name = nameList[player_Id]
        showRemovePopUpDialog(player_Id,p_name!!,bId)


    }


    fun updateStrikerScore() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/score"] =
            GlobalVariable.STRIKER_SCORE.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/dot_balls_played"] =
            GlobalVariable.Striker_DOT_BALLS_Played.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/ball_played"] =
            GlobalVariable.STRIKER_BALL.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/doubles"] =
            GlobalVariable.STRIKER_doubles.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/triples"] =
            GlobalVariable.STRIKER_TRIPLES.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/teamScore"] =
            GlobalVariable.CURRENT_TEAM_SCORE.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
            GlobalVariable.BOWLER_RUNS_CONCEDED.toString()
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
    }


    fun updateNonStrikerScore() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/score"] =
            GlobalVariable.NON_STRIKER_SCORE.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/dot_balls_played"] =
            GlobalVariable.Non_Striker_DOT_BALLS_Played.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/ball_played"] =
            GlobalVariable.NON_STRIKER_BALL.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/doubles"] =
            GlobalVariable.non_STRIKER_doubles.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/triples"] =
            GlobalVariable.non_STRIKER_TRIPLES.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/teamScore"] =
            GlobalVariable.CURRENT_TEAM_SCORE.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
            GlobalVariable.BOWLER_RUNS_CONCEDED.toString()

        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
    }


    fun updateStrikerFourCount() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/score"] =
            GlobalVariable.STRIKER_SCORE.toString()
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/ball_played"] =
            GlobalVariable.STRIKER_BALL.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/num_four"] =
            GlobalVariable.STRIKER_NUM_FOUR.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/teamScore"] =
            GlobalVariable.CURRENT_TEAM_SCORE.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
            GlobalVariable.BOWLER_RUNS_CONCEDED.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/num_fours_conceded"] =
            GlobalVariable.num_fours_conceded.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/dot_balls_bowled"] =
            GlobalVariable.DOT_BALLS_BOWLED.toString()


        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
    }

    fun updateStrikerSixCount() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/score"] =
            GlobalVariable.STRIKER_SCORE.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/ball_played"] =
            GlobalVariable.STRIKER_BALL.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/num_six"] =
            GlobalVariable.STRIKER_NUM_SIX.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/teamScore"] =
            GlobalVariable.CURRENT_TEAM_SCORE.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
            GlobalVariable.BOWLER_RUNS_CONCEDED.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/num_sixes_conceded"] =
            GlobalVariable.num_fours_conceded.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/dot_balls_bowled"] =
            GlobalVariable.DOT_BALLS_BOWLED.toString()


        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
    }


    fun updateNonStrikerSixCount() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/score"] =
            GlobalVariable.NON_STRIKER_SCORE.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/ball_played"] =
            GlobalVariable.NON_STRIKER_BALL.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/num_six"] =
            GlobalVariable.NON_STRIKER_NUM_SIX.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/teamScore"] =
            GlobalVariable.CURRENT_TEAM_SCORE.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
            GlobalVariable.BOWLER_RUNS_CONCEDED.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/num_sixes_conceded"] =
            GlobalVariable.num_fours_conceded.toString()


        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
    }


    fun updateNonStrikerFourCount() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/score"] =
            GlobalVariable.NON_STRIKER_SCORE.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/ball_played"] =
            GlobalVariable.NON_STRIKER_BALL.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/num_four"] =
            GlobalVariable.NON_STRIKER_NUM_SIX.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/teamScore"] =
            GlobalVariable.CURRENT_TEAM_SCORE.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
            GlobalVariable.BOWLER_RUNS_CONCEDED.toString()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/num_fours_conceded"] =
            GlobalVariable.num_fours_conceded.toString()

        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
    }

    private fun showScreenContent() {
        //Team
        team_current_score.text = GlobalVariable.CURRENT_TEAM_SCORE.toString()
        team_current_wickets.text = GlobalVariable.TEAM_WICKET.toString()
        team_current_overs_played.text = GlobalVariable.CURRENT_OVER.toString()
        team_current_over_balls_played.text = GlobalVariable.CURRENT_BALL.toString()
        current_run_rate_match_scoring.text = GlobalVariable.TEAM_CRR.toString()
        required_run_rate_match_scoring.text = GlobalVariable.TEAM_RRR.toString()


        //Partnership
        partnership_runs.text = GlobalVariable.CURRENT_PARTNERSHIP_RUNS.toString()
        partnership_balls_played.text = GlobalVariable.CURRENT_PARTNERSHIP_BALLS.toString()

        //Batsman 1
        batsman_1_name.text = GlobalVariable.STRIKER_NAME
        batsman_1_score.text = GlobalVariable.STRIKER_SCORE.toString()
        batsman_1_balls_played.text = GlobalVariable.STRIKER_BALL.toString()

        //Batsman 2
        batsman_2_name.text = GlobalVariable.NON_STRIKER_NAME
        batsman_2_score.text = GlobalVariable.NON_STRIKER_SCORE.toString()
        batsman_2_balls_played.text = GlobalVariable.NON_STRIKER_BALL.toString()

        //Bowler
        bowler_name.text = GlobalVariable.BOWLER_NAME
        no_overs_bowled.text = GlobalVariable.BOWLER_OVERS.toString()
        no_balls_bowled.text = GlobalVariable.BOWLER_BALLS_BOWLED.toString()
        maiden_overs.text = GlobalVariable.BOWLER_MAIDEN.toString()
        runs_concede.text = GlobalVariable.BOWLER_RUNS_CONCEDED.toString()
        bowler_wickets.text = GlobalVariable.BOWLER_WICKET.toString()


    }


    fun getBattingTeamSquadName(teamId:String){
        var newDatabaseRef = FirebaseDatabase.getInstance().getReference("TeamsPlayer/$teamId")
    newDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {}
        override fun onDataChange(p0: DataSnapshot) {
           if (p0.exists()){
               val pRef =FirebaseDatabase.getInstance().getReference("PlayerBasicProfile")
               p0.children.forEach {
                   val pId =it.key.toString()
                   pRef.child(pId).addListenerForSingleValueEvent(object : ValueEventListener{
                       override fun onCancelled(p0: DatabaseError) {}
                       override fun onDataChange(p0: DataSnapshot) {
                      if (p0.exists()){
                          val pName=p0.child("name").value.toString()
                          batting_team_Squad_Name[pId] = pName
                          Log.d("BLSquadBAT",batting_team_Squad_Name[pId])
                      }
                       }
                   })
               }
           }
        }
    })
    }


    fun getBowlingTeamSquadName(teamId:String){
        val newDatabaseRef = FirebaseDatabase.getInstance().getReference("TeamsPlayer/$teamId")
        newDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    var pRef =FirebaseDatabase.getInstance().getReference("PlayerBasicProfile")
                    p0.children.forEach {
                        val pId =it.key.toString()
                        pRef.child(pId).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {}
                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists()){
                                    val pName=p0.child("name").value.toString()
                                    bowling_team_Squad_Name[pId] = pName
                                    Log.d("BLSquad",bowling_team_Squad_Name[pId])
                                }
                            }
                        })
                    }
                }
            }
        })
    }

    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.ZeroRun -> {
                if (batsman_1_card.isChecked) {
                    showBall(0)
                    //Team


                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    //Batsman
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.Striker_DOT_BALLS_Played=1+  GlobalVariable.Striker_DOT_BALLS_Played
                    //Bowler
                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED
                    GlobalVariable.DOT_BALLS_BOWLED=1+ GlobalVariable.DOT_BALLS_BOWLED
                    showScreenContent()
                    updateStrikerScore()
                } else {
                    showBall(0)
                    //Team

                    //Batsman
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.Non_Striker_DOT_BALLS_Played=1+GlobalVariable.Non_Striker_DOT_BALLS_Played
                    //Bowler
                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED
                    GlobalVariable.DOT_BALLS_BOWLED=1+ GlobalVariable.DOT_BALLS_BOWLED
                    showScreenContent()
                    updateNonStrikerScore()
                }
            }
            R.id.oneRun -> {

                if (batsman_1_card.isChecked) {
                    showBall(1)
                    //Team

                    GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE


                    //Partnership

                    //Batsman
                    GlobalVariable.STRIKER_SCORE = 1 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL

                    //Bowler
                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    switchStriker()
                    showScreenContent()
                    updateStrikerScore()

                } else {
                    showBall(1)
                    //Team
                    GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE

                    //Batsman
                    GlobalVariable.NON_STRIKER_SCORE = 1 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL

                    //Bowler
                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    switchStriker()
                    showScreenContent()
                    updateNonStrikerScore()
                }
            }
            R.id.twoRuns -> {
                if (batsman_1_card.isChecked) {
                    showBall(2)
                    //Team

                    GlobalVariable.CURRENT_TEAM_SCORE = 2 + GlobalVariable.CURRENT_TEAM_SCORE
                    //Batsman
                    GlobalVariable.STRIKER_SCORE = 2 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_doubles = 1 + GlobalVariable.STRIKER_doubles

                    //Bowler
                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 2 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    showScreenContent()
                    updateStrikerScore()
                } else {
                    showBall(2)
                    GlobalVariable.NON_STRIKER_SCORE = 2 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.CURRENT_TEAM_SCORE = 2 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.non_STRIKER_doubles = 1 + GlobalVariable.non_STRIKER_doubles

                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 2 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    showScreenContent()
                    updateNonStrikerScore()
                }
            }
            R.id.threeRuns -> {
                if (batsman_1_card.isChecked) {
                    showBall(3)
                    GlobalVariable.STRIKER_SCORE = 3 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_TRIPLES = 1 + GlobalVariable.STRIKER_TRIPLES
                    GlobalVariable.CURRENT_TEAM_SCORE = 3 + GlobalVariable.CURRENT_TEAM_SCORE
                    switchStriker()
                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 3 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    showScreenContent()
                    updateStrikerScore()
                } else {
                    showBall(3)
                    GlobalVariable.non_STRIKER_TRIPLES = 1 + GlobalVariable.non_STRIKER_TRIPLES
                    GlobalVariable.NON_STRIKER_SCORE = 3 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.CURRENT_TEAM_SCORE = 3 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 3 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    switchStriker()
                    showScreenContent()
                    updateNonStrikerScore()
                }
            }

            R.id.fourRuns -> {
                if (batsman_1_card.isChecked) {
                    showBall(4)
                    GlobalVariable.STRIKER_SCORE = 4 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_NUM_FOUR = 1 + GlobalVariable.STRIKER_NUM_FOUR
                    GlobalVariable.CURRENT_TEAM_SCORE = 4 + GlobalVariable.CURRENT_TEAM_SCORE

                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 4 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    GlobalVariable.num_fours_conceded = 1 + GlobalVariable.num_fours_conceded

                    showScreenContent()
                    updateStrikerFourCount()
                } else {
                    showBall(4)
                    GlobalVariable.NON_STRIKER_SCORE = 4 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.CURRENT_TEAM_SCORE = 4 + GlobalVariable.CURRENT_TEAM_SCORE

                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 4 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    GlobalVariable.num_fours_conceded = 1 + GlobalVariable.num_fours_conceded

                    showScreenContent()
                    updateNonStrikerFourCount()
                }
            }
            R.id.sixRuns -> {
                if (batsman_1_card.isChecked) {
                    showBall(6)
                    GlobalVariable.STRIKER_SCORE = 6 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_NUM_SIX = 1 + GlobalVariable.STRIKER_NUM_SIX
                    GlobalVariable.CURRENT_TEAM_SCORE = 6 + GlobalVariable.CURRENT_TEAM_SCORE

                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 6 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    runs_concede.text = GlobalVariable.BOWLER_RUNS_CONCEDED.toString()
                    GlobalVariable.num_sixes_conceded = 1 + GlobalVariable.num_sixes_conceded

                    showScreenContent()
                    updateStrikerSixCount()
                } else {
                    showBall(6)
                    GlobalVariable.NON_STRIKER_SCORE = 6 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.NON_STRIKER_NUM_SIX = 1 + GlobalVariable.NON_STRIKER_NUM_SIX
                    GlobalVariable.CURRENT_TEAM_SCORE = 6 + GlobalVariable.CURRENT_TEAM_SCORE

                    GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 6 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    GlobalVariable.num_sixes_conceded = 1 + GlobalVariable.num_sixes_conceded

                    showScreenContent()
                    updateNonStrikerSixCount()
                }
            }
            R.id.out_button_MatchScoringActivity-> {

                GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED
                showScreenContent()
                if (batsman_1_card.isChecked) {
                    setOutPlayer(GlobalVariable.STRIKER_ID, batting_team_Squad_Name,R.id.batsman_1_card)
                } else {
                    setOutPlayer(GlobalVariable.NON_STRIKER_ID, batting_team_Squad_Name,R.id.batsman_2_card)
                }

            }

        }

    }
    fun switchStriker()
    {
        if(batsman_1_card.isChecked){
            batsman_1_card.isChecked=false
            batsman_2_card.isChecked=true
        }else{
            batsman_1_card.isChecked=true
            batsman_2_card.isChecked=false

        }
    }


    fun showBall(runs: Int) {
        GlobalVariable.CURRENT_BALL = 1 + GlobalVariable.CURRENT_BALL

            GlobalVariable.TEAM_CRR = (GlobalVariable.CURRENT_TEAM_SCORE / (GlobalVariable.CURRENT_OVER).toFloat()+(((GlobalVariable.CURRENT_BALL).toFloat())/6.0)).toFloat()

        if (GlobalVariable.CURRENT_BALL == 6) {
            GlobalVariable.CURRENT_OVER = 1 + GlobalVariable.CURRENT_OVER
            GlobalVariable.BOWLER_OVERS = 1 + GlobalVariable.BOWLER_OVERS
            GlobalVariable.BOWLER_BALLS_BOWLED=0
            startActivityForResult<TeamsPlayerReadyToPlayMatch>(newBowler_RC,
                "teamId" to GlobalVariable.BOWLING_TEAM_ID,
                "newMatchId" to GlobalVariable.MATCH_ID)
            GlobalVariable.bowler_selection=2
            if(GlobalVariable.MATCH_OVERS==GlobalVariable.CURRENT_OVER)
            {
                //////////////////////////////////
            }
            switchStriker()
            GlobalVariable.CURRENT_BALL = 0
            ballAdapter.clear()
            //showPopUp
        } else {
            ballAdapter.add(ScoreBall(runs))
        }

    }

    class ScoreBall(val runs: Int) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.this_over_card
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val btn = viewHolder.itemView.find<Button>(R.id.run_at_a_ball)

            btn.text = runs.toString()

        }

    }


}






