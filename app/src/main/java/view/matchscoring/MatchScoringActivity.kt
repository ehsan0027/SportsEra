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
import model.Bowler
import model.player.PlayerPlayingMatch
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivityForResult
import view.Dashboard
import view.GlobalVariable
import view.team.TeamsPlayerReadyToPlayMatch


@Suppress("DEPRECATION", "PLUGIN_WARNING")
class MatchScoringActivity() : AppCompatActivity(), View.OnClickListener {

    var updateLiveData :NotifyDataChange?=null
    private var databaseReference: FirebaseDatabase? = null
    val groupAdapter = GroupAdapter<ViewHolder>().apply { spanCount = 2 }
    lateinit var batting_Team: String
    lateinit var currentMatch_Id: String
    lateinit var bowling_Team: String
    val battingTeamPlayerList = ArrayList<String>()
    val bowlingTeamPlayerList = ArrayList<String>()
    val bowlersList = ArrayList<String>()
    val firstBowlerData = ArrayList<Any>()
    val secondBowlerData = ArrayList<Any>()

    val batting_team_Squad_Name = HashMap<String, String>()//Creating an empty arraylist
    val bowling_team_Squad_Name = HashMap<String, String>()//Creating an empty arraylist
    val ballAdapter = GroupAdapter<ViewHolder>()
    val newplayer_RC = 1
    val newBowler_RC = 2


    interface NotifyDataChange {
        fun updataData(){
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_scoring)
        score_detail_textView.text = "Toss Won By ${GlobalVariable.BATTING_TEAM_NAME}"
        databaseReference = FirebaseDatabase.getInstance()
        val ctx=Dashboard()
        updateLiveData=ctx
        //getBattingTeamSquadName(GlobalVariable.BATTING_TEAM_ID)
        //getBowlingTeamSquadName(GlobalVariable.BOWLING_TEAM_ID)
        getTeamSquadCount(GlobalVariable.BATTING_TEAM_ID)
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

        //Card1
        batsman_1_card.setOnClickListener {
            batsman_1_card.isChecked= !batsman_1_card.isChecked
            if(batsman_1_card.isChecked) {
                batsman_2_card.isChecked =false
                batsman_2_card.strokeColor = resources.getColor(R.color.DarkRed)
                batsman_1_card.strokeColor = resources.getColor(R.color.BlueViolet)
            }else{
                batsman_2_card.isChecked=true
            }
        }
        //Card2
        batsman_2_card.setOnClickListener {
            batsman_2_card.isChecked = !(batsman_2_card.isChecked)

            if (batsman_2_card.isChecked) {
                batsman_1_card.isChecked = false
                batsman_1_card.strokeColor = resources.getColor(R.color.DarkRed)
                batsman_2_card.strokeColor = resources.getColor(R.color.BlueViolet)
            } else {
                batsman_1_card.isChecked = true
            }

        }

        batsman_1_card.setOnCheckedChangeListener { card, isChecked ->
            run {
                if (isChecked) {
                    batsman_2_card.isChecked = false
                }
            }
        }
        batsman_2_card.setOnCheckedChangeListener { card, isChecked ->
            run {
                if (isChecked) {
                    batsman_1_card.isChecked = false
                }
            }
        }

    }

///////////COMPILE AND  RUN
    fun updateOvers()
{
    val inningRef=FirebaseDatabase.getInstance().reference
    val newHash = HashMap<String,Any>()
    newHash["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/overs"]=GlobalVariable.Current_Over
    newHash["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/over_balls"]=GlobalVariable.CURRENT_BALL
    inningRef.updateChildren(newHash).addOnCompleteListener { task ->
        if (task.isSuccessful){
            toast("synced")
        }

    }
}

