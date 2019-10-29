package view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentTransaction
import com.example.sportsplayer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pawegio.kandroid.visible
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.dashboard_activity.*
import kotlinx.android.synthetic.main.fragment_team_upcoming_match_card.view.*
import kotlinx.android.synthetic.main.match_card_on_dashboard.view.*
import kotlinx.android.synthetic.main.my_team_list_ondashboard.view.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import view.ProfilePackage.Profile
import view.fragment.SearchTeamFragment
import view.match.FullScoreCardActivity
import view.match.MatchDetails
import view.match.TossActivity
import view.team.TeamDetailActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class Dashboard : AppCompatActivity(), SearchTeamFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var currentPlayer: String = ""
    private lateinit var searchTeamFragment: SearchTeamFragment
    val teamAdapter = GroupAdapter<ViewHolder>()
    val upcomingMatchAdapter = GroupAdapter<ViewHolder>()

    val liveMatchAdapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)


        profile_cardView.setOnClickListener {
            startActivity<Profile>()
        }

        liveMatchAdapter.setOnItemClickListener { item, view ->

            val match = item as LiveMatchViewHolder
            val m_Id = match.match_Id
            val teamA_Id = match.team_A_Id
            val teamB_Id = match.team_B_Id
            startActivity<FullScoreCardActivity>(
                "match_Id" to m_Id,
                "team_A_Id" to teamA_Id,
                "team_B_Id" to teamB_Id
            )
            finish()
        }



        currentPlayer = FirebaseAuth.getInstance().uid.toString()

        live_match_card_recycler_view_dashboard?.adapter = liveMatchAdapter

        teamAdapter.setOnItemClickListener { item, view ->

            val team = item as MyTeamOnDashboard
            Log.d("Dashboard_TeamName", team.teamName)
            Log.d("Dashboard_TeamCaptain", team.captainId)
            Log.d("Dashboard_TeamCity", team.teamCity)


            startActivity<TeamDetailActivity>(
                "team_A_Id" to team.teamId,
                "team_A_Logo" to team.teamLogo,
                "team_A_Name" to team.teamName,
                "team_A_City" to team.teamCity,
                "captainId_A" to team.captainId
            )

        }

        //get the instance of SearchTeamFragment
        searchTeamFragment = SearchTeamFragment()

        //Listener to check the fragments on the Stack
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                makeViewsVisible(dashboard_layout)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK)

            when (requestCode) {

            }

    }


    override fun onResume() {
        super.onResume()

        teamAdapter.clear()
        upcomingMatchAdapter.clear()
        liveMatchAdapter.clear()
        dashboard_team_recyclerView?.removeAllViewsInLayout()
        upcoming_match_card_recycler_view_dashboard?.removeAllViewsInLayout()
        live_match_card_recycler_view_dashboard?.removeAllViewsInLayout()
        //retrieve team data from the database
        fetchTeamFromDatabase(currentPlayer)
        //Live Match Call
        fetchLiveMatchDetails(currentPlayer)
    }

    private fun getUserInfo() {

        val playerRef =
            FirebaseDatabase.getInstance().getReference("/PlayerBasicProfile/$currentPlayer")
        playerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                val playerImage = p0.child("profile_img").value.toString()
                val playerName = p0.child("name").value.toString()
                val playingRole = p0.child("playing_role").value.toString()
                val playerCity = p0.child("city").value.toString()


                val playerDob = p0.child("dateOfBirth").value.toString()
                var date: Date? = null
                val sdf = SimpleDateFormat("dd.MM.yyyy")
                try {
                    date = sdf.parse(playerDob)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                if (date == null) return
                val dob: Calendar = Calendar.getInstance()
                val today: Calendar = Calendar.getInstance()
                dob.time = date
                val year = dob.get(Calendar.YEAR)//get dateOfBirth Year
                val month = dob.get(Calendar.MONTH)
                val day = dob.get(Calendar.DAY_OF_MONTH)
                dob.set(year, month, day)
                var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
                if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }
                playerAge_DashboardActivity.text = age.toString()


                Picasso
                    .get()
                    .load(playerImage)
                    .fit() // use fit() and centerInside() for making it memory efficient.
                    .centerInside()
                    .into(profile_Image_DashboardActivity)
                playerName_DashboardActivity.text = playerName
                playerRole_DashboardActivity.text = playingRole
                playerCity_DashboardActivity.text = playerCity

            }
        })

        val playerRefRuns =
            FirebaseDatabase.getInstance().getReference("/BattingStats/$currentPlayer")
        playerRefRuns.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                val playerRuns = p0.child("runs").value.toString()

                batting_figures_dashboard.text = playerRuns

            }
        })

        val playerRefWickets =
            FirebaseDatabase.getInstance().getReference("/BowlingStats/$currentPlayer")
        playerRefWickets.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                val playerWickets = p0.child("wicket").value.toString()

                bowling_figures_dashboard.text = playerWickets

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        val playerRefMatches =
            FirebaseDatabase.getInstance().getReference("/BattingStats/$currentPlayer")
        playerRefMatches.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {


                val playerMatches = p0.child("matches").value.toString()

                matches_figures_Dashboard.text = playerMatches


            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.dashboard_menu, menu)
        val menuItem: MenuItem = menu!!.findItem(R.id.actionbar_search)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnSearchClickListener {
            makeViewsInvisible(dashboard_layout)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, searchTeamFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("searchTeamFragment")
                .commit()
        }


        return super.onCreateOptionsMenu(menu)


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.profile -> startActivity<Profile>()
            R.id.editProfile -> {
                startActivity<EditProfile>()
            }//Upcoming Matches Activity
            R.id.startMatch -> {
                startActivity<MatchDetails>()
            }
            R.id.signOut -> signOutUser()
            R.id.createTeam -> startActivity<TeamRegistration>()
            R.id.create_team_Button -> startActivity<TeamRegistration>()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        getUserInfo()
    }

    private fun makeViewsInvisible(vararg view: View) {
        for (v in view) {
            v.visible = false
        }
    }

    private fun makeViewsVisible(vararg view: View) {
        for (v in view) {
            v.visible = true
        }
    }

    fun makeLayoutVisible() {
        makeViewsVisible(dashboard_layout)
        return
    }


    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        } else {

            super.onBackPressed()
            finish()
        }
    }

    private fun signOutUser() {
        val m = FirebaseAuth.getInstance()
        m.signOut()
        // startActivity<MainActivity>()
    }

    //Team Card Started

    private fun fetchTeamFromDatabase(playerId: String) {

        val teamRef = FirebaseDatabase.getInstance()
        val playersTeamReference =
            FirebaseDatabase.getInstance().getReference("/PlayersTeam/$playerId")
        playersTeamReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    p0.children.forEach {
                        val teamId = it.key
                        teamRef.getReference("/Team/$teamId").also { task ->
                            task.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    //get the actual Team (Name and Logo)
                                    val team_Id = p0.child("teamId").value.toString()
                                    val teamLogo = p0.child("teamLogo").value.toString()
                                    val teamName = p0.child("teamName").value.toString()
                                    val captainId = p0.child("captainId").value.toString()
                                    val teamCity = p0.child("city").value.toString()

                                    //cardView color
                                    //   val red=(10..230).random()
                                    //   val green=(10..230).random()
                                    //   val blue=(10..230).random()
                                    //   val color= Color.argb(255,red,green,blue)
                                    teamAdapter.add(
                                        MyTeamOnDashboard(
                                            teamLogo,
                                            teamName,
                                            captainId,
                                            teamCity,
                                            team_Id
                                        )
                                    )
                                }

                            })
                        }
                    }

                    dashboard_team_recyclerView.adapter = teamAdapter

                }
            }
        })
    }

    class MyTeamOnDashboard(
        var teamLogo: String,
        var teamName: String,
        var captainId: String,
        var teamCity: String,
        var teamId: String
    ) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.my_team_list_ondashboard
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            val logo = viewHolder.itemView.findViewById<ImageView>(R.id.my_team_logo_dashboard)
            Picasso.get().load(teamLogo).into(logo)
            viewHolder.itemView.my_team_name_dashboard.text = teamName

        }


    }

    //Team Card End

