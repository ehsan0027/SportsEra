package view.match

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsplayer.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_toss.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import view.GlobalVariable

class TossActivity : AppCompatActivity() {

    lateinit var teamA_Id: String
    lateinit var teamB_Id: String
    lateinit var teamA_Name:String
    lateinit var teamB_Name:String
    lateinit var teamA_Logo:String
    lateinit var teamB_Logo:String

    lateinit var battingTeamName: String
    lateinit var newMatchId: String //new match id sent from StartMatchActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toss)


//start Inning Button
        startInning_TossActivity.setOnClickListener { setTossWonTeam()}

        //teamA_Card
        teamA_Card_TossActivity.setOnClickListener { v ->
            run {

                teamA_Card_TossActivity.isChecked = !teamA_Card_TossActivity.isChecked
                if (teamA_Card_TossActivity.isChecked)
                {teamB_Card_TossActivity.isChecked=false
                GlobalVariable.TossWonTeamName = teamA_Name
                }
            }

        }

        //teamB_Card
        teamB_Card_TossActivity.setOnClickListener { v ->
            run {

                teamB_Card_TossActivity.isChecked = !teamB_Card_TossActivity.isChecked
                if (teamB_Card_TossActivity.isChecked)
                {teamA_Card_TossActivity.isChecked=false
                GlobalVariable.TossWonTeamName = teamB_Name
                }
            }

        }

        //batting Card
        batting_Card_TossActivity.setOnClickListener { view ->
            run {
                batting_Card_TossActivity.isChecked = !batting_Card_TossActivity.isChecked
                if (batting_Card_TossActivity.isChecked) {
                    GlobalVariable.TossWonTeamDecidedTo  = "Bat"
                    bowling_Card_TossActivity.isChecked = false
                    Log.d("TOSSACTIVITY","batting")
                }
            }
        }


        bowling_Card_TossActivity.setOnClickListener { view ->
            run {
                bowling_Card_TossActivity.isChecked = !bowling_Card_TossActivity.isChecked
                if (bowling_Card_TossActivity.isChecked) {
                    GlobalVariable.TossWonTeamDecidedTo = "Bowl"
                    batting_Card_TossActivity.isChecked = false
                    Log.d("TOSSACTIVITY","bowling")

                }
            }
        }

    }


    private fun setTossWonTeam()
    {
        val battingTeamId: String

        when{
            (teamA_Card_TossActivity.isChecked && batting_Card_TossActivity.isChecked)||
                    (teamB_Card_TossActivity.isChecked && bowling_Card_TossActivity.isChecked)-> {
                battingTeamId=teamA_Id
                battingTeamName=teamA_Name
                GlobalVariable.BATTING_TEAM_ID=battingTeamId
                GlobalVariable.BATTING_TEAM_NAME=teamA_Name
                GlobalVariable.BOWLING_TEAM_NAME=teamB_Name
                GlobalVariable.BOWLING_TEAM_ID=teamB_Id
                GlobalVariable.BATTING_TEAM_LOGO=teamA_Logo
                Log.d("TOSSACTIVITY","teamA_selected")
                Log.d("MatchOvers1",GlobalVariable.MATCH_OVERS.toString())
                startInning(battingTeamId)

            }
            (teamB_Card_TossActivity.isChecked && batting_Card_TossActivity.isChecked)||
                    (teamA_Card_TossActivity.isChecked && bowling_Card_TossActivity.isChecked)->{
                battingTeamId=teamB_Id
                battingTeamName=teamB_Name
                GlobalVariable.BATTING_TEAM_ID=battingTeamId
                GlobalVariable.BATTING_TEAM_NAME=teamB_Name
                GlobalVariable.BATTING_TEAM_LOGO=teamB_Logo
                GlobalVariable.BOWLING_TEAM_NAME=teamA_Name
                GlobalVariable.BOWLING_TEAM_ID=teamA_Id
                Log.d("TOSSACTIVITY","teamB_selected")

                startInning(battingTeamId)

            }
        }



    }



    private fun startInning(battingTeamId:String) {
        Log.d("TOSSACTIVITY","BT_id $battingTeamId")
        if (battingTeamId.isNotEmpty()
            && battingTeamName.isNotEmpty()
            && newMatchId.isNotEmpty()
            && teamA_Id.isNotEmpty()
            && teamB_Id.isNotEmpty()
        ) {
            Log.d("TOSSACTIVITY","StartInningActivity")
            startActivity<StartInningActivity>(
                "battingTeamId" to battingTeamId,
                "battingTeamName" to battingTeamName,
                "newMatchId" to newMatchId,
                "teamA_Id" to teamA_Id,
                "teamB_Id" to teamB_Id,
                "tossWonTeamElectedTo" to GlobalVariable.TossWonTeamDecidedTo
            )
                } else {
            alert {
                title = "No Selection"
                message = "Please Select Toss Winning Team and Their Choice (batting/bowling)"
                setFinishOnTouchOutside(false)
                okButton { dialog ->
                    dialog.dismiss()
                }
            }.show()
        }

    }

    override fun onResume() {
        super.onResume()
        showTeams()
    }

    private fun showTeams() {

        teamA_Id = intent.getStringExtra("team_A_Id")
        teamB_Id = intent.getStringExtra("team_B_Id")
        teamA_Name = intent.getStringExtra("team_A_Name")
        teamB_Name = intent.getStringExtra("team_B_Name")
        newMatchId = intent.getStringExtra("match_Id")

        teamA_Logo = intent.getStringExtra("team_A_Logo")
        teamB_Logo = intent.getStringExtra("team_B_Logo")

        Picasso.get().load(teamA_Logo).into(teamA_Logo_TossActivity)
        Picasso.get().load(teamB_Logo).into(teamB_Logo_TossActivity)
        teamA_FullName_TossActivity.text = teamA_Name
        teamB_FullName_Logo_TossActivity.text = teamB_Name

    }


}
