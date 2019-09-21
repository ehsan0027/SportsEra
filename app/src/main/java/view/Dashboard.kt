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
import com.example.sportsplayer.MainActivity
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
import kotlinx.android.synthetic.main.my_team_list_ondashboard.view.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import view.ProfilePackage.Profile
import view.fragment.SearchTeamFragment
import view.match.MatchDetails
import view.team.TeamDetailActivity

@Suppress("DEPRECATION")
class Dashboard:AppCompatActivity(), SearchTeamFragment.OnFragmentInteractionListener
{

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val CODE_IMAGE_GALLERY=1
    val SAMPLE_CROPPED_IMAGE_NAME="SampleCropImage"
    private var selectedProfileUri: Uri? = null


    private lateinit var currentPlayer:String
   private lateinit var searchTeamFragment: SearchTeamFragment
    val teamAdapter=GroupAdapter<ViewHolder>()

    val upcomingMatchAdapter=GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)

        currentPlayer = FirebaseAuth.getInstance().uid.toString()


        teamAdapter.setOnItemClickListener { item, view ->

            val team=item as MyTeamOnDashboard
            Log.d("Dashboard_TeamName",team.teamName)
            Log.d("Dashboard_TeamCaptain",team.captainId)
            Log.d("Dashboard_TeamCity",team.teamCity)


            startActivity<TeamDetailActivity>(
                "teamId" to team.teamId,
                "teamLogo" to team.teamLogo,
                "teamName" to team.teamName,
                "teamCity" to team.teamCity,
                "captainId" to team.captainId
                )

        }


        //retrieve team data from the database
        fetchTeamFromDatabase(currentPlayer)

        //retrieve team data from the database
        fetchUpcomingMatchDetails(currentPlayer)

        //get the instance of SearchTeamFragment
        searchTeamFragment= SearchTeamFragment()

        //Listener to check the fragments on the Stack
        supportFragmentManager.addOnBackStackChangedListener {
            if(supportFragmentManager.backStackEntryCount ==0)
            {
                makeViewsVisible(dashboard_layout)
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK)

            when(requestCode){

            }

    }

    fun getUserInfo()
    {

        val playerRef= FirebaseDatabase.getInstance().getReference("/PlayerBasicProfile/$currentPlayer")
        playerRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                val playerImage=p0.child("profile_img").value.toString()
                val playerName=p0.child("name").value.toString()
                val playingRole=p0.child("playing_role").value.toString()
                val playerAge=p0.child("age").value.toString()
                val playerCity=p0.child("Location").value.toString()


                Picasso
                    .get()
                    .load(playerImage)
                    .fit() // use fit() and centerInside() for making it memory efficient.
                    .centerInside()
                    .into(profile_Image_DashboardActivity)
                playerName_DashboardActivity.text=playerName
                playerRole_DashboardActivity.text=playingRole
                playerAge_DashboardActivity.text=playerAge
                playerCity_DashboardActivity.text=playerCity

            }
        })

        val playerRefRuns= FirebaseDatabase.getInstance().getReference("/BattingStats/$currentPlayer")
        playerRefRuns.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                val playerRuns=p0.child("runs").value.toString()

                batting_figures_dashboard.text=playerRuns

            }
        })

        val playerRefWickets= FirebaseDatabase.getInstance().getReference("/BowlingStats/$currentPlayer")
        playerRefWickets.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                val playerWickets=p0.child("wicket").value.toString()

                bowling_figures_dashboard.text=playerWickets

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        val playerRefMatches= FirebaseDatabase.getInstance().getReference("/BattingStats/$currentPlayer")
        playerRefMatches.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {


                val playerMatches=p0.child("matches").value.toString()

                matches_figures_Dashboard.text=playerMatches


            }
        })
    }


    private fun calculateAge(){


        val playerRef= FirebaseDatabase.getInstance().getReference("/PlayerBasicProfile/$currentPlayer")
        playerRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                val playerDob=p0.child("dateOfBirth").value.toString()

                


            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
           val inflater= menuInflater
           inflater.inflate(R.menu.dashboard_menu,menu)
        val menuItem:MenuItem=menu!!.findItem(R.id.actionbar_search)
        val searchView=menuItem.actionView as SearchView
        searchView.setOnSearchClickListener {
            makeViewsInvisible(dashboard_layout)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer,searchTeamFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("searchTeamFragment")
                .commit()
        }


        return super.onCreateOptionsMenu(menu)


    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId)
        {
            R.id.profile->startActivity<Profile>()
            R.id.editProfile->{
                startActivity<EditProfile>()
            }//Upcoming Matches Activity
            R.id.startMatch->{
               startActivity<MatchDetails>()
            }
            R.id.signOut->signOutUser()
            R.id.createTeam->startActivity<TeamRegistration>()
            R.id.create_team_Button->startActivity<TeamRegistration>()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        getUserInfo()
    }

    private fun makeViewsInvisible(vararg view:View)
    {
        for(v in view)
        {
            v.visible=false
        }
    }

        private fun makeViewsVisible(vararg view:View)
        {
            for(v in view)
            {
                v.visible=true
            }
        }