//Upcoming Match Card Started

    private fun fetchUpcomingMatchDetails(playerId: String) {

        val newDatabaseRef =
            FirebaseDatabase.getInstance().getReference("PlayersMatchId/$playerId/Upcoming")
        newDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                longToast("Non Empty Card Shown")
                Log.d("empty", "NonEmpty Card Shown")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val scheduleMatchRef =
                        FirebaseDatabase.getInstance().getReference("MatchInfo")
                    p0.children.forEach {

                        val upcomingMatchId = it.key
                        scheduleMatchRef.child("$upcomingMatchId")
                            .addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(p0: DataSnapshot) {


                                    //INSTALL SUCCESSFULLY


                                    if (p0.exists()) {

                                        val match_type = p0.child("matchType").value.toString()
                                        val team_A_Id = p0.child("team_A_Id").value.toString()
                                        val team_B_Id = p0.child("team_B_Id").value.toString()
                                        val match_Id = p0.child("matchId").value.toString()
                                        val match_overs = p0.child("matchOvers").value.toString()
                                        GlobalVariable.MATCH_OVERS = match_overs.toInt()
                                        val match_date = p0.child("matchDate").value.toString()
                                        val match_time = p0.child("matchTime").value.toString()
                                        val match_venue = p0.child("matchVenue").value.toString()
                                        val match_city = p0.child("matchCity").value.toString()
                                        val team_A_Name = p0.child("team_A_Name").value.toString()
                                        val team_B_Name = p0.child("team_B_Name").value.toString()
                                        val team_A_Logo = p0.child("team_A_Logo").value.toString()
                                        val team_B_Logo = p0.child("team_B_Logo").value.toString()
                                        val sender = p0.child("sender").value.toString()
                                        empty_match_card_on_dashboard.visible = false

                                        upcomingMatchAdapter.add(
                                            UpcomingMatchViewHolder(
                                                match_Id,
                                                team_A_Id,
                                                team_B_Id,
                                                match_type,
                                                match_overs,
                                                match_date,
                                                match_time,
                                                match_venue,
                                                match_city,
                                                team_A_Name,
                                                team_B_Name,
                                                team_A_Logo,
                                                team_B_Logo,
                                                sender,
                                                this@Dashboard
                                            )
                                        )

                                    }
                                }
                            })
                    }
                    upcoming_match_card_recycler_view_dashboard?.adapter = upcomingMatchAdapter
                }
            }
        })

    }


    fun startTossActivity(position: Int) {
        val item = upcomingMatchAdapter.getItem(position) as UpcomingMatchViewHolder


        val match_id = item.match_Id
        val team_A_id = item.team_A_Id
        val team_B_id = item.team_B_Id
        val team_A_name = item.team_A_Name
        val team_B_name = item.team_B_Name
        val team_A_logo = item.team_A_Logo
        val team_B_logo = item.team_B_Logo

        startActivity<TossActivity>(
            "match_Id" to match_id,
            "team_A_Id" to team_A_id,
            "team_B_Id" to team_B_id,
            "team_A_Name" to team_A_name,
            "team_B_Name" to team_B_name,
            "team_A_Logo" to team_A_logo,
            "team_B_Logo" to team_B_logo
        )

    }


    class UpcomingMatchViewHolder(

        var match_Id: String,
        var team_A_Id: String,
        var team_B_Id: String,
        var match_type: String,
        var match_overs: String,
        var match_date: String,
        var match_time: String,
        var match_venue: String,
        var match_city: String,
        var team_A_Name: String,
        var team_B_Name: String,
        var team_A_Logo: String,
        var team_B_Logo: String,
        var sender: String,
        val ctx: Dashboard
    ) : Item<ViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.fragment_team_upcoming_match_card
        }

        private fun makeViewsInvisible(vararg view: View) {
            for (v in view) {
                v.visible = false
            }
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.upcoming_match_cardView


            viewHolder.itemView.title_of_match_upcoming.text = match_type

            if (match_type != "Test") {
                viewHolder.itemView.overs_of_match_upcoming.text = match_overs
            } else {
                makeViewsInvisible(viewHolder.itemView.overs_of_match_upcoming)
            }


            if (ctx.currentPlayer != sender) {
                makeViewsInvisible(viewHolder.itemView.start_match_button)
            }
            viewHolder.itemView.start_match_button.setOnClickListener { v ->
                run {
                    ctx.startTossActivity(position)
                }
            }
            viewHolder.itemView.date_of_match_upcoming.text = match_date
            viewHolder.itemView.starting_time_of_match_upcoming.text = match_time
            viewHolder.itemView.venue_of_match_on_match_card_upcoming.text = match_venue
            viewHolder.itemView.city_of_match_on_match_card_upcoming.text = match_city
            viewHolder.itemView.team_A_name_match_card_upcoming.text = team_A_Name
            viewHolder.itemView.team_B_name_match_card_upcoming.text = team_B_Name


            val logo_team_A =
                viewHolder.itemView.findViewById<ImageView>(R.id.team_A_logo_match_card_upcoming)
            Picasso.get().load(team_A_Logo).into(logo_team_A)

            val logo_team_B =
                viewHolder.itemView.findViewById<ImageView>(R.id.team_B_logo_match_card_upcoming)
            Picasso.get().load(team_B_Logo).into(logo_team_B)

        }


    }

    //Upcoming Match Card End


    //Live Match Card Starts

    private fun fetchLiveMatchDetails(playerId: String) {
        val newDatabaseRef =
            FirebaseDatabase.getInstance()
                .getReference("PlayersMatchId/$playerId/Live")
        newDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(liveMatch: DataSnapshot) {
                if (liveMatch.exists()) {
                    Log.d("Flow","1")
                    val liveMatchRef =
                        FirebaseDatabase.getInstance().getReference("MatchInfo")
                    liveMatch.children.forEach {
                        val matchId = it.key.toString()

                        liveMatchRef.child(matchId)
                            .addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {}

                                override fun onDataChange(matchInfo: DataSnapshot) {

                                    if (matchInfo.exists()) {
                                        Log.d("Flow", "2")

                                        val battingTeamId: String
                                        val bowlingTeamId: String
                                        val bowlTeamName: String
                                        val batTeamLogo: String
                                        val bowlTeamLogo: String

                                        val match_Id =
                                            matchInfo.child("matchId").value.toString()
                                        val batTeamName =
                                            matchInfo.child("battingTeamName").value.toString()
                                        val team_A_Id =
                                            matchInfo.child("team_A_Id").value.toString()
                                        val team_B_Id =
                                            matchInfo.child("team_B_Id").value.toString()
                                        val match_type =
                                            matchInfo.child("matchType").value.toString()
                                        val match_overs =
                                            matchInfo.child("matchOvers").value.toString()
                                        val team_A_Name =
                                            matchInfo.child("team_A_Name").value.toString()
                                        val team_B_Name =
                                            matchInfo.child("team_B_Name").value.toString()
                                        val team_A_Logo =
                                            matchInfo.child("team_A_Logo").value.toString()
                                        val team_B_Logo =
                                            matchInfo.child("team_B_Logo").value.toString()
                                        val sender = matchInfo.child("sender").value.toString()
                                        if (batTeamName == team_A_Name) {
                                            battingTeamId = team_A_Id
                                            bowlingTeamId = team_B_Id
                                            bowlTeamName = team_B_Name
                                            batTeamLogo = team_A_Logo
                                            bowlTeamLogo = team_B_Logo
                                        } else {
                                            battingTeamId = team_B_Id
                                            bowlingTeamId = team_A_Id
                                            bowlTeamName = team_A_Name
                                            batTeamLogo = team_B_Logo
                                            bowlTeamLogo = team_A_Logo
                                        }
                                        secondInningScore(matchId)

                                        //FirstInning
                                        val db_RefFirstInning = FirebaseDatabase.getInstance()
                                            .getReference("/MatchScore/$matchId/FirstInning")

                                        db_RefFirstInning.addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {}

                                            override fun onDataChange(firstInningData: DataSnapshot) {
                                                if (firstInningData.exists()) {
                                                    Log.d("Flow", "3")
                                                    GlobalVariable.LiveMatchCurrentInning =
                                                        firstInningData.child("CurrentInning")
                                                            .value.toString()
                                                    GlobalVariable.LiveScoreFirstInning =
                                                        firstInningData.child("inningScore")
                                                            .value.toString()
                                                            .toInt()
                                                    GlobalVariable.LiveWicketsFirstInning =
                                                        firstInningData.child("wickets")
                                                            .value.toString().toInt()
                                                    GlobalVariable.LiveOversFirstInning =
                                                        firstInningData.child("overs")
                                                            .value.toString().toInt()
                                                    GlobalVariable.LiveOverBallsFirstInning =
                                                        firstInningData.child("over_balls")
                                                            .value.toString()
                                                            .toInt()

                                                    if (GlobalVariable.LiveMatchCurrentInning == "FirstInning") {

                                                        GlobalVariable.LiveMatchCurrentDetails =
                                                            firstInningData.child("MatchCurrentDetail")
                                                                .value.toString()
                                                        GlobalVariable.LiveCRR =
                                                            firstInningData.child("crr")
                                                                .value.toString().toFloat()
                                                        GlobalVariable.LiveRRR = 0f

                                                        GlobalVariable.LiveScoreSecondInning = 0
                                                        GlobalVariable.LiveWicketsSecondInning = 0
                                                        GlobalVariable.LiveOversSecondInning = 0
                                                        GlobalVariable.LiveOverBallsSecondInning = 0
                                                    }
                                                    Log.d(
                                                        "MSG1",
                                                        match_Id
                                                    )
                                                    Log.d(
                                                        "MSG1",
                                                        "${GlobalVariable.LiveScoreFirstInning}"
                                                    )
                                                    Log.d(
                                                        "MSG1",
                                                        "${GlobalVariable.LiveScoreSecondInning}"
                                                    )

                                                    liveMatchAdapter.add(
                                                        LiveMatchViewHolder(
                                                            match_Id,
                                                            battingTeamId,
                                                            bowlingTeamId,
                                                            match_type,
                                                            match_overs,
                                                            batTeamName,
                                                            bowlTeamName,
                                                            batTeamLogo,
                                                            bowlTeamLogo,
                                                            sender,
                                                            GlobalVariable.LiveScoreFirstInning,
                                                            GlobalVariable.LiveWicketsFirstInning,
                                                            GlobalVariable.LiveOversFirstInning,
                                                            GlobalVariable.LiveOverBallsFirstInning,
                                                            GlobalVariable.LiveScoreSecondInning,
                                                            GlobalVariable.LiveWicketsSecondInning,
                                                            GlobalVariable.LiveOversSecondInning,
                                                            GlobalVariable.LiveOverBallsSecondInning,
                                                            GlobalVariable.LiveMatchCurrentDetails,
                                                            GlobalVariable.LiveCRR,
                                                            GlobalVariable.LiveRRR,

                                                            this@Dashboard
                                                        )
                                                    )
                                                }
                                            }
                                        })

                                        empty_match_card_on_dashboard.visible = false
                                    }
                                }
                            })
                    }
                }



        else{
            fetchUpcomingMatchDetails(currentPlayer)
        }
    }})

        /*   fun resumeScoringActivity(position: Int) {
            val item = liveMatchAdapter.getItem(position) as LiveMatchViewHolder


            val team_A_name = item.team_A_Name
            val team_B_name = item.team_B_Name
            val team_A_logo = item.team_A_Logo
            val team_B_logo = item.team_B_Logo

            startActivity<MatchScoringActivity>(
                "match_Id" to match_id,
                "team_A_Id" to team_A_id,
                "team_B_Id" to team_B_id,
                "team_A_Name" to team_A_name,
                "team_B_Name" to team_B_name,
                "team_A_Logo" to team_A_logo,
                "team_B_Logo" to team_B_logo
            )

        }

*/
    }

    private fun secondInningScore(match_Id: String){

        //SecondInning
        val db_RefSecond =
            FirebaseDatabase.getInstance()
                .getReference("/MatchScore/$match_Id/SecondInning")
        db_RefSecond.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    Log.d("Flow", "4")
                    Log.d("MSG1", "SecondInning")
                    if (p0.exists()) {
                        GlobalVariable.LiveScoreSecondInning =
                            p0.child("inningScore")
                                .value.toString().toInt()
                        Log.d(
                            "MSG1",
                            "${GlobalVariable.LiveScoreSecondInning}"
                        )
                        GlobalVariable.LiveWicketsSecondInning =
                            p0.child("wickets")
                                .value.toString()
                                .toInt()
                        GlobalVariable.LiveOversSecondInning =
                            p0.child("overs")
                                .value.toString()
                                .toInt()
                        GlobalVariable.LiveOverBallsSecondInning =
                            p0.child("over_balls")
                                .value.toString()
                                .toInt()
                        GlobalVariable.LiveMatchCurrentDetails =
                            p0.child("MatchCurrentDetail")
                                .value.toString()
                        GlobalVariable.LiveCRR =
                            p0.child("crr")
                                .value.toString()
                                .toFloat()
                        GlobalVariable.LiveRRR =
                            p0.child("rrr")
                                .value.toString()
                                .toFloat()

                    }
                }
            }
        )
    }

    class LiveMatchViewHolder(

        var match_Id: String,
        var team_A_Id: String,
        var team_B_Id: String,
        var match_type: String,
        var match_overs: String,
        var batTeamName: String,
        var bowlTeamName: String,
        var batTeamLogo: String,
        var bowlTeamLogo: String,
        var sender: String,
        var liveScoreFirstInning: Int,
        var liveWicketsFirstInning: Int,
        var liveOversFirstInning: Int,
        var liveOverBallsFirstInning: Int,
        var liveScoreSecondInning: Int,
        var liveWicketsSecondInning: Int,
        var liveOversSecondInning: Int,
        var liveOverBallsSecondInning: Int,
        var liveCurrentMatchDetails: String,
        var liveCurrentCRR: Float,
        var liveCurrentRRR: Float,
        val ctx: Dashboard
    ) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.match_card_on_dashboard
        }

        private fun makeViewsInvisible(vararg view: View) {
            for (v in view) {
                v.visible = false
            }
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            Log.d("MSG1","Card is bind")
            viewHolder.itemView.match_cardView



//            if (ctx.currentPlayer != sender) {
//                makeViewsInvisible(viewHolder.itemView.resume_scoring)
//            }
            makeViewsInvisible(viewHolder.itemView.resume_scoring)
            viewHolder.itemView.resume_scoring.setOnClickListener { v ->
                run {
                    ctx.startTossActivity(position)
                }
            }
            viewHolder.itemView.team_A_score_match_card.text = liveScoreFirstInning.toString()
            viewHolder.itemView.team_A_wickets_match_card.text = liveWicketsFirstInning.toString()
            viewHolder.itemView.team_A_current_over_match_card.text =
                liveOversFirstInning.toString()
            viewHolder.itemView.team_A_current_over_balls_match_card.text =
                liveOverBallsFirstInning.toString()
            viewHolder.itemView.team_A_total_overs_match_card.text = match_overs

            viewHolder.itemView.team_B_score_match_card.text = liveScoreSecondInning.toString()
            viewHolder.itemView.team_B_wickets_match_card.text = liveWicketsSecondInning.toString()
            viewHolder.itemView.team_B_current_over_match_card.text =
                liveOversSecondInning.toString()
            viewHolder.itemView.team_B_current_over_balls_match_card.text =
                liveOverBallsSecondInning.toString()
            viewHolder.itemView.team_B_total_overs_match_card.text = match_overs


            viewHolder.itemView.match_detail_text_view_match_card.text = liveCurrentMatchDetails
            viewHolder.itemView.current_run_rate_on_match_card.text = liveCurrentCRR.toString()
            viewHolder.itemView.required_run_rate_on_match_card.text = liveCurrentRRR.toString()
            viewHolder.itemView.title_of_match.text = match_type
            viewHolder.itemView.team_A_name_match_card.text = batTeamName
            viewHolder.itemView.team_B_name_match_card.text = bowlTeamName

            val logo_team_A =
                viewHolder.itemView.findViewById<ImageView>(R.id.team_A_logo_match_card)
            Picasso.get().load(batTeamLogo).into(logo_team_A)

            val logo_team_B =
                viewHolder.itemView.findViewById<ImageView>(R.id.team_B_logo_match_card)
            Picasso.get().load(bowlTeamLogo).into(logo_team_B)

        }


    }
    //Live Match Card End


}