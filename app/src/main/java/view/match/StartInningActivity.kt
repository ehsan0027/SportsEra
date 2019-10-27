package view.match

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsplayer.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pawegio.kandroid.toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_start_inning.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Batsman
import model.Bowler
import model.Inning
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import view.GlobalVariable
import view.matchscoring.MatchScoringActivity
import view.team.TeamsPlayerReadyToPlayMatch
import kotlin.collections.set

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
        newMatchId=""




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

            CoroutineScope(Dispatchers.IO).launch {
                createInning()

            }
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
//        GlobalVariable.BATTING_TEAM_SQUAD.forEach {
//            Log.d("TossFlow",it)
//        }
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
            override fun onDataChange(teamData: DataSnapshot) {
                if (teamData.exists()) {
                    val bowlingTeamName = teamData.child("teamName").value.toString()
                    batting_TeamName_StartInning.text = battingTeamName
                    bowling_Team_Name_StartInning.text = bowlingTeamName
                }
            }
        })

    }

    private fun  setTossWonTeam(){
        val tossDB = FirebaseDatabase.getInstance().reference
        val updateTossTeam = HashMap<String,Any>()
        updateTossTeam["/MatchInfo/$newMatchId/tossWonByTeam"] = GlobalVariable.TossWonTeamName
        updateTossTeam["/MatchInfo/$newMatchId/tossWonTeamElectedTo"] = GlobalVariable.TossWonTeamDecidedTo
        updateTossTeam["/MatchInfo/$newMatchId/battingTeamName"] = GlobalVariable.BATTING_TEAM_NAME

        tossDB.updateChildren(updateTossTeam).addOnCompleteListener { task ->
            if (task.isSuccessful){
                toast("Toss Won By ${GlobalVariable.BATTING_TEAM_NAME}")
            }
        }
    }

   private  fun createInning(){
        if (newMatchId.isNotEmpty() && teamA_Id.isNotEmpty()
            && striker.isNotEmpty() && nonStriker.isNotEmpty()
            && bowler.isNotEmpty()
        ) {
            GlobalVariable.MATCH_ID = newMatchId
            val batsman1 = Batsman(
                GlobalVariable.Batsman_1_NAME,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0f,
                GlobalVariable.BattingPosition,
                GlobalVariable.Batsman_1_Img

            )
            GlobalVariable.BattingPosition = 1 + GlobalVariable.BattingPosition
            val batsman2 = Batsman(
                GlobalVariable.Batsman_2_NAME,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0f,
                GlobalVariable.BattingPosition,
                GlobalVariable.Batsman_2_Img
            )
            val bowler = Bowler(
                GlobalVariable.BOWLER_NAME,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0f,
                0,
                GlobalVariable.BowlerPosition,
                GlobalVariable.BOWLER_Img
            )
            val inning = Inning()
//            val progressDialog: ProgressDialog =
//                ProgressDialog.show(this@StartInningActivity, "Starting Match", "Setting up match innings...")
//            progressDialog.show()

            if (GlobalVariable.Inning=="FirstInning"){
                setTossWonTeam()
                GlobalVariable.BATTING_TEAM_SQUAD.forEach {
                    if(!GlobalVariable.SELECTED_PLAYERS_ID_LIST.contains(it))
                    {
                        GlobalVariable.YetToBat.add(it)
                    }
                }
            }
            else{
                GlobalVariable.BOWLING_TEAM_SQUAD.forEach {
                    if(!GlobalVariable.SELECTED_PLAYERS_ID_LIST.contains(it))
                    {
                        GlobalVariable.YetToBat.add(it)
                    }
                }
            }


            val updateStatus = FirebaseDatabase.getInstance().reference
            val setStatusLive = HashMap<String, Any?>()
            setStatusLive["/TeamsMatchInfo/$teamA_Id/Live/$newMatchId"] = true
            setStatusLive["/TeamsMatchInfo/$teamA_Id/Upcoming/$newMatchId"] = null
            setStatusLive["/TeamsMatchInfo/$teamB_Id/Live/$newMatchId"] = true
            setStatusLive["/TeamsMatchInfo/$teamB_Id/Upcoming/$newMatchId"] = null

            updateStatus.updateChildren(setStatusLive).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val newdataBaseRef =
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

                                            setPlayerUpcomingMatch["PlayersMatchId/$p_id/Live/$newMatchId"] =
                                                true
                                            setPlayerUpcomingMatch["PlayersMatchId/$p_id/Upcoming/$newMatchId"] =
                                                null

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

                        val newdataBaseRef_B =
                            FirebaseDatabase.getInstance().reference
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


                                                setPlayerUpcomingMatch["PlayersMatchId/$p_id/Live/$newMatchId"] =
                                                    true
                                                setPlayerUpcomingMatch["PlayersMatchId/$p_id/Upcoming/$newMatchId"] =
                                                    null
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
                            val setPlayersReady = HashMap<String, Any>()
                            setPlayersReady["/MatchScore/$newMatchId/${GlobalVariable.Inning}/$battingTeamIdStartInning/${GlobalVariable.Batsman_1_ID}"] =
                                batsman1
                            setPlayersReady["/MatchScore/$newMatchId/${GlobalVariable.Inning}/$battingTeamIdStartInning/StrikerId"] =
                                GlobalVariable.Batsman_1_ID
                            setPlayersReady["/MatchScore/$newMatchId/${GlobalVariable.Inning}/$battingTeamIdStartInning/${GlobalVariable.Batsman_2_ID}"] =
                                batsman2
                            setPlayersReady["/MatchScore/$newMatchId/${GlobalVariable.Inning}/$battingTeamIdStartInning/YetToBat"] =
                                GlobalVariable.YetToBat
                            setPlayersReady["/MatchScore/$newMatchId/${GlobalVariable.Inning}/$bowlingTeamIdStartInning/${GlobalVariable.BOWLER_ID}"] =
                                bowler
                            setPlayersReady["/MatchScore/$newMatchId/${GlobalVariable.Inning}/$bowlingTeamIdStartInning/CurrentBowler"] =
                                GlobalVariable.BOWLER_ID
                            ndR.updateChildren(setPlayersReady).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    toast("Players are Ready")

                                    val dbRef = FirebaseDatabase.getInstance().reference
                                    val setTeamIds = HashMap<String, Any>()
                                    setTeamIds["/MatchScore/$newMatchId/FirstInning/CurrentInning"] =
                                        GlobalVariable.Inning
                                    setTeamIds["/MatchScore/$newMatchId/${GlobalVariable.Inning}/battingTeamId"] =
                                        GlobalVariable.BATTING_TEAM_ID
                                    setTeamIds["/MatchScore/$newMatchId/${GlobalVariable.Inning}/bowlingTeamId"] =
                                        GlobalVariable.BOWLING_TEAM_ID
                                    setTeamIds["/MatchScore/$newMatchId/${GlobalVariable.Inning}/battingTeamName"] =
                                        GlobalVariable.BATTING_TEAM_NAME
                                    setTeamIds["/MatchScore/$newMatchId/${GlobalVariable.Inning}/bowlingTeamName"] =
                                        GlobalVariable.BOWLING_TEAM_NAME
                                    dbRef.updateChildren(setTeamIds).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            toast("inning started")
                                            startActivity<MatchScoringActivity>()
//                                            progressDialog.dismiss()
                                            finish()
                                        }
                                    }
                                }
                            }
                        }

                    }
                }


            }.addOnFailureListener { exception ->
                toast(exception.localizedMessage.toString())
//                progressDialog.dismiss()

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
                    Log.d("onActivity_SI", "striker selected")
                    val name = data.getStringExtra("name")
                    val player_img = data.getStringExtra("player_img")
                    striker = data.getStringExtra("playerId")
                    GlobalVariable.SELECTED_PLAYERS_ID_LIST.add(striker)
                    GlobalVariable.Batsman_1_NAME = name
                    GlobalVariable.Batsman_1_ID = striker
                    GlobalVariable.Batsman_1_Img=player_img
                    Picasso.get().load(player_img).into(striker_imageButton_StartInning)
                    playerReSelection(name, STRIKER_RC)
                    striker_Name_StartInning.text = name
                    toast(name)
                }
                NON_STRIKER_RC -> {
                    val name = data.getStringExtra("name")
                    val player_img = data.getStringExtra("player_img")
                    nonStriker = data.getStringExtra("playerId")
                    GlobalVariable.SELECTED_PLAYERS_ID_LIST.add(nonStriker)
                    GlobalVariable.Batsman_2_ID = nonStriker
                    GlobalVariable.Batsman_2_NAME = name
                    GlobalVariable.Batsman_2_Img=player_img
                    Picasso.get().load(player_img).into(non_Striker_imageButton_StartInning)
                    playerReSelection(name, NON_STRIKER_RC)
                    non_striker_Name_StartInning.text = name
                    toast(name)
                }
                BOWLER_RC -> {
                    val name = data.getStringExtra("name")
                    val player_img = data.getStringExtra("player_img")
                    bowler = data.getStringExtra("playerId")
                    GlobalVariable.BOWLER_ID = bowler
                    Log.d("BOWLER_ID_1", "$bowler")
                    GlobalVariable.BOWLER_NAME = name
                    GlobalVariable.BOWLER_Img=player_img
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