fun makeLayoutVisible()
{
    makeViewsVisible(dashboard_layout)
    return
}


    override fun onBackPressed() {

        if(supportFragmentManager.backStackEntryCount>0)
        {
            supportFragmentManager.popBackStackImmediate()
        }

        else {

            super.onBackPressed()
            finish()
        }
    }

    private fun signOutUser() {
         val m= FirebaseAuth.getInstance()
         m.signOut()
        startActivity<MainActivity>()
    }

    //Team Card Started

private fun fetchTeamFromDatabase(playerId:String)
{

val teamRef=FirebaseDatabase.getInstance()
val playersTeamReference=FirebaseDatabase.getInstance().getReference("/PlayersTeam/$playerId")
    playersTeamReference.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {}
        override fun onDataChange(p0: DataSnapshot) {
           if(p0.exists())
           {
               p0.children.forEach{
                   val teamId=it.key
                    teamRef.getReference("/Team/$teamId").also { task ->
                       task.addListenerForSingleValueEvent(object:ValueEventListener{
                           override fun onCancelled(p0: DatabaseError) {
                               TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                           }

                           override fun onDataChange(p0: DataSnapshot) {
                               //get the actual Team (Name and Logo)
                               val team_Id=p0.child("teamId").value.toString()
                               val teamLogo=p0.child("teamLogo").value.toString()
                               val teamName=p0.child("teamName").value.toString()
                               val captainId=p0.child("captainId").value.toString()
                               val teamCity=p0.child("city").value.toString()

                               //cardView color
                            //   val red=(10..230).random()
                            //   val green=(10..230).random()
                            //   val blue=(10..230).random()
                            //   val color= Color.argb(255,red,green,blue)
                               teamAdapter.add(MyTeamOnDashboard(teamLogo,teamName,captainId,teamCity,team_Id))
                           }

                       })
                   }
               }

               dashboard_team_recyclerView.adapter=teamAdapter

           }
        }
    })
}

    class MyTeamOnDashboard(var teamLogo:String,
                            var teamName:String,
                            var captainId:String,
                            var teamCity:String,
                            var teamId:String):Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.my_team_list_ondashboard
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            val logo=viewHolder.itemView.findViewById<ImageView>(R.id.my_team_logo_dashboard)
            Picasso.get().load(teamLogo).into(logo)
            viewHolder.itemView.my_team_name_dashboard.text=teamName

        }


    }

    //Team Card End

//Upcoming Match Card Started


    private fun fetchUpcomingMatchDetails(playerId: String) {

        val newDatabaseRef =
            FirebaseDatabase.getInstance().getReference("PlayersUpcomingMatch/$playerId")
        newDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                longToast("Non Empty Card Shown")
                Log.d("empty","NonEmpty Card Shown")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val scheduleMatchRef =
                        FirebaseDatabase.getInstance().getReference("ScheduledMatch")
                    p0.children.forEach {

                        val upcomingMatchId = it.key
                        scheduleMatchRef.child("$upcomingMatchId").addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {


                                //INSTALL SUCCESSFULLY



                                if(p0.exists()) {

    val match_type = p0.child("matchType").value.toString()
    val match_overs = p0.child("matchOvers").value.toString()
    val match_date = p0.child("matchDate").value.toString()
    val match_time = p0.child("matchTime").value.toString()
    val match_venue = p0.child("matchVenue").value.toString()
    val match_city = p0.child("matchCity").value.toString()
    val team_A_Name = p0.child("team_A_Name").value.toString()
    val team_B_Name = p0.child("team_B_Name").value.toString()
    val team_A_Logo = p0.child("team_A_Logo").value.toString()
    val team_B_Logo = p0.child("team_B_Logo").value.toString()
    val sender = p0.child("sender").value.toString()
    empty_match_card_on_dashboard.visible=false

    upcomingMatchAdapter.add(
        UpcomingMatchViewHolder(
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
                    upcoming_match_card_recycler_view_dashboard?.adapter=upcomingMatchAdapter
                }
            }
        })

    }

    class UpcomingMatchViewHolder(

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
        val ctx:Dashboard) : Item<ViewHolder>() {
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

            if (match_type!="Test"){
                viewHolder.itemView.overs_of_match_upcoming.text = match_overs
            }else{
                makeViewsInvisible(viewHolder.itemView.overs_of_match_upcoming)
            }


            if (ctx.currentPlayer!=sender){
                makeViewsInvisible(viewHolder.itemView.start_match_button)
            }


            viewHolder.itemView.date_of_match_upcoming.text = match_date
            viewHolder.itemView.starting_time_of_match_upcoming.text = match_time
            viewHolder.itemView.venue_of_match_on_match_card_upcoming.text = match_venue
            viewHolder.itemView.city_of_match_on_match_card_upcoming.text = match_city
            viewHolder.itemView.team_A_name_match_card_upcoming.text = team_A_Name
            viewHolder.itemView.team_B_name_match_card_upcoming.text = team_B_Name


            val logo_team_A=viewHolder.itemView.findViewById<ImageView>(R.id.team_A_logo_match_card_upcoming)
            Picasso.get().load(team_A_Logo).into(logo_team_A)

            val logo_team_B=viewHolder.itemView.findViewById<ImageView>(R.id.team_B_logo_match_card_upcoming)
            Picasso.get().load(team_B_Logo).into(logo_team_B)

        }


    }

    //Upcoming Match Card End


}