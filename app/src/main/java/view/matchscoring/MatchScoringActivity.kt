package view.matchscoring

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsplayer.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pawegio.kandroid.toast
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_match_scoring.*
import model.Batsman
import model.Bowler
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import view.GlobalVariable
import view.match.StartInningActivity
import view.team.TeamsPlayerReadyToPlayMatch
import java.math.RoundingMode


@Suppress("DEPRECATION", "PLUGIN_WARNING")
class MatchScoringActivity : AppCompatActivity(), View.OnClickListener {


    private var databaseReference: FirebaseDatabase? = null
    val bowlersList = ArrayList<String>()
    val ballAdapter = GroupAdapter<ViewHolder>()
    val newplayer_RC = 1
    val newBowler_RC = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_scoring)
        databaseReference = FirebaseDatabase.getInstance()
        getTeamSquadCount()

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
            batsman_1_card.isChecked = !batsman_1_card.isChecked
            if (batsman_1_card.isChecked) {
                batsman_2_card.isChecked = false
                batsman_2_card.strokeColor = resources.getColor(R.color.DarkRed)
                batsman_1_card.strokeColor = resources.getColor(R.color.BlueViolet)
            } else {
                batsman_2_card.isChecked = true
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

    override fun onStart() {
        super.onStart()
        supportActionBar?.title = GlobalVariable.BATTING_TEAM_NAME
        batsman_1_card.isChecked = true
        Log.d("overs", GlobalVariable.MATCH_OVERS.toString())
        match_total_overs.text = GlobalVariable.MATCH_OVERS.toString()
        bowlersList.add(GlobalVariable.BOWLER_ID)

    }

    override fun onResume() {
        super.onResume()
        batsman_1_name.text = GlobalVariable.STRIKER_NAME
        batsman_2_name.text = GlobalVariable.NON_STRIKER_NAME
        bowler_name.text = GlobalVariable.BOWLER_NAME

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
            GlobalVariable.this_Over_wickets = 1 + GlobalVariable.this_Over_wickets
            GlobalVariable.BOWLER_WICKET = 1 + GlobalVariable.BOWLER_WICKET
            //Partnership
            GlobalVariable.CURRENT_PARTNERSHIP_RUNS = 0
            GlobalVariable.CURRENT_PARTNERSHIP_BALLS = 0
            showBall("W")

            val db_Ref = FirebaseDatabase.getInstance().reference
            val newData = HashMap<String, Any>()
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/wickets"] =
                GlobalVariable.TEAM_WICKET
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/wickets"] =
                GlobalVariable.BOWLER_WICKET
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/CRR"] =
                GlobalVariable.TEAM_CRR
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/MatchCurrentDetail"] =
                GlobalVariable.MatchCurrentDetails
            if (GlobalVariable.Inning=="SecondInning"){
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/RRR"] =
                    GlobalVariable.TEAM_RRR}
            db_Ref.updateChildren(newData).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val db_Ref = FirebaseDatabase.getInstance().reference
                    val newData = HashMap<String, Any>()
                    newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.Current_Over}/${GlobalVariable.CURRENT_BALL}/Out"] =
                        p_id

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
                                        if (GlobalVariable.TEAM_WICKET == GlobalVariable.BATTING_TEAM_Squad_Count - 1) {
                                            toast("Inning Completed")
                                            if (GlobalVariable.Inning=="FirstInning")
                                            {showInningCompletePopUp()}
                                            else{showCompleteMatchPopUp()}
                                        }else{
                                        //START ACTIVITY TO SELECT NEW BATSMAN
                                        startActivityForResult<TeamsPlayerReadyToPlayMatch>(
                                            newplayer_RC,
                                            "teamId" to GlobalVariable.BATTING_TEAM_ID,
                                            "newMatchId" to GlobalVariable.MATCH_ID
                                        )
                                        }
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
                            GlobalVariable.STRIKER_NAME = name
                            GlobalVariable.STRIKER_SCORE = 0
                            GlobalVariable.STRIKER_BALL = 0
                            GlobalVariable.STRIKER_SINGLES = 0
                            GlobalVariable.STRIKER_doubles = 0
                            GlobalVariable.STRIKER_TRIPLES = 0
                            GlobalVariable.STRIKER_NUM_FOUR = 0
                            GlobalVariable.STRIKER_NUM_SIX = 0
                            GlobalVariable.STRIKER_SR = 00f
                            GlobalVariable.Striker_DOT_BALLS_Played = 0
                            batsman_1_name.text = GlobalVariable.STRIKER_NAME
                            batsman_1_score.text = GlobalVariable.STRIKER_SCORE.toString()
                            showScreenContent()
                            addNewBatsman(playerId)
                        } else {
                            GlobalVariable.NON_STRIKER_ID = playerId
                            GlobalVariable.NON_STRIKER_NAME = name
                            GlobalVariable.STRIKER_SCORE = 0
                            GlobalVariable.NON_STRIKER_BALL = 0
                            GlobalVariable.NON_STRIKER_SINGLES = 0
                            GlobalVariable.NON_STRIKER_doubles = 0
                            GlobalVariable.NON_STRIKER_TRIPLES = 0
                            GlobalVariable.NON_STRIKER_NUM_FOUR = 0
                            GlobalVariable.NON_STRIKER_NUM_SIX = 0
                            GlobalVariable.NON_STRIKER_SR = 00f
                            GlobalVariable.NON_Striker_DOT_BALLS_Played = 0
                            batsman_2_name.text = GlobalVariable.NON_STRIKER_NAME
                            batsman_2_score.text = GlobalVariable.NON_STRIKER_SCORE.toString()
                            showScreenContent()
                            addNewBatsman(playerId)
                        }
                    }
                }

                newBowler_RC -> {
                    val playerId = data.getStringExtra("playerId")
                    val name = data.getStringExtra("name")

                    GlobalVariable.BOWLER_NAME = name
                    GlobalVariable.BOWLER_ID = playerId
                    if (bowlersList.contains(playerId)) {
                        Log.d("NewOver", "Contains")


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
                        GlobalVariable.BowlerPosition = 1 + GlobalVariable.BowlerPosition
                        val newBowler = Bowler(0,0,0,0,0,0,0,0,0f,0,GlobalVariable.BowlerPosition)
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
                                GlobalVariable.BOWLER_ECONOMY = 00f
                                GlobalVariable.num_fours_conceded = 0
                                GlobalVariable.num_sixes_conceded = 0
                                GlobalVariable.BOWLER_BALLS_BOWLED = 0
                                GlobalVariable.BOWLER_RUNS_CONCEDED = 0
                                GlobalVariable.DOT_BALLS_BOWLED = 0
                                toast("Bowler is Ready")

                                showScreenContent()


                            }

                        }
                    }
                }
            }
        }
    }

    private fun addNewBatsman(playerId: String?) {
        GlobalVariable.BattingPosition = 1 + GlobalVariable.BattingPosition
        val batRef = FirebaseDatabase.getInstance().reference
        val bat = HashMap<String, Any>()
        val newP = Batsman(0,0,0,0,0,0,0,0,GlobalVariable.BattingPosition)
        bat["MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/$playerId"] =
            newP
        batRef.updateChildren(bat).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("New Player Added")
            }
        }
    }


    private fun setBallScore(runs: Int, extra: String) {

        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.Current_Over}/${GlobalVariable.CURRENT_BALL}/Runs"] =
            runs
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }

        when (extra) {
            "no_ball" -> {
                setExtras(1, "no_ball")

            }
            "wide" -> {
                setExtras(1, "wide")

            }
            "leg_bye" -> {
                setExtras(1, "leg_bye")

            }
            "bye" -> {
                setExtras(1, "bye")
            }
            else -> {
                setExtras(runs, extra)
            }
        }
    }

    private fun setExtras(score: Int, extra: String) {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.Current_Over}/${GlobalVariable.CURRENT_BALL}/$extra"] =
            score
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/CRR"] =
            GlobalVariable.TEAM_CRR
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/MatchCurrentDetail"] =
            GlobalVariable.MatchCurrentDetails
        if (GlobalVariable.Inning=="SecondInning"){
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/RRR"] =
                GlobalVariable.TEAM_RRR}
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
    }

    private fun updateStrikerDotBall() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/dots"] =
            GlobalVariable.Striker_DOT_BALLS_Played
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/balls_played"] =
            GlobalVariable.STRIKER_BALL
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/over_balls"] =
            GlobalVariable.CURRENT_BALL
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/CRR"] =
            GlobalVariable.TEAM_CRR
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/MatchCurrentDetail"] =
            GlobalVariable.MatchCurrentDetails
        if (GlobalVariable.Inning=="SecondInning"){
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/RRR"] =
                GlobalVariable.TEAM_RRR}
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }

    }


    private fun updateNonStrikerDotBall() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/dots"] =
            GlobalVariable.Striker_DOT_BALLS_Played
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/balls_played"] =
            GlobalVariable.NON_STRIKER_BALL
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/over_balls"] =
            GlobalVariable.CURRENT_BALL
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/CRR"] =
            GlobalVariable.TEAM_CRR
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/MatchCurrentDetail"] =
            GlobalVariable.MatchCurrentDetails
        if (GlobalVariable.Inning=="SecondInning"){
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/RRR"] =
                GlobalVariable.TEAM_RRR}
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }

    }


    private fun updateStrikerScore() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/runs"] =
            GlobalVariable.STRIKER_SCORE
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/balls_played"] =
            GlobalVariable.STRIKER_BALL
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/singles"] =
            GlobalVariable.STRIKER_SINGLES
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/doubles"] =
            GlobalVariable.STRIKER_doubles
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/triples"] =
            GlobalVariable.STRIKER_TRIPLES
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/inningScore"] =
            GlobalVariable.CURRENT_TEAM_SCORE
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/CRR"] =
            GlobalVariable.TEAM_CRR
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/MatchCurrentDetail"] =
            GlobalVariable.MatchCurrentDetails
        if (GlobalVariable.Inning=="SecondInning"){
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/RRR"] =
            GlobalVariable.TEAM_RRR}
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/over_balls"] =
            GlobalVariable.CURRENT_BALL
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/runs_conceded"] =
            GlobalVariable.BOWLER_RUNS_CONCEDED
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
    }


    private fun updateNonStrikerScore() {
        val db_Ref = FirebaseDatabase.getInstance().reference
        val newData = HashMap<String, Any>()
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/runs"] =
            GlobalVariable.NON_STRIKER_SCORE
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/balls_played"] =
            GlobalVariable.NON_STRIKER_BALL
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/singles"] =
            GlobalVariable.NON_STRIKER_SINGLES
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/doubles"] =
            GlobalVariable.NON_STRIKER_doubles
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/triples"] =
            GlobalVariable.NON_STRIKER_TRIPLES
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/inningScore"] =
            GlobalVariable.CURRENT_TEAM_SCORE
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/CRR"] =
            GlobalVariable.TEAM_CRR
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/MatchCurrentDetail"] =
            GlobalVariable.MatchCurrentDetails
        if (GlobalVariable.Inning=="SecondInning"){
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/RRR"] =
                GlobalVariable.TEAM_RRR}
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/balls_bowled"] =
            GlobalVariable.BOWLER_BALLS_BOWLED
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/over_balls"] =
            GlobalVariable.CURRENT_BALL
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
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/no_of_fours"] =
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
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/no_of_fours"] =
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
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.STRIKER_ID}/no_of_six"] =
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
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BATTING_TEAM_ID}/${GlobalVariable.NON_STRIKER_ID}/no_of_six"] =
            GlobalVariable.NON_STRIKER_NUM_SIX
        newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/num_sixes_conceded"] =
            GlobalVariable.num_sixes_conceded
        db_Ref.updateChildren(newData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("synced")
            }
        }
        //update NonStriker Data
        updateNonStrikerScore()

    }


    private fun showScreenContent() {
        //Team
        team_current_score.text = GlobalVariable.CURRENT_TEAM_SCORE.toString()
        team_current_wickets.text = GlobalVariable.TEAM_WICKET.toString()
        team_current_overs_played.text = GlobalVariable.Current_Over.toString()
        team_current_over_balls_played.text = GlobalVariable.CURRENT_BALL.toString()
        current_run_rate_match_scoring.text = GlobalVariable.TEAM_CRR.toString()


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

        if (GlobalVariable.Inning=="FirstInning"){
            required_run_rate_match_scoring.text = GlobalVariable.TEAM_RRR.toString()
            GlobalVariable.MatchCurrentDetails = "Toss Won By ${GlobalVariable.BATTING_TEAM_NAME} and Elected to do ${GlobalVariable.TossWonTeamDecidedTo} first"
            score_detail_textView.text = GlobalVariable.MatchCurrentDetails
        }
        else {
            val requiredRuns = ((GlobalVariable.FirstInningScore+1)-GlobalVariable.CURRENT_TEAM_SCORE)
            val remainingBalls = ((GlobalVariable.MATCH_OVERS*6)-((GlobalVariable.Current_Over*6)+GlobalVariable.CURRENT_BALL))
            GlobalVariable.TEAM_RRR = ((requiredRuns*6).toFloat()/(remainingBalls).toFloat()).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
            GlobalVariable.MatchCurrentDetails = "${GlobalVariable.BATTING_TEAM_NAME} Needs ${requiredRuns} runs from ${remainingBalls}"
            score_detail_textView.text = GlobalVariable.MatchCurrentDetails
            required_run_rate_match_scoring.text = GlobalVariable.TEAM_RRR.toString()
        }


    }


    private fun getTeamSquadCount() {
        val squadRef = FirebaseDatabase.getInstance()
            .getReference("/MatchInfo/${GlobalVariable.MATCH_ID}/${GlobalVariable.TeamSquad}")
        squadRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    GlobalVariable.BATTING_TEAM_Squad_Count = p0.childrenCount.toInt()
                }
            }
        })
    }


    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.ZeroRun -> {
                if (batsman_1_card.isChecked) {
                    setBallScore(0, "Runs")

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


                    showBall("0")
                    //Update Striker Dot Ball
                    updateStrikerDotBall()


                } else {
                    setBallScore(0, "Runs")

                    //Team
                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    //Batsman
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.NON_Striker_DOT_BALLS_Played =
                        1 + GlobalVariable.NON_Striker_DOT_BALLS_Played
                    //Bowler
                    GlobalVariable.DOT_BALLS_BOWLED = 1 + GlobalVariable.DOT_BALLS_BOWLED


                    showBall("0")
                    //Update NonStriker Dot ball
                    updateNonStrikerDotBall()

                }
            }
            R.id.oneRun -> {

                if (batsman_1_card.isChecked) {
                    setBallScore(1, "Runs")
                    //Team

                    GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.this_Over_runs = 1 + GlobalVariable.this_Over_runs


                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    //Batsman
                    GlobalVariable.STRIKER_SCORE = 1 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_SINGLES = 1 + GlobalVariable.STRIKER_SINGLES
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL

                    //Bowler
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED


                    switchStriker()
                    showBall("1")
                    updateStrikerScore()


                } else {
                    setBallScore(1, "Runs")
                    //Team
                    GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.this_Over_runs = 1 + GlobalVariable.this_Over_runs

                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    //Batsman
                    GlobalVariable.NON_STRIKER_SCORE = 1 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_SINGLES = 1 + GlobalVariable.NON_STRIKER_SINGLES
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL

                    //Bowler
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED


                    switchStriker()
                    showBall("1")
                    updateNonStrikerScore()

                }
            }
            R.id.twoRuns -> {
                if (batsman_1_card.isChecked) {
                    setBallScore(2, "Runs")
                    //Team

                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                        2 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    GlobalVariable.CURRENT_TEAM_SCORE = 2 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.this_Over_runs = 2 + GlobalVariable.this_Over_runs
                    //Batsman
                    GlobalVariable.STRIKER_SCORE = 2 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_doubles = 1 + GlobalVariable.STRIKER_doubles

                    //Bowler
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 2 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    showBall("2")
                    updateStrikerScore()

                } else {
                    setBallScore(2, "Runs")

                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                        2 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    GlobalVariable.NON_STRIKER_SCORE = 2 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.CURRENT_TEAM_SCORE = 2 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.this_Over_runs = 2 + GlobalVariable.this_Over_runs
                    GlobalVariable.NON_STRIKER_doubles = 1 + GlobalVariable.NON_STRIKER_doubles

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 2 + GlobalVariable.BOWLER_RUNS_CONCEDED


                    showBall("2")
                    updateNonStrikerScore()

                }
            }
            R.id.threeRuns -> {
                if (batsman_1_card.isChecked) {
                    setBallScore(3, "Runs")

                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                        3 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    GlobalVariable.STRIKER_SCORE = 3 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_TRIPLES = 1 + GlobalVariable.STRIKER_TRIPLES
                    GlobalVariable.CURRENT_TEAM_SCORE = 3 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.this_Over_runs = 3 + GlobalVariable.this_Over_runs
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 3 + GlobalVariable.BOWLER_RUNS_CONCEDED

                    switchStriker()
                    showBall("3")
                    updateStrikerScore()

                } else {
                    setBallScore(3, "Runs")

                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                        3 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    GlobalVariable.NON_STRIKER_TRIPLES = 1 + GlobalVariable.NON_STRIKER_TRIPLES
                    GlobalVariable.NON_STRIKER_SCORE = 3 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.CURRENT_TEAM_SCORE = 3 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.this_Over_runs = 3 + GlobalVariable.this_Over_runs
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 3 + GlobalVariable.BOWLER_RUNS_CONCEDED


                    switchStriker()
                    showBall("3")
                    updateNonStrikerScore()

                }
            }

            R.id.fourRuns -> {
                if (batsman_1_card.isChecked) {
                    setBallScore(4, "Runs")

                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                        4 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    GlobalVariable.STRIKER_SCORE = 4 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_NUM_FOUR = 1 + GlobalVariable.STRIKER_NUM_FOUR
                    GlobalVariable.CURRENT_TEAM_SCORE = 4 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.this_Over_runs = 4 + GlobalVariable.this_Over_runs

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 4 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    GlobalVariable.num_fours_conceded = 1 + GlobalVariable.num_fours_conceded


                    showBall("4")
                    updateStrikerFourCount()

                } else {
                    setBallScore(4, "Runs")
                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                        4 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    GlobalVariable.NON_STRIKER_SCORE = 4 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.NON_STRIKER_NUM_FOUR = 1 + GlobalVariable.NON_STRIKER_NUM_FOUR
                    GlobalVariable.CURRENT_TEAM_SCORE = 4 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.this_Over_runs = 4 + GlobalVariable.this_Over_runs

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 4 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    GlobalVariable.num_fours_conceded = 1 + GlobalVariable.num_fours_conceded



                    showBall("4")
                    updateNonStrikerFourCount()

                }
            }
            R.id.sixRuns -> {
                if (batsman_1_card.isChecked) {
                    setBallScore(6, "Runs")

                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                        6 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    GlobalVariable.STRIKER_SCORE = 6 + GlobalVariable.STRIKER_SCORE
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                    GlobalVariable.STRIKER_NUM_SIX = 1 + GlobalVariable.STRIKER_NUM_SIX
                    GlobalVariable.CURRENT_TEAM_SCORE = 6 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.this_Over_runs = 6 + GlobalVariable.this_Over_runs

                    GlobalVariable.BOWLER_RUNS_CONCEDED = 6 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    runs_concede.text = GlobalVariable.BOWLER_RUNS_CONCEDED.toString()
                    GlobalVariable.num_sixes_conceded = 1 + GlobalVariable.num_sixes_conceded


                    showBall("6")
                    updateStrikerSixCount()

                } else {
                    setBallScore(6, "Runs")

                    //Partnership
                    GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                        6 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                    GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                        1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                    GlobalVariable.NON_STRIKER_SCORE = 6 + GlobalVariable.NON_STRIKER_SCORE
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                    GlobalVariable.NON_STRIKER_NUM_SIX = 1 + GlobalVariable.NON_STRIKER_NUM_SIX
                    GlobalVariable.CURRENT_TEAM_SCORE = 6 + GlobalVariable.CURRENT_TEAM_SCORE
                    GlobalVariable.this_Over_runs = 6 + GlobalVariable.this_Over_runs
                    GlobalVariable.BOWLER_RUNS_CONCEDED = 6 + GlobalVariable.BOWLER_RUNS_CONCEDED
                    GlobalVariable.num_sixes_conceded = 1 + GlobalVariable.num_sixes_conceded


                    showBall("6")
                    updateNonStrikerSixCount()

                }
            }

            R.id.wideBall -> {
                setBallScore(1, "wide")
                //Partnership
                GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                    1 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS

                GlobalVariable.BOWLER_Wide_ball = 1 + GlobalVariable.BOWLER_Wide_ball
                GlobalVariable.Wides = 1 + GlobalVariable.Wides
                GlobalVariable.TEAM_Extras = 1 + GlobalVariable.TEAM_Extras
                GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE
                GlobalVariable.this_Over_extras = 1 + GlobalVariable.this_Over_extras
                GlobalVariable.this_Over_runs = 1 + GlobalVariable.this_Over_runs
                GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED

                showBall("Wd")

                val db_Ref = FirebaseDatabase.getInstance().reference
                val newData = HashMap<String, Any>()
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/extras/Wides"] =
                    GlobalVariable.Wides
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/no_of_wide_balls_by_bowler"] =
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
                setBallScore(1, "no_ball")


                //Partnership
                GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                    1 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                GlobalVariable.CURRENT_PARTNERSHIP_BALLS = 1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                GlobalVariable.NoBalls = 1 + GlobalVariable.NoBalls
                GlobalVariable.TEAM_Extras = 1 + GlobalVariable.TEAM_Extras
                GlobalVariable.BOWLER_No_ball = 1 + GlobalVariable.BOWLER_No_ball
                GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE
                GlobalVariable.this_Over_runs = 1 + GlobalVariable.this_Over_runs
                GlobalVariable.this_Over_extras = 1 + GlobalVariable.this_Over_extras
                GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED

                if (batsman_1_card.isChecked) {
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                } else {
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                }
                showBall("N")
                val db_Ref = FirebaseDatabase.getInstance().reference
                val newData = HashMap<String, Any>()
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/extras/NoBalls"] =
                    GlobalVariable.NoBalls
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/no_of_no_balls_by_bowler"] =
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
                setBallScore(1, "bye")


                //Partnership
                GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                    1 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                    1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                GlobalVariable.Byes = 1 + GlobalVariable.Byes
                GlobalVariable.TEAM_Extras = 1 + GlobalVariable.TEAM_Extras
                GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED
                GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE
                GlobalVariable.this_Over_runs = 1 + GlobalVariable.this_Over_runs
                GlobalVariable.this_Over_extras = 1 + GlobalVariable.this_Over_extras

                if (batsman_1_card.isChecked) {
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                } else {
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                }


                showBall("B")
                val db_Ref = FirebaseDatabase.getInstance().reference
                val newData = HashMap<String, Any>()
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/extras/Byes"] =
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
                setBallScore(1, "leg_bye")

                //Partnership
                GlobalVariable.CURRENT_PARTNERSHIP_RUNS =
                    1 + GlobalVariable.CURRENT_PARTNERSHIP_RUNS
                GlobalVariable.CURRENT_PARTNERSHIP_BALLS =
                    1 + GlobalVariable.CURRENT_PARTNERSHIP_BALLS

                GlobalVariable.LegByes = 1 + GlobalVariable.LegByes
                GlobalVariable.TEAM_Extras = 1 + GlobalVariable.TEAM_Extras
                GlobalVariable.BOWLER_RUNS_CONCEDED = 1 + GlobalVariable.BOWLER_RUNS_CONCEDED
                GlobalVariable.CURRENT_TEAM_SCORE = 1 + GlobalVariable.CURRENT_TEAM_SCORE
                GlobalVariable.this_Over_runs = 1 + GlobalVariable.this_Over_runs
                GlobalVariable.this_Over_extras = 1 + GlobalVariable.this_Over_extras

                if (batsman_1_card.isChecked) {
                    GlobalVariable.STRIKER_BALL = 1 + GlobalVariable.STRIKER_BALL
                } else {
                    GlobalVariable.NON_STRIKER_BALL = 1 + GlobalVariable.NON_STRIKER_BALL
                }


                showBall("LB")

                val db_Ref = FirebaseDatabase.getInstance().reference
                val newData = HashMap<String, Any>()
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/extras/LegByes"] =
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

                showScreenContent()
                if (batsman_1_card.isChecked) {
                    showRemovePopUpDialog(
                        GlobalVariable.STRIKER_ID,
                        GlobalVariable.STRIKER_NAME,
                        R.id.batsman_1_card
                    )


                } else {
                    showRemovePopUpDialog(
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

    private fun showCompleteMatchPopUp(){
        val completeMatchPopUp = Dialog(this)
        completeMatchPopUp.setCancelable(false)
        completeMatchPopUp.setCanceledOnTouchOutside(false)

        val view = layoutInflater.inflate(R.layout.complete_match_pop_up_dialouge,null)
        completeMatchPopUp.setContentView(view)

        val firstInningTeamName = view.find<TextView>(R.id.team_A_Name_match_complete_pop_up)
        val firstInningTeamScore = view.find<TextView>(R.id.team_A_Runs_match_complete_pop_up)
        val firstInningTeamWickets= view.find<TextView>(R.id.team_A_wickets_match_complete_pop_up)
        val firstInningTeamOvers = view.find<TextView>(R.id.team_A_overs_match_complete_pop_up)
        val firstInningTeamOverBalls= view.find<TextView>(R.id.team_A_over_balls_match_complete_pop_up)

        val secondInningTeamName = view.find<TextView>(R.id.team_B_Name_match_complete_pop_up)
        val secondInningTeamScore = view.find<TextView>(R.id.team_B_Runs_match_complete_pop_up)
        val secondInningTeamWickets= view.find<TextView>(R.id.team_B_wickets_match_complete_pop_up)
        val secondInningTeamOvers = view.find<TextView>(R.id.team_B_overs_match_complete_pop_up)
        val secondInningTeamOverBalls= view.find<TextView>(R.id.team_B_over_Balls_match_complete_pop_up)

        val winningTeamName = view.find<TextView>(R.id.winner_name)
        val winningMargin = view.find<TextView>(R.id.margin_of_victory)
        val winningBy = view.find<TextView>(R.id.victory_by)
        val saveGameButton = view.find<Button>(R.id.save_game_complete_match_pop_up)

        firstInningTeamName.text = GlobalVariable.BOWLING_TEAM_NAME
        firstInningTeamScore.text = GlobalVariable.FirstInningScore.toString()
        firstInningTeamWickets.text = GlobalVariable.FirstInningWickets.toString()
        firstInningTeamOvers.text = GlobalVariable.FirstInningOversPlayed.toString()
        firstInningTeamOverBalls.text = GlobalVariable.FirstInningOverBallsPlayed.toString()

        secondInningTeamName.text = GlobalVariable.BATTING_TEAM_NAME
        secondInningTeamScore.text = GlobalVariable.CURRENT_TEAM_SCORE.toString()
        secondInningTeamWickets.text = GlobalVariable.TEAM_WICKET.toString()
        secondInningTeamOvers.text = GlobalVariable.Current_Over.toString()
        secondInningTeamOverBalls.text = GlobalVariable.CURRENT_BALL.toString()

        when {
            GlobalVariable.FirstInningScore>GlobalVariable.CURRENT_TEAM_SCORE -> {
                winningTeamName.text = GlobalVariable.BOWLING_TEAM_NAME
                winningMargin.text = (GlobalVariable.FirstInningScore - GlobalVariable.CURRENT_TEAM_SCORE).toString()
                winningBy.text = "runs"
            }
            GlobalVariable.FirstInningScore==GlobalVariable.CURRENT_TEAM_SCORE -> {
                winningTeamName.text = "Match is Tied"
                winningMargin.visibility = View.GONE
                winningBy.visibility = View.GONE
            }
            else -> {
                winningTeamName.text = GlobalVariable.BATTING_TEAM_NAME
                winningMargin.text = ((GlobalVariable.BATTING_TEAM_Squad_Count-1)-GlobalVariable.TEAM_WICKET).toString()
                winningBy.text = "wickets"
            }
        }

   //     saveGameButton.setOnClickListener {  }

        completeMatchPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        completeMatchPopUp.show()

    }

    private fun showInningCompletePopUp() {
        val inningPopUpDialog = Dialog(this)

        inningPopUpDialog.setCancelable(false)
        inningPopUpDialog.setCanceledOnTouchOutside(false)
        val view = layoutInflater.inflate(R.layout.inning_completion_pop_up_dialogue, null)
        inningPopUpDialog.setContentView(view)
        val battingTeamName = view.find<TextView>(R.id.batting_team_name_inning_complete_card)
        val battingTeamLogo = view.find<ImageView>(R.id.first_inningTeam_logo_Inning_end_popup)
        val runs = view.find<TextView>(R.id.batting_teamScore_inning_end_popup)
        val overs = view.find<TextView>(R.id.batting_team_over_played_popup)
        val over_balls = view.find<TextView>(R.id.batting_team_over_balls_played_popup)
        val wickets = view.find<TextView>(R.id.batting_team_wickets_popup)
        val extras = view.find<TextView>(R.id.batting_team_extras_popup)
        val nextInningButton = view.find<Button>(R.id.next_inning_button_popUp)
        battingTeamName.text = GlobalVariable.BATTING_TEAM_NAME
        Picasso.get().load(GlobalVariable.BATTING_TEAM_LOGO).into(battingTeamLogo)
        runs.text = GlobalVariable.CURRENT_TEAM_SCORE.toString()
        overs.text = GlobalVariable.Current_Over.toString()
        over_balls.text = GlobalVariable.CURRENT_BALL.toString()
        wickets.text = GlobalVariable.TEAM_WICKET.toString()
        extras.text = GlobalVariable.TEAM_Extras.toString()
        nextInningButton.setOnClickListener {

            GlobalVariable.FirstInningScore = GlobalVariable.CURRENT_TEAM_SCORE
            GlobalVariable.FirstInningWickets = GlobalVariable.TEAM_WICKET
            GlobalVariable.FirstInningOversPlayed = GlobalVariable.Current_Over
            GlobalVariable.FirstInningOverBallsPlayed= GlobalVariable.CURRENT_BALL
            GlobalVariable.Inning = "SecondInning"

            val temp = GlobalVariable.BATTING_TEAM_ID
            GlobalVariable.BATTING_TEAM_ID = GlobalVariable.BOWLING_TEAM_ID
            GlobalVariable.BOWLING_TEAM_ID = temp

            val temp2 = GlobalVariable.BATTING_TEAM_NAME
            GlobalVariable.BATTING_TEAM_NAME = GlobalVariable.BOWLING_TEAM_NAME
            GlobalVariable.BOWLING_TEAM_NAME = temp2
            GlobalVariable.TeamSquad = "team_B_Squad"
            GlobalVariable.Wides = 0
            GlobalVariable.NoBalls = 0
            GlobalVariable.Byes = 0
            GlobalVariable.LegByes = 0
            GlobalVariable.BATTING_TEAM_Squad_Count = 0
            GlobalVariable.BattingPosition = 1
            GlobalVariable.BowlerPosition = 1

            GlobalVariable.CURRENT_BALL = 0
            GlobalVariable.Current_Over = 0
            GlobalVariable.CURRENT_TEAM_SCORE = 0
            GlobalVariable.TEAM_Extras = 0
            GlobalVariable.TEAM_WICKET = 0

            GlobalVariable.TEAM_CRR = 00f
            GlobalVariable.TEAM_RRR = 00f

            GlobalVariable.STRIKER_ID = ""
            GlobalVariable.STRIKER_NAME = ""
            GlobalVariable.STRIKER_SCORE = 0
            GlobalVariable.STRIKER_BALL = 0
            GlobalVariable.STRIKER_SINGLES = 0
            GlobalVariable.STRIKER_doubles = 0
            GlobalVariable.STRIKER_TRIPLES = 0
            GlobalVariable.STRIKER_NUM_FOUR = 0
            GlobalVariable.STRIKER_NUM_SIX = 0
            GlobalVariable.STRIKER_SR = 00f
            GlobalVariable.Striker_DOT_BALLS_Played = 0

            GlobalVariable.NON_STRIKER_ID = ""
            GlobalVariable.NON_STRIKER_NAME = ""
            GlobalVariable.NON_STRIKER_SCORE = 0
            GlobalVariable.NON_STRIKER_BALL = 0
            GlobalVariable.NON_STRIKER_SINGLES = 0
            GlobalVariable.NON_STRIKER_doubles = 0
            GlobalVariable.NON_STRIKER_TRIPLES = 0
            GlobalVariable.NON_STRIKER_NUM_FOUR = 0
            GlobalVariable.NON_STRIKER_NUM_SIX = 0
            GlobalVariable.NON_STRIKER_SR = 00f
            GlobalVariable.NON_Striker_DOT_BALLS_Played = 0

            GlobalVariable.CURRENT_PARTNERSHIP_BALLS = 0
            GlobalVariable.CURRENT_PARTNERSHIP_RUNS = 0

            GlobalVariable.BOWLER_ID = ""
            GlobalVariable.BOWLER_NAME = ""
            GlobalVariable.BOWLER_OVERS = 0
            GlobalVariable.BOWLER_MAIDEN = 0
            GlobalVariable.BOWLER_WICKET = 0
            GlobalVariable.BOWLER_Wide_ball = 0
            GlobalVariable.BOWLER_No_ball = 0
            GlobalVariable.BOWLER_ECONOMY = 00f
            GlobalVariable.num_fours_conceded = 0
            GlobalVariable.num_sixes_conceded = 0
            GlobalVariable.BOWLER_BALLS_BOWLED = 0
            GlobalVariable.BOWLER_RUNS_CONCEDED = 0
            GlobalVariable.DOT_BALLS_BOWLED = 0

            GlobalVariable.this_Over_wickets = 0
            GlobalVariable.this_Over_extras = 0
            GlobalVariable.this_Over_runs = 0

            GlobalVariable.found = false

            val requiredRuns = ((GlobalVariable.FirstInningScore+1)-GlobalVariable.CURRENT_TEAM_SCORE)
            val remainingBalls = ((GlobalVariable.MATCH_OVERS*6)-((GlobalVariable.Current_Over*6)+GlobalVariable.CURRENT_BALL))
            GlobalVariable.TEAM_RRR = ((requiredRuns*6).toFloat()/(remainingBalls).toFloat())
            GlobalVariable.MatchCurrentDetails = "${GlobalVariable.BATTING_TEAM_NAME} Needs ${requiredRuns} runs from ${remainingBalls}"
            score_detail_textView.text = GlobalVariable.MatchCurrentDetails
            required_run_rate_match_scoring.text = GlobalVariable.TEAM_RRR.toString()



            startActivity<StartInningActivity>(
                "battingTeamId" to GlobalVariable.BATTING_TEAM_ID,
                "battingTeamName" to GlobalVariable.BATTING_TEAM_NAME,
                "newMatchId" to GlobalVariable.MATCH_ID,
                "teamA_Id" to GlobalVariable.BATTING_TEAM_ID,
                "teamB_Id" to GlobalVariable.BOWLING_TEAM_ID,
                "tossWonTeamElectedTo" to GlobalVariable.CURRENT_TEAM_SCORE.toString()
            )
            inningPopUpDialog.dismiss()
            finish()
        }
        inningPopUpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        inningPopUpDialog.show()
    }

    private fun showOverSummaryPopUp() {
        val overSummaryPopUpDialog = Dialog(this)
        overSummaryPopUpDialog.setCancelable(false)
        overSummaryPopUpDialog.setCanceledOnTouchOutside(false)
        val view = layoutInflater.inflate(R.layout.over_summary_pop_up_dialogue, null)
        overSummaryPopUpDialog.setContentView(view)
        val battingTeamName = view.find<TextView>(R.id.batting_team_name_over_card)
        val t_runs = view.find<TextView>(R.id.total_runs_over_card)
        val t_wickets = view.find<TextView>(R.id.team_wickets_over_card)
        val t_overs = view.find<TextView>(R.id.team_overs_over_card)
        val s_Name = view.find<TextView>(R.id.striker_name_over_card)
        val s_runs = view.find<TextView>(R.id.striker_runs_over_card)
        val s_balls = view.find<TextView>(R.id.striker_balls_played_over_card)
        val ns_Name = view.find<TextView>(R.id.non_striker_name_over_card)
        val ns_runs = view.find<TextView>(R.id.non_striker_runs_over_card)
        val ns_balls = view.find<TextView>(R.id.non_striker_balls_played_over_card)

        val bowler_name = view.find<TextView>(R.id.bowler_name_over_card)
        val bowler_overs = view.find<TextView>(R.id.bowler_overs_over_card)
        val bowler_maiden = view.find<TextView>(R.id.bowler_maiden_overs_over_card)
        val bowler_runs_conceded = view.find<TextView>(R.id.bowler_runs_conceded_over_card)
        val bowler_wickets = view.find<TextView>(R.id.bowler_wickets_over_card)

        val this_over_runs = view.find<TextView>(R.id.runs_in_over_over_card)
        val this_over_wickets = view.find<TextView>(R.id.wickets_in_over_over_card)
        val this_over_extras = view.find<TextView>(R.id.extras_in_over_over_card)

        val nextOver = view.find<Button>(R.id.start_next_over_over_card)

        battingTeamName.text = GlobalVariable.BATTING_TEAM_NAME
        t_runs.text = GlobalVariable.CURRENT_TEAM_SCORE.toString()
        t_wickets.text = GlobalVariable.TEAM_WICKET.toString()
        t_overs.text = GlobalVariable.Current_Over.toString()
        s_Name.text = GlobalVariable.STRIKER_NAME
        s_runs.text = GlobalVariable.STRIKER_SCORE.toString()
        s_balls.text = GlobalVariable.STRIKER_BALL.toString()
        ns_Name.text = GlobalVariable.NON_STRIKER_NAME
        ns_runs.text = GlobalVariable.NON_STRIKER_SCORE.toString()
        ns_balls.text = GlobalVariable.NON_STRIKER_BALL.toString()

        bowler_name.text = GlobalVariable.BOWLER_NAME
        bowler_overs.text = GlobalVariable.BOWLER_OVERS.toString()
        bowler_maiden.text = GlobalVariable.BOWLER_MAIDEN.toString()
        bowler_runs_conceded.text = GlobalVariable.BOWLER_RUNS_CONCEDED.toString()
        bowler_wickets.text = GlobalVariable.BOWLER_WICKET.toString()

        this_over_runs.text = GlobalVariable.this_Over_runs.toString()
        this_over_wickets.text = GlobalVariable.this_Over_wickets.toString()
        this_over_extras.text = GlobalVariable.this_Over_extras.toString()

        nextOver.setOnClickListener {
            startActivityForResult<TeamsPlayerReadyToPlayMatch>(
                newBowler_RC,
                "teamId" to GlobalVariable.BOWLING_TEAM_ID,
                "newMatchId" to GlobalVariable.MATCH_ID
            )

            overSummaryPopUpDialog.dismiss()
        }



        overSummaryPopUpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        overSummaryPopUpDialog.show()
    }

    private fun showBall(runs: String) {

        when (runs) {
            "Wd", "N" -> {
            }
            else -> {
                GlobalVariable.CURRENT_BALL = 1 + GlobalVariable.CURRENT_BALL
                GlobalVariable.BOWLER_BALLS_BOWLED = 1 + GlobalVariable.BOWLER_BALLS_BOWLED
            }
        }
           val value = (((GlobalVariable.CURRENT_TEAM_SCORE) * 6).toFloat() / ((GlobalVariable.Current_Over) * 6 + GlobalVariable.CURRENT_BALL).toFloat())
        //"%.2f".format(value).toDouble()
        val number2digits = String.format("%.2f", value).toFloat()
        GlobalVariable.TEAM_CRR = number2digits
        showScreenContent()

        Log.d("Overs_teamBalls", GlobalVariable.CURRENT_BALL.toString())
        Log.d("Overs", GlobalVariable.Current_Over.toString())
        Log.d("Overs_Score", GlobalVariable.CURRENT_TEAM_SCORE.toString())
        Log.d("Overs_CRR", GlobalVariable.TEAM_CRR.toString())
        Log.d("Overs_BowlerBalls", GlobalVariable.BOWLER_BALLS_BOWLED.toString())

        if (GlobalVariable.CURRENT_BALL == 6) {
            GlobalVariable.Current_Over = 1 + GlobalVariable.Current_Over
            GlobalVariable.BOWLER_OVERS = 1 + GlobalVariable.BOWLER_OVERS


            GlobalVariable.BOWLER_BALLS_BOWLED = 0
            GlobalVariable.CURRENT_BALL = 0
            showScreenContent()

            if (GlobalVariable.this_Over_runs == 0) {
                GlobalVariable.BOWLER_MAIDEN = 1 + GlobalVariable.BOWLER_MAIDEN
            }

            val db_Ref = FirebaseDatabase.getInstance().reference
            val newData = HashMap<String, Any>()
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/overs"] =
                GlobalVariable.Current_Over
            newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/bowler_overs"] =
                GlobalVariable.BOWLER_OVERS
            if (GlobalVariable.this_Over_runs == 0) {
                newData["/MatchScore/${GlobalVariable.MATCH_ID}/${GlobalVariable.Inning}/${GlobalVariable.BOWLING_TEAM_ID}/${GlobalVariable.BOWLER_ID}/maiden"] =
                    GlobalVariable.BOWLER_MAIDEN
            }
            db_Ref.updateChildren(newData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (GlobalVariable.Inning == "SecondInning" && (GlobalVariable.CURRENT_TEAM_SCORE > GlobalVariable.FirstInningScore)){
                        showCompleteMatchPopUp()
                    }
                    toast("Over Completed")
                }
            }

            if (GlobalVariable.Current_Over == GlobalVariable.MATCH_OVERS) {
                toast("Inning Completed")

                if (GlobalVariable.Inning=="FirstInning"){
                    GlobalVariable.BOWLER_BALLS_BOWLED = 0
                    GlobalVariable.CURRENT_BALL = 0
                    showInningCompletePopUp()
                }
                else{
                    showCompleteMatchPopUp()
                }

                //showPopUp
            } else {
                showOverSummaryPopUp()
            }

            switchStriker()
            GlobalVariable.this_Over_runs = 0
            GlobalVariable.this_Over_wickets = 0
            GlobalVariable.this_Over_extras = 0
            ballAdapter.clear()
            //showPopUp
        } else {
            ballAdapter.add(ScoreBall(runs, this))
            if (GlobalVariable.Inning == "SecondInning" && (GlobalVariable.CURRENT_TEAM_SCORE > GlobalVariable.FirstInningScore)){
                showCompleteMatchPopUp()
            }
        }


    }

    class ScoreBall(val runs: String, val ctx: MatchScoringActivity) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.this_over_card
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            ctx.ball_by_ball_score_recyclerView.smoothScrollToPosition(position)
            val btn = viewHolder.itemView.find<Button>(R.id.run_at_a_ball)
            when (runs) {
                "4" -> {
                    btn.setBackgroundColor(ctx.resources.getColor(R.color.Orange))
                }
                "6" -> {
                    btn.setBackgroundColor(ctx.resources.getColor(R.color.YellowGreen))
                }
                "Wd" -> {
                    btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    btn.setBackgroundColor(ctx.resources.getColor(R.color.SteelBlue))
                }
                "N" -> {
                    btn.setBackgroundColor(ctx.resources.getColor(R.color.SteelBlue))
                }
                "B" -> {
                    btn.setBackgroundColor(ctx.resources.getColor(R.color.SteelBlue))
                }
                "LB" -> {
                    btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                    btn.setBackgroundColor(ctx.resources.getColor(R.color.SteelBlue))
                }
                "W" -> {
                    btn.setBackgroundColor(ctx.resources.getColor(R.color.DarkRed))
                }
                else -> {
                    btn.setBackgroundColor(ctx.resources.getColor(R.color.Gray))
                }

            }
            btn.text = runs

        }

    }


}