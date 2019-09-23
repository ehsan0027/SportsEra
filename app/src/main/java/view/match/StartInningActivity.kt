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
import model.player.PlayerPlayingMatch
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
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
            val progressDialog: ProgressDialog =
                ProgressDialog.show(this, "Starting Match", "setting up match innings...")
            progressDialog.show()

            val databaseRef = FirebaseDatabase.getInstance().reference
            //create an instance of SelectedPlayerForMatch
            val player = PlayerPlayingMatch()

            val startMatchInning = HashMap<String, Any>()

            startMatchInning["/MatchScore/$newMatchId/$battingTeamIdStartInning/$striker"] = player
            startMatchInning["/MatchScore/$newMatchId/$battingTeamIdStartInning/$nonStriker"] =
                player
            startMatchInning["/MatchScore/$newMatchId/$bowlingTeamIdStartInning/$bowler"] = player
            databaseRef.updateChildren(startMatchInning).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("Inning Started")


                    val newDatabaseRef =
                        FirebaseDatabase.getInstance().getReference("/ScheduleMatch/$newMatchId")
                    newDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                val ballType = p0.child("ballType")
                                    .value.toString()
                                val matchCity =
                                    p0.child("matchCity")
                                        .value.toString()
                                val match_date =
                                    p0.child("matchDate")
                                        .value.toString()
                                val match_overs =
                                    p0.child("matchOvers")
                                        .value.toString()
                                val match_time =
                                    p0.child("matchTime")
                                        .value.toString()
                                val matchType =
                                    p0.child("matchType")
                                        .value.toString()
                                val matchVenue =
                                    p0.child("matchVenue")
                                        .value.toString()
                                val sender_Capatain =
                                    p0.child("sender")
                                        .value.toString()

                                val reciever_Captain =
                                    p0.child("reciever")
                                        .value.toString()

                                val squad_count =
                                    p0.child("squadCount")
                                        .value.toString()


                            }
                        }
                    }
                    )






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


    fun setMatchInfo(
        date: String,
        time: String,
        overs: String,
        mType: String,
        ballType: String,
        mVenue: String,
        mCity: String,
        sender: String,
        squadCount: String,
        receiver: String
    ) {
        ///       val match=MatchInvite(mType,overs,mCity,mVenue,date,time,ballType,squadCount,teamA_Id,teamB_Id,)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                STRIKER_RC -> {

                    val name = data.getStringExtra("name")
                    val player_img = data.getStringExtra("player_img")
                    striker = data.getStringExtra("playerId")
                    Picasso.get().load(player_img).into(striker_imageButton_StartInning)
                    playerReSelection(name, STRIKER_RC)
                    striker_Name_StartInning.text = name
                    toast(name)
                }
                NON_STRIKER_RC -> {
                    val name = data.getStringExtra("name")
                    val player_img = data.getStringExtra("player_img")
                    nonStriker = data.getStringExtra("playerId")
                    Picasso.get().load(player_img).into(non_Striker_imageButton_StartInning)
                    playerReSelection(name, NON_STRIKER_RC)
                    non_striker_Name_StartInning.text = name
                    toast(name)
                }
                BOWLER_RC -> {
                    val name = data.getStringExtra("name")
                    val player_img = data.getStringExtra("player_img")
                    bowler = data.getStringExtra("playerId")
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