    override fun onStart() {
        super.onStart()
        supportActionBar?.title = GlobalVariable.BATTING_TEAM_NAME
        batsman_1_card.isChecked = true
        Log.d("overs", GlobalVariable.MATCH_OVERS.toString())
        match_total_overs.text = GlobalVariable.MATCH_OVERS.toString()
        match_total_overs.text = GlobalVariable.MATCH_OVERS.toString()
        bowlersList.add(GlobalVariable.BOWLER_ID)
//        getTeamAsquadName()
      //  secondBowlerData[0]=GlobalVariable.BOWLING_TEAM_SQUAD[1]

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

    fun bowlingTeamSquadId(){
        val db_Ref=FirebaseDatabase.getInstance().getReference("")

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


    private fun showRemovePopUpDialog(p_id: String, pName: String, bId: Int) {
        val outPopUpDialog = Dialog(this)
        outPopUpDialog.setCancelable(true)
        val view = layoutInflater.inflate(R.layout.out_cause, null)
        outPopUpDialog.setContentView(view)
        val cancel_Button = view?.find<Button>(R.id.cancelButton_OutCausePopUp)
        val out_Button = view?.find<Button>(R.id.out_button_OutCause_PopUp)
        val runs = view?.find<TextView>(R.id.runs_TextView_OutCause_PopUp)
        val balls = view?.find<TextView>(R.id.balls_TextView_OutCause_PopUp)
        val name = view?.find<TextView>(R.id.player_Name_outCause_Popup)
        name?.text = pName
        when (bId) {
            R.id.batsman_1_card -> {
                runs?.text = GlobalVariable.STRIKER_SCORE.toString()
                balls?.text = GlobalVariable.STRIKER_BALL.toString()
            }
            R.id.batsman_2_card -> {
                runs?.text = GlobalVariable.NON_STRIKER_SCORE.toString()
                balls?.text = GlobalVariable.NON_STRIKER_BALL.toString()
            }
        }


        cancel_Button?.setOnClickListener { outPopUpDialog.dismiss() }
        out_Button?.setOnClickListener {
            GlobalVariable.TEAM_WICKET = 1 + GlobalVariable.TEAM_WICKET
            val db_Ref = FirebaseDatabase.getInstance().reference
            val newData = HashMap<String, Any>()
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/wickets"] =
                GlobalVariable.TEAM_WICKET
            db_Ref.updateChildren(newData).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val db_Ref = FirebaseDatabase.getInstance().reference
                    val newData = HashMap<String, Any>()
                    newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.Current_Over}/${GlobalVariable.CURRENT_BALL}/Out"] = p_id

                    db_Ref.updateChildren(newData).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            toast("synced")
                        }
                    }

                    toast("synced")
                }
            }
            outPlayer(p_id)
            outPopUpDialog.dismiss()
        }

        outPopUpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        outPopUpDialog.show()
    }


    private fun outPlayer(p_id: String) {
        val newDatabase = FirebaseDatabase.getInstance()
            .getReference("/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/$p_id")
        newDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    var outSquadRef = FirebaseDatabase.getInstance().reference
                    val setOutSquad = HashMap<String, Any?>()
                    setOutSquad["MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/OutSquad/$p_id"] =
                        p0.value
                    outSquadRef.updateChildren(setOutSquad).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            outSquadRef = FirebaseDatabase.getInstance().reference
                            val removeOutPlayer = HashMap<String, Any?>()
                            removeOutPlayer["MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/$p_id"] =
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

                newBowler_RC -> {
                    val playerId = data.getStringExtra("playerId")
                    val name = data.getStringExtra("name")
//
//
//                    if (firstBowlerData[0]==GlobalVariable.BOWLER_ID)
//                    {
//                        updateFirstBowlerData(GlobalVariable.BOWLER_ID)
//                        getBowlerData(playerId)
//                    }else if (secondBowlerData[0]==GlobalVariable.BOWLER_ID){
//                        updateSecondBowlerData(GlobalVariable.BOWLER_ID)
//                        getBowlerData(playerId)
//                    }
//
//
                    GlobalVariable.BOWLER_NAME = name
                    GlobalVariable.BOWLER_ID=playerId
                    if (bowlersList.contains(playerId)) {
                        Log.d("NewOver","Contains")


                        val ndb_Ref = FirebaseDatabase.getInstance()
                            .getReference("/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/$playerId")
                        ndb_Ref.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists()) {

                                    GlobalVariable.BOWLER_BALLS_BOWLED =
                                        p0.child("balls_bowled").value.toString().toInt()
                                    GlobalVariable.BOWLER_OVERS =
                                        p0.child("bowler_overs").value.toString().toInt()
                                    GlobalVariable.BOWLER_WICKET =
                                        p0.child("wickets").value.toString().toInt()
                                    GlobalVariable.BOWLER_RUNS_CONCEDED =
                                        p0.child("runs_conceded").value.toString().toInt()
                                    GlobalVariable.BOWLER_MAIDEN =
                                        p0.child("maiden").value.toString().toInt()
                                    showScreenContent()
                                }
                            }
                        })

                    } else {
//                       secondBowlerData[0]=playerId

                        val newBowler = Bowler()
                        val db_Ref = FirebaseDatabase.getInstance().reference
                        val setBowler = HashMap<String, Any>()
                        setBowler["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/$playerId"] =
                            newBowler
                        db_Ref.updateChildren(setBowler).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                bowlersList.add(playerId)

                                GlobalVariable.BOWLER_OVERS = 0
                                GlobalVariable.BOWLER_MAIDEN = 0
                                GlobalVariable.BOWLER_WICKET = 0
                                GlobalVariable.BOWLER_Wide_ball = 0
                                GlobalVariable.BOWLER_No_ball = 0
                                GlobalVariable.BOWLER_ECONEMY = 0f
                                GlobalVariable.num_fours_conceded = 0
                                GlobalVariable.num_sixes_conceded = 0
                                GlobalVariable.BOWLER_BALLS_BOWLED = 0
                                GlobalVariable.BOWLER_RUNS_CONCEDED = 0
                                GlobalVariable.DOT_BALLS_BOWLED = 0
                                toast("Bowler is Ready")

//                               updateSecondBowlerData(playerId)
//                               GlobalVariable.BOWLER_ID = playerId
                                showScreenContent()


                            }

                        }
                    }
                }
            }
        }
    }

    private fun addNewBatsman(playerId: String?) {
        val batRef = FirebaseDatabase.getInstance().reference
        val bat = HashMap<String, Any>()
        val newP = PlayerPlayingMatch()
        bat["MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.BATTING_TEAM_ID}/$playerId"] =
            newP
        batRef.updateChildren(bat).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("New Player Added")
            }
        }

    }


    private fun setOutPlayer(player_Id: String, name:String, bId: Int) {
        Log.d("PlayerID**", player_Id)

        showRemovePopUpDialog(player_Id, name, bId)


    }

    fun setBallScore(runs:Int,extra:String) {

        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.Current_Over}/${GlobalVariable.CURRENT_BALL}/Runs"] = runs
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }

       when(extra)
       {
           "no_ball"->{
               setExtras(1,"no_ball")

           }
           "wide"->{
               setExtras(1,"wide")

           }
           "leg_bye"->{
               setExtras(1,"leg_bye")

           }
           "bye"->{
               setExtras(1,"bye")
           }
           else->{
               setExtras(runs,extra)
           }
       }
    }

    fun setExtras(score:Int,extra:String){
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.Current_Over}/${GlobalVariable.CURRENT_BALL}/$extra"] = score
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
    }

    fun updateStrikerDotBall() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/dots"] =
            GlobalVariable.Striker_DOT_BALLS_Played
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/balls_played"] =
            GlobalVariable.STRIKER_BALL
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }

    }


    fun updateNonStrikerDotBall() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/dots"] =
            GlobalVariable.Striker_DOT_BALLS_Played
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/balls_played"] =
            GlobalVariable.NON_STRIKER_BALL
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }

    }


    fun updateStrikerScore() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/runs"] =
            GlobalVariable.STRIKER_SCORE
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/balls_played"] =
            GlobalVariable.STRIKER_BALL
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/doubles"] =
            GlobalVariable.STRIKER_doubles
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/triples"] =
            GlobalVariable.STRIKER_TRIPLES
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/InningScore"] =
            GlobalVariable.CURRENT_TEAM_SCORE
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
            GlobalVariable.BOWLER_RUNS_CONCEDED
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
    }


    fun updateNonStrikerScore() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/runs"] =
            GlobalVariable.NON_STRIKER_SCORE
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/balls_played"] =
            GlobalVariable.NON_STRIKER_BALL
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/doubles"] =
            GlobalVariable.non_STRIKER_doubles
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/triples"] =
            GlobalVariable.non_STRIKER_TRIPLES
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/InningScore"] =
            GlobalVariable.CURRENT_TEAM_SCORE
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
            GlobalVariable.BOWLER_RUNS_CONCEDED

        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
    }


    private fun updateStrikerFourCount() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/num_four"] =
            GlobalVariable.STRIKER_NUM_FOUR
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/num_fours_conceded"] =
            GlobalVariable.num_fours_conceded
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
        //Update Striker Data
        updateStrikerScore()

    }

    private fun updateNonStrikerFourCount() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/num_four"] =
            GlobalVariable.NON_STRIKER_NUM_FOUR
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/num_fours_conceded"] =
            GlobalVariable.num_fours_conceded
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
        //UpdateNonStriker Data
        updateNonStrikerScore()
    }

    private fun updateStrikerSixCount() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/num_six"] =
            GlobalVariable.STRIKER_NUM_SIX
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/num_sixes_conceded"] =
            GlobalVariable.num_sixes_conceded

        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }

        //update Striker Data
        updateStrikerScore()
    }


    private fun updateNonStrikerSixCount() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/num_six"] =
            GlobalVariable.NON_STRIKER_NUM_SIX
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/num_sixes_conceded"] =
            GlobalVariable.num_sixes_conceded
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
        //update NonStriker Sata
        updateNonStrikerScore()

    }





    private fun showScreenContent() {
        //Team
        team_current_score.text = GlobalVariable.CURRENT_TEAM_SCORE.toString()
        team_current_wickets.text = GlobalVariable.TEAM_WICKET.toString()
        team_current_overs_played.text = GlobalVariable.Current_Over.toString()
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


    private fun getBattingTeamSquadName(teamId: String) {
        val newDatabaseRef = FirebaseDatabase.getInstance().getReference("TeamsPlayer/$teamId")
        newDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val pRef = FirebaseDatabase.getInstance().getReference("PlayerBasicProfile")
                    p0.children.forEach {
                        val pId = it.key.toString()
                        pRef.child(pId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {}
                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists()) {
                                    val pName = p0.child("name").value.toString()
                                    batting_team_Squad_Name[pId] = pName
                                    Log.d("BLSquadBAT", batting_team_Squad_Name[pId])
                                }
                            }
                        })
                    }
                }
            }
        })
    }


    fun getTeamSquadCount(teamId:String){
        val squadRef=FirebaseDatabase.getInstance().getReference("/MatchInfo/${GlobalVariable.MATCH_ID}/team_A_Squad")
        squadRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    GlobalVariable.BATTING_TEAM_Squad_Count= p0.childrenCount.toInt()
                }
            }
        })
    }

    private fun getBowlingTeamSquadName(teamId: String) {
        val newDatabaseRef = FirebaseDatabase.getInstance().getReference("TeamsPlayer/$teamId")
        newDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    var pRef = FirebaseDatabase.getInstance().getReference("PlayerBasicProfile")
                    p0.children.forEach {
                        val pId = it.key.toString()
                        pRef.child(pId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {}
                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists()) {
                                    val pName = p0.child("name").value.toString()
                                    bowling_team_Squad_Name[pId] = pName
                                    Log.d("BLSquad", bowling_team_Squad_Name[pId])
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
                    showBall("0")
                    //Team
                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    //Batsman
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.Striker_DOT_BALLS_Played =
                        1 + GlobalVariable.Striker_DOT_BALLS_Played

                    //Bowler
                    GlobalVariable.DOT_BALLS_BOWLED = 1 + GlobalVariable.DOT_BALLS_BOWLED
                    showScreenContent()

                    //Update Striker Dot Ball
                    updateStrikerDotBall()
                    setBallScore(0,"score")

                } else {
                    showBall("0")
                    //Team

                    //Batsman
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.Non_Striker_DOT_BALLS_Played =
                        1 + GlobalVariable.Non_Striker_DOT_BALLS_Played
                    //Bowler
                    GlobalVariable.DOT_BALLS_BOWLED = 1 + GlobalVariable.DOT_BALLS_BOWLED
                    showScreenContent()
                    //Update Nonstriker Dot ball
                    updateNonStrikerDotBall()
                    setBallScore(0,"score")
                }
            }
            R.id.oneRun -> {

                if (batsman_1_card.isChecked) {
                    showBall("1")
                    //Team

                    GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE


                    //Partnership

                    //Batsman
                    GlobalVariable.STRIKER_SCORE = 1 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_SINGLES = 1 + GlobalVariable.STRIKER_SINGLES
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL

                    //Bowler
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    switchStriker()
                    showScreenContent()
                    updateStrikerScore()
                    setBallScore(1,"score")

                } else {
                    showBall("1")
                    //Team
                    GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE

                    //Batsman
                    GlobalVariable.NON_STRIKER_SCORE = 1 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_SINGLES = 1 + GlobalVariable.NON_STRIKER_SINGLES
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL

                    //Bowler
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    switchStriker()
                    showScreenContent()
                    updateNonStrikerScore()
                    setBallScore(1,"score")
                }
            }
            R.id.twoRuns -> {
                if (batsman_1_card.isChecked) {
                    showBall("2")
                    //Team

                    GlobalVariable.CURRENT_TEAM_SCORE = 2 + GlobalVariable.CURRENT_TEAM_SCORE
                    //Batsman
                    GlobalVariable.STRIKER_SCORE = 2 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_doubles = 1 + GlobalVariable.STRIKER_doubles

                    //Bowler
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 2 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    showScreenContent()
                    updateStrikerScore()
                    setBallScore(2,"score")
                } else {
                    showBall("2")
                    GlobalVariable.NON_STRIKER_SCORE = 2 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.CURRENT_TEAM_SCORE = 2 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.non_STRIKER_doubles = 1 + GlobalVariable.non_STRIKER_doubles

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 2 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    showScreenContent()
                    updateNonStrikerScore()
                    setBallScore(2,"score")
                }
            }
            R.id.threeRuns -> {
                if (batsman_1_card.isChecked) {
                    showBall("3")
                    GlobalVariable.STRIKER_SCORE = 3 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_TRIPLES = 1 + GlobalVariable.STRIKER_TRIPLES
                    GlobalVariable.CURRENT_TEAM_SCORE = 3 + GlobalVariable.CURRENT_TEAM_SCORE
                    switchStriker()
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 3 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    showScreenContent()
                    updateStrikerScore()
                    setBallScore(3,"score")
                } else {
                    showBall("3")
                    GlobalVariable.non_STRIKER_TRIPLES = 1 + GlobalVariable.non_STRIKER_TRIPLES
                    GlobalVariable.NON_STRIKER_SCORE = 3 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.CURRENT_TEAM_SCORE = 3 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 3 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    switchStriker()
                    showScreenContent()
                    updateNonStrikerScore()
                    setBallScore(3,"score")
                }
            }

            R.id.fourRuns -> {
                if (batsman_1_card.isChecked) {
                    showBall("4")
                    GlobalVariable.STRIKER_SCORE = 4 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_NUM_FOUR = 1 + GlobalVariable.STRIKER_NUM_FOUR
                    GlobalVariable.CURRENT_TEAM_SCORE = 4 + GlobalVariable.CURRENT_TEAM_SCORE

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 4 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    GlobalVariable.num_fours_conceded = 1 + GlobalVariable.num_fours_conceded

                    showScreenContent()
                    updateStrikerFourCount()
                    setBallScore(4,"score")
                } else {
                    showBall("4")
                    GlobalVariable.NON_STRIKER_SCORE = 4 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.CURRENT_TEAM_SCORE = 4 + GlobalVariable.CURRENT_TEAM_SCORE

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 4 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    GlobalVariable.num_fours_conceded = 1 + GlobalVariable.num_fours_conceded

                    showScreenContent()
                    updateNonStrikerFourCount()
                    setBallScore(4,"score")
                }
            }
            R.id.sixRuns -> {
                if (batsman_1_card.isChecked) {
                    showBall("6")
                    GlobalVariable.STRIKER_SCORE = 6 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_NUM_SIX = 1 + GlobalVariable.STRIKER_NUM_SIX
                    GlobalVariable.CURRENT_TEAM_SCORE = 6 + GlobalVariable.CURRENT_TEAM_SCORE

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 6 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    runs_concede.text = GlobalVariable.BOWLER_RUNS_CONCEDED.toString()
                    GlobalVariable.num_sixes_conceded = 1 + GlobalVariable.num_sixes_conceded

                    showScreenContent()
                    updateStrikerSixCount()
                    setBallScore(6,"score")
                } else {
                    showBall("6")
                    GlobalVariable.NON_STRIKER_SCORE = 6 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.NON_STRIKER_NUM_SIX = 1 + GlobalVariable.NON_STRIKER_NUM_SIX
                    GlobalVariable.CURRENT_TEAM_SCORE = 6 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 6 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    GlobalVariable.num_sixes_conceded = 1 + GlobalVariable.num_sixes_conceded

                    showScreenContent()
                    updateNonStrikerSixCount()
                    setBallScore(6,"score")
                }
            }

            R.id.wideBall -> {
                showBall("Wd")
                GlobalVariable.BOWLER_Wide_ball = 1 + GlobalVariable.BOWLER_Wide_ball
                GlobalVariable.Wides = 1 + GlobalVariable.Wides
                GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE
                GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED
                showScreenContent()
                setBallScore(1,"wide")
                val db_Ref = FirebaseDatabase.getInstance().reference
                val newData = HashMap<String, Any>()
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/Extras/Wides"] =
                    GlobalVariable.Wides
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/no_wide"] =
                    GlobalVariable.BOWLER_Wide_ball
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
                    GlobalVariable.BOWLER_RUNS_CONCEDED
                db_Ref.updateChildren(newData).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("synced")
                    }
                }

            }


            R.id.noBall -> {

                showBall("N")
                GlobalVariable.NoBalls = 1 + GlobalVariable.NoBalls
                GlobalVariable.BOWLER_No_ball = 1 + GlobalVariable.BOWLER_No_ball
                GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE
                GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED

                showScreenContent()
                setBallScore(1,"no_ball")
                val db_Ref = FirebaseDatabase.getInstance().reference
                val newData = HashMap<String, Any>()
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/Extras/NoBalls"] =
                    GlobalVariable.NoBalls
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/no_ball"] =
                    GlobalVariable.BOWLER_No_ball
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
                    GlobalVariable.BOWLER_RUNS_CONCEDED
                db_Ref.updateChildren(newData).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("synced")
                    }
                }


            }

            R.id.byeRuns -> {
                showBall("B")
                GlobalVariable.Byes = 1 + GlobalVariable.Byes
                GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED
                GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE
                showScreenContent()
                setBallScore(1,"bye")
                val db_Ref = FirebaseDatabase.getInstance().reference
                val newData = HashMap<String, Any>()
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/Extras/Byes"] =
                    GlobalVariable.Byes
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
                    GlobalVariable.BOWLER_RUNS_CONCEDED
                db_Ref.updateChildren(newData).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("synced")
                    }
                }

            }


            R.id.legByes -> {

                showBall("LB")
                GlobalVariable.LegByes = 1 + GlobalVariable.LegByes
                GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED
                GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE

                showScreenContent()
                setBallScore(1,"leg_bye")
                val db_Ref = FirebaseDatabase.getInstance().reference
                val newData = HashMap<String, Any>()
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/Extras/LegByes"] =
                    GlobalVariable.LegByes
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
                    GlobalVariable.BOWLER_RUNS_CONCEDED
                db_Ref.updateChildren(newData).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("synced")
                    }
                }


            }


            R.id.out_button_MatchScoringActivity -> {

                if (GlobalVariable.TEAM_WICKET==GlobalVariable.BATTING_TEAM_Squad_Count-1)
                {
                    toast("Inning Completed")
                    //ShowPopUpDialog
                }
                showScreenContent()
                if (batsman_1_card.isChecked) {
                    setOutPlayer(
                        GlobalVariable.STRIKER_ID,
                        GlobalVariable.STRIKER_NAME,
                        R.id.batsman_1_card
                    )
                } else {
                    setOutPlayer(
                        GlobalVariable.NON_STRIKER_ID,
                        GlobalVariable.NON_STRIKER_NAME,
                        R.id.batsman_2_card
                    )
                }

            }

        }

    }

    private fun switchStriker() {
        if (batsman_1_card.isChecked) {
            batsman_1_card.isChecked = false
            batsman_2_card.isChecked = true
        } else {
            batsman_1_card.isChecked = true
            batsman_2_card.isChecked = false

        }
    }

    fun updateFirstBowlerData(b_id: String){

            firstBowlerData.add((b_id))
            firstBowlerData.add(GlobalVariable.BOWLER_NAME)
            firstBowlerData.add(GlobalVariable.BOWLER_OVERS)
            firstBowlerData.add(GlobalVariable.BOWLER_MAIDEN)
            firstBowlerData.add(GlobalVariable.BOWLER_WICKET)
            firstBowlerData.add(GlobalVariable.BOWLER_Wide_ball)
            firstBowlerData.add(GlobalVariable.BOWLER_No_ball)
            firstBowlerData.add(GlobalVariable.BOWLER_ECONEMY)
            firstBowlerData.add(GlobalVariable.num_fours_conceded)
            firstBowlerData.add(GlobalVariable.num_sixes_conceded)
            firstBowlerData.add(GlobalVariable.BOWLER_BALLS_BOWLED)
            firstBowlerData.add(GlobalVariable.BOWLER_RUNS_CONCEDED)
            firstBowlerData.add(GlobalVariable.DOT_BALLS_BOWLED)

    }


    fun updateSecondBowlerData(b_id:String){
        secondBowlerData.add(b_id)
        secondBowlerData.add(GlobalVariable.BOWLER_NAME)
        secondBowlerData.add(GlobalVariable.BOWLER_OVERS)
        secondBowlerData.add(GlobalVariable.BOWLER_MAIDEN)
        secondBowlerData.add(GlobalVariable.BOWLER_WICKET)
        secondBowlerData.add(GlobalVariable.BOWLER_Wide_ball)
        secondBowlerData.add(GlobalVariable.BOWLER_No_ball)
        secondBowlerData.add(GlobalVariable.BOWLER_ECONEMY)
        secondBowlerData.add(GlobalVariable.num_fours_conceded)
        secondBowlerData.add(GlobalVariable.num_sixes_conceded)
        secondBowlerData.add(GlobalVariable.BOWLER_BALLS_BOWLED)
        secondBowlerData.add(GlobalVariable.BOWLER_RUNS_CONCEDED)
        secondBowlerData.add(GlobalVariable.DOT_BALLS_BOWLED)

    }

    fun getBowlerData(b_id: String){
        if(firstBowlerData[0]==b_id){
            GlobalVariable.BOWLER_NAME=firstBowlerData[1].toString()
            GlobalVariable.BOWLER_OVERS=firstBowlerData[2].toString().toInt()
            GlobalVariable.BOWLER_MAIDEN=firstBowlerData[3].toString().toInt()
            GlobalVariable.BOWLER_WICKET=firstBowlerData[4].toString().toInt()
            GlobalVariable.BOWLER_Wide_ball=firstBowlerData[5].toString().toInt()
            GlobalVariable.BOWLER_No_ball=firstBowlerData[6].toString().toInt()
            GlobalVariable.BOWLER_ECONEMY=firstBowlerData[7].toString().toFloat()
            GlobalVariable.num_fours_conceded=firstBowlerData[8].toString().toInt()
            GlobalVariable.num_sixes_conceded=firstBowlerData[9].toString().toInt()
            GlobalVariable.BOWLER_BALLS_BOWLED=firstBowlerData[10].toString().toInt()
            GlobalVariable.BOWLER_RUNS_CONCEDED=firstBowlerData[11].toString().toInt()
            GlobalVariable.DOT_BALLS_BOWLED=firstBowlerData[12].toString().toInt()
        }
        else if (secondBowlerData[0]==b_id){
            GlobalVariable.BOWLER_NAME=firstBowlerData[1].toString()
            GlobalVariable.BOWLER_OVERS=firstBowlerData[2].toString().toInt()
            GlobalVariable.BOWLER_MAIDEN=firstBowlerData[3].toString().toInt()
            GlobalVariable.BOWLER_WICKET=firstBowlerData[4].toString().toInt()
            GlobalVariable.BOWLER_Wide_ball=firstBowlerData[5].toString().toInt()
            GlobalVariable.BOWLER_No_ball=firstBowlerData[6].toString().toInt()
            GlobalVariable.BOWLER_ECONEMY=firstBowlerData[7].toString().toFloat()
            GlobalVariable.num_fours_conceded=firstBowlerData[8].toString().toInt()
            GlobalVariable.num_sixes_conceded=firstBowlerData[9].toString().toInt()
            GlobalVariable.BOWLER_BALLS_BOWLED=firstBowlerData[10].toString().toInt()
            GlobalVariable.BOWLER_RUNS_CONCEDED=firstBowlerData[11].toString().toInt()
            GlobalVariable.DOT_BALLS_BOWLED=firstBowlerData[12].toString().toInt()
        }else
        {
          //  GlobalVariable.BOWLER_NAME = name
            GlobalVariable.BOWLER_ID = b_id
            GlobalVariable.BOWLER_BALLS_BOWLED = 0
            GlobalVariable.BOWLER_RUNS_CONCEDED = 0
            GlobalVariable.BOWLER_OVERS = 0
            GlobalVariable.BOWLER_WICKET = 0
            GlobalVariable.BOWLER_MAIDEN = 0
            GlobalVariable.BOWLER_No_ball = 0
            GlobalVariable.BOWLER_Wide_ball = 0
            GlobalVariable.num_fours_conceded = 0
            GlobalVariable.num_sixes_conceded= 0
            GlobalVariable.DOT_BALLS_BOWLED= 0
            GlobalVariable.BOWLER_ECONEMY= 0f
        }
    }

    fun showInningCompletePopUp(){
        val inningPopUpDialog = Dialog(this)
        inningPopUpDialog.setCancelable(true)
        val view = layoutInflater.inflate(R.layout.inning_completion_pop_up_dialogue, null)
        inningPopUpDialog.setContentView(view)
        val runs=view.find<TextView>(R.id.batting_teamScore_inning_end_popup)
        val overs=view.find<TextView>(R.id.batting_team_over_played_popup)
        val over_balls=view.find<TextView>(R.id.batting_team_over_balls_played_popup)
        val wickets=view.find<TextView>(R.id.batting_team_wickets_popup)
        val extras=view.find<TextView>(R.id.batting_team_extras_popup)
        val nextInningButton=view.find<Button>(R.id.next_inning_button_popUp)
    runs.text=GlobalVariable.CURRENT_TEAM_SCORE.toString()
    overs.text=GlobalVariable.Current_Over.toString()
    over_balls.text=GlobalVariable.CURRENT_BALL.toString()
    wickets.text=GlobalVariable.TEAM_WICKET.toString()
    extras.text=GlobalVariable.TEAM_Extras.toString()

        inningPopUpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        inningPopUpDialog.show()
    }


    private fun showBall(runs: String) {
        if (runs=="Wd"||runs=="N"){
            updateOvers()
            updateLiveData?.updataData()
        }else{
            GlobalVariable.CURRENT_BALL = 1 + GlobalVariable.CURRENT_BALL
            GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED
            updateOvers()
            updateLiveData?.updataData()
        }




        GlobalVariable.TEAM_CRR =
            (GlobalVariable.CURRENT_TEAM_SCORE / (GlobalVariable.Current_Over).toFloat() + (((GlobalVariable.CURRENT_BALL).toFloat()) / 6.0)).toFloat()

        if (GlobalVariable.CURRENT_BALL == 6) {
            GlobalVariable.Current_Over = 1 + GlobalVariable.Current_Over
            GlobalVariable.BOWLER_OVERS = 1 + GlobalVariable.BOWLER_OVERS

            val db_Ref = FirebaseDatabase.getInstance().reference
            val newData = HashMap<String, Any>()
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/bowler_overs"] =
                GlobalVariable.BOWLER_OVERS
            db_Ref.updateChildren(newData).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    toast("Over Completed")
                }
            }


            //if (firstBowlerData[0]==GlobalVariable.BOWLER_ID){
           //     updateFirstBowlerData(GlobalVariable.BOWLER_ID)
           // }
            //else if (secondBowlerData[0]==GlobalVariable)


            if(GlobalVariable.Current_Over==GlobalVariable.MATCH_OVERS)
            {
                toast("Inning Completed")
                showInningCompletePopUp()

                //showPopUp
            }
            startActivityForResult<TeamsPlayerReadyToPlayMatch>(
                newBowler_RC,
                "teamId" to GlobalVariable.BOWLING_TEAM_ID,
                "newMatchId" to GlobalVariable.MATCH_ID
            )

            switchStriker()
            GlobalVariable.BOWLER_BALLS_BOWLED = 0
            GlobalVariable.CURRENT_BALL = 0
            ballAdapter.clear()
            //showPopUp
        } else {
            ballAdapter.add(ScoreBall(runs))
        }

    }

    class ScoreBall(val runs: String) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.this_over_card
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val btn = viewHolder.itemView.find<Button>(R.id.run_at_a_ball)

            btn.text = runs.toString()

        }

    }


}






