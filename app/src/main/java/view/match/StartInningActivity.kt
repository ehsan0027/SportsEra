package view.match

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsplayer.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_start_inning.*
import model.Batsman
import model.Bowler
import model.Inning
import org.jetbrains.anko.*
import view.GlobalVariable
import view.matchscoring.MatchScoringActivity
import view.team.TeamsPlayerReadyToPlayMatch
@Suppress("DEPRECATION")
class StartInningActivity : AppCompatActivity() {
    lateinit var newMatchId: String
    lateinit var teamA_Id: String
    lateinit var teamB_Id: String
    lateinit var striker: String
    lateinit var nonStriker: String
    lateinit var bowler: String
    private lateinit var battingTeamIdStartInning: String
    private lateinit var bowlingTeamIdStartInning: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_inning)
        striker = ""
        nonStriker = ""
        //Select striker for batting
        striker_imageButton_StartInning.setOnClickListener { selectStriker() }
        //select Non striker for batting
        non_Striker_imageButton_StartInning.setOnClickListener { selectNonStriker() }
        //[select bowler]
        bowler_imageButton_StartInning.setOnClickListener {
            if (battingTeamIdStartInning == teamA_Id) {
                startActivityForResult<TeamsPlayerReadyToPlayMatch>(
                    BOWLER_RC,
                    "teamId" to teamB_Id,
                    "newMatchId" to newMatchId
                )
            } else {
                startActivityForResult<TeamsPlayerReadyToPlayMatch>(
                    BOWLER_RC,
                    "teamId" to teamA_Id,
                    "newMatchId" to newMatchId
                )
            }
        }

        //start Inning
        saveInning_Button_StartInning.setOnClickListener {
            createInning()
        }
    }

    private fun selectStriker() {
        startActivityForResult<TeamsPlayerReadyToPlayMatch>(
            STRIKER_RC,
            "teamId" to battingTeamIdStartInning,
            "newMatchId" to newMatchId
        )

    }

    private fun selectNonStriker() {
        startActivityForResult<TeamsPlayerReadyToPlayMatch>(
            NON_STRIKER_RC,
            "teamId" to battingTeamIdStartInning,
            "newMatchId" to newMatchId
        )

    }

    private fun playerReSelection(playerName: String, requestCode: Int) {
        if (striker == nonStriker) {
            toast("SamePlayerSelection")
            alert {
                title = "Reselection"
                message = "$playerName Already Selected for Batting"
                okButton { dialog ->
                    when (requestCode) {
                        STRIKER_RC -> {
                            selectStriker()
                        }
                        NON_STRIKER_RC -> {
                            selectNonStriker()
                        }
                    }
                    dialog.dismiss()
                }
            }.show()
        } else {
            Log.d("ReselectionStriker", striker)
            Log.d("ReselectionNonStriker", nonStriker)
            toast("player are not same")
        }
    }


    override fun onStart() {
        super.onStart()
        supportActionBar?.title = "Start Inning"
        battingTeamIdStartInning = intent.getStringExtra("battingTeamId")
        val battingTeamName = intent.getStringExtra("battingTeamName")
        newMatchId = intent.getStringExtra("newMatchId")
        teamA_Id = intent.getStringExtra("teamA_Id")
        teamB_Id = intent.getStringExtra("teamB_Id")
        bowlingTeamIdStartInning = when (battingTeamIdStartInning) {
            teamA_Id -> teamB_Id
            else -> teamA_Id
        }
        setTeamName(battingTeamName, bowlingTeamIdStartInning)

    }

    private fun setTeamName(battingTeamName: String, teamId: String) {
        val teamRef = FirebaseDatabase.getInstance().getReference("/Team/$teamId")
        teamRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val bowlingTeamName = p0.child("teamName").value.toString()
                    batting_TeamName_StartInning.text = battingTeamName
                    bowling_Team_Name_StartInning.text = bowlingTeamName
                }


            }
        })

    }
    private fun createInning() {
        if (newMatchId.isNotEmpty() && teamA_Id.isNotEmpty()
            && striker.isNotEmpty() && nonStriker.isNotEmpty()
            && bowler.isNotEmpty()
        ) {
            GlobalVariable.MATCH_ID = newMatchId
            val batsman1 = Batsman()
            val batsman2 = Batsman()
            val bowler = Bowler()
            val inning = Inning()
            val progressDialog: ProgressDialog =
                ProgressDialog.show(this, "Starting Match", "Setting up match innings...")
            progressDialog.show()

            Log.d("InningA",battingTeamIdStartInning)
            Log.d("InningB",bowlingTeamIdStartInning)
            Log.d("InningS",GlobalVariable.STRIKER_ID)
            Log.d("InningN",GlobalVariable.NON_STRIKER_ID)
            Log.d("InningBL",GlobalVariable.BOWLER_ID)

            val updateStatus = FirebaseDatabase.getInstance().reference
            val setStatusLive = HashMap<String, Any?>()
            setStatusLive["MatchInfo/$newMatchId/match  Status"] = "Live"
            setStatusLive["/TeamsMatchInfo/$teamA_Id/Live/$newMatchId"]=true
            setStatusLive["/TeamsMatchInfo/$teamA_Id/Upcoming/$newMatchId"]=null
            setStatusLive["/TeamsMatchInfo/$teamB_Id/Live/$newMatchId"]=true
            setStatusLive["/TeamsMatchInfo/$teamB_Id/Upcoming/$newMatchId"]=null

            updateStatus.updateChildren(setStatusLive).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val newdataBaseRef =
                        FirebaseDatabase.getInstance().reference
                    val newdataBaseRef_B =
                        FirebaseDatabase.getInstance().reference

                    newdataBaseRef.child("TeamsPlayer").child(teamA_Id)
                        .addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                                override fun onDataChange(p0: DataSnapshot) {

                                    if (p0.exists()) {
                                        val playerRef =
                                            FirebaseDatabase.getInstance()
                                                .reference
                                        val setPlayerUpcomingMatch =
                                            HashMap<String, Any?>()
                                        p0.children.forEach {
                                            val p_id = it.key

                                            setPlayerUpcomingMatch["PlayersMatchId/$p_id/Live/$newMatchId"] = true
                                            setPlayerUpcomingMatch["PlayersMatchId/$p_id/Upcoming/$newMatchId"] = null

                                            playerRef.updateChildren(
                                                setPlayerUpcomingMatch
                                            )
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Log.d(
                                                            "Player",
                                                            "Team A Upcoming Match is Set in Player"
                                                        )
                                                    }
                                                }

                                        }
                                    }
                                }

                            }
                        )



                    newdataBaseRef_B.child("TeamsPlayer").child(teamB_Id)
                        .addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                                override fun onDataChange(p0: DataSnapshot) {

                                    if (p0.exists()) {
                                        val playerRef =
                                            FirebaseDatabase.getInstance()
                                                .reference
                                        val setPlayerUpcomingMatch =
                                            HashMap<String, Any?>()
                                        p0.children.forEach {
                                            val p_id = it.key


                                            setPlayerUpcomingMatch["PlayersMatchId/$p_id/Live/$newMatchId"] = true
                                            setPlayerUpcomingMatch["PlayersMatchId/$p_id/Upcoming/$newMatchId"] = null
                                            playerRef.updateChildren(
                                                setPlayerUpcomingMatch
                                            )
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Log.d(
                                                            "Player",
                                                            "Team B Upcoming Match is Set in Player"
                                                        )
                                                    }
                                                }

                                        }
                                    }
                                }

                            }
                        )


                    val databaseRef = FirebaseDatabase.getInstance().reference
                    val startMatchInning = HashMap<String, Any>()
                    startMatchInning["/MatchScore/$newMatchId/${GlobalVariable.Inning}"] = inning

                    databaseRef.updateChildren(startMatchInning).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            toast("Inning Started")

                            val ndR = FirebaseDatabase.getInstance().reference
                            val setPlayersReady = HashMap<String,Any>()
                            setPlayersReady["/MatchScore/$newMatchId/${GlobalVariable.Inning}/$battingTeamIdStartInning/${GlobalVariable.STRIKER_ID}"]= batsman1
                            setPlayersReady["/MatchScore/$newMatchId/${GlobalVariable.Inning}/$battingTeamIdStartInning/${GlobalVariable.NON_STRIKER_ID}"] = batsman2
                            setPlayersReady["/MatchScore/$newMatchId/${GlobalVariable.Inning}/$bowlingTeamIdStartInning/${GlobalVariable.BOWLER_ID}"] = bowler
                            ndR.updateChildren(setPlayersReady).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    toast("Players are Ready")
                                }
                            }
                        }
                    }
                    startActivity<MatchScoringActivity>()
                    progressDialog.dismiss()
                    finish()
                }
            }.addOnFailureListener { exception ->
                toast(exception.localizedMessage.toString())
                progressDialog.dismiss()

            }

        } else {
            toast("Something went Wrong")
        }

    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                STRIKER_RC -> {
                                Log.d("onActivity_SI","striker selected")
                    val name = data.getStringExtra("name")
                    val player_img = data.getStringExtra("player_img")
                    striker = data.getStringExtra("playerId")
                    GlobalVariable.STRIKER_NAME=name
                    GlobalVariable.STRIKER_ID=striker
                    Picasso.get().load(player_img).into(striker_imageButton_StartInning)
                    playerReSelection(name, STRIKER_RC)
                    striker_Name_StartInning.text = name
                    toast(name)
                }
                NON_STRIKER_RC -> {
                    val name = data.getStringExtra("name")
                    val player_img = data.getStringExtra("player_img")
                    nonStriker = data.getStringExtra("playerId")
                    GlobalVariable.NON_STRIKER_ID=nonStriker
                    GlobalVariable.NON_STRIKER_NAME=name
                    Picasso.get().load(player_img).into(non_Striker_imageButton_StartInning)
                    playerReSelection(name, NON_STRIKER_RC)
                    non_striker_Name_StartInning.text = name
                    toast(name)
                }
                BOWLER_RC -> {
                    val name = data.getStringExtra("name")
                    val player_img = data.getStringExtra("player_img")
                    bowler = data.getStringExtra("playerId")
                    GlobalVariable.BOWLER_ID=bowler
                    Log.d("BOWLER_ID_1","$bowler")
                    GlobalVariable.BOWLER_NAME=name
                    bowler_Name_StartInning.text = name
                    Picasso.get().load(player_img).into(bowler_imageButton_StartInning)
                    toast(name)
                }

            }
        }
    }

    companion object {
        const val STRIKER_RC = 1
        const val NON_STRIKER_RC = 2
        const val BOWLER_RC = 3
    }
}
