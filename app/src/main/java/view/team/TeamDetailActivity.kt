package view.team


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsplayer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pawegio.kandroid.toast
import com.pawegio.kandroid.visible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_team_detail.*
import org.jetbrains.anko.startActivity
import view.match.MatchDetails
import view.team.ui.*
import view.team.ui.TeamMatchTabs.TeamCompletedMatchesFragment
import view.team.ui.TeamMatchTabs.TeamUpcomingMatchFragment

class TeamDetailActivity : AppCompatActivity(), View.OnClickListener,
    TeamStatsFragment.OnFragmentInteractionListener,
    TeamMemberFragment.OnFragmentInteractionListener,
    TeamMatchFragment.OnFragmentInteractionListener,
    TeamRequestMatchFragment.OnFragmentInteractionListener,
    TeamSquadFragment.OnFragmentInteractionListener,
    TeamUpcomingMatchFragment.OnFragmentInteractionListener,
    TeamCompletedMatchesFragment.OnFragmentInteractionListener {


    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var captainId_A: String
    lateinit var team_A_Id: String
    lateinit var team_A_Name: String
    lateinit var team_A_Logo: String
    lateinit var team_A_City: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)

        setViewsContent()
        //assign Click Listener to Button
        challenge_for_match.setOnClickListener(this)

        val captainId = intent.getStringExtra("captainId_A").toString()
        val currentPlayer = FirebaseAuth.getInstance().uid.toString()
        if (currentPlayer != captainId) {
            makeViewsInvisible(challenge_for_match)
        }


    }


    private fun getUserInfo(teamId: String) {
        val teamRef = FirebaseDatabase.getInstance().getReference("/Team/$teamId")
        teamRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {


                val teamSquadCount =p0.child("TeamSquad").childrenCount
                Log.d("Team Squad Count","$teamSquadCount")
                if (teamSquadCount < 2)
                {
                    makeViewsInvisible(challenge_for_match)
                toast("Please Add Players in Your Team to Challenge Opponent")
                }



                captainId_A = p0.child("captainId").value.toString()
                val playerRef =
                    FirebaseDatabase.getInstance().getReference("/PlayerBasicProfile/$captainId_A")
                playerRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        val captainName = p0.child("name").value.toString()
                        teamCaptain_TeamDetailActivity.text = captainName
                    }
                })
            }
        })
    }

    private fun setViewsContent() {
        team_A_Id = intent.getStringExtra("team_A_Id")
        team_A_Logo = intent.getStringExtra("team_A_Logo")
        team_A_Name = intent.getStringExtra("team_A_Name")
        team_A_City = intent.getStringExtra("team_A_City")
        captainId_A = intent.getStringExtra("captainId_A")
        Log.d("CaptainId", captainId_A)

        supportActionBar?.title = team_A_Name

        val fragmentAdapter =
            SectionPagerAdapter(team_A_Id, team_A_Name, team_A_Logo, captainId_A, supportFragmentManager)
        viewPager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)

        Picasso.get().load(team_A_Logo).into(team_logo_TeamDetailActivity)
        teamCity_TeamDetailActivity.text = team_A_City
        getUserInfo(team_A_Id)


    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.challenge_for_match -> {


                startActivity<MatchDetails>(
                    "team_A_Id" to team_A_Id,
                    "team_A_Logo" to team_A_Logo,
                    "team_A_Name" to team_A_Name,
                    "captainId_A" to captainId_A,
                    "teamCity_A" to team_A_City
                )
                finish()
            }
        }
    }

    private fun makeViewsInvisible(vararg view: View) {
        for (v in view) {
            v.visible = false
        }
    }

}
