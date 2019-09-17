package view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.NonNull
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
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.dashboard_activity.*
import kotlinx.android.synthetic.main.my_team_list_ondashboard.view.*
import org.jetbrains.anko.startActivity
import view.fragment.SearchTeamFragment
import view.match.StartMatchActivity
import view.matchscoring.MatchScoringActivity
import view.team.TeamDetailActivity
import java.io.File

@Suppress("DEPRECATION")
class Dashboard:AppCompatActivity(), SearchTeamFragment.OnFragmentInteractionListener
{
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val CODE_IMAGE_GALLERY=1

    private var mAuth:FirebaseAuth?=null
   private lateinit var searchTeamFragment: SearchTeamFragment
    val adapter=GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)
        mAuth= FirebaseAuth.getInstance()


        adapter.setOnItemClickListener { item, view ->
            val team=item as MyTeamOnDashboard
            Log.d("Dashboard_TeamName",team.teamName)
            Log.d("Dashboard_TeamCaptain",team.teamCaptain)
            Log.d("Dashboard_TeamCity",team.teamCity)

            startActivity<TeamDetailActivity>(
                "teamId" to team.teamId,
                "teamLogo" to team.teamLogo,
                "teamName" to team.teamName,
                "teamCity" to team.teamCity
                )
        }
            cropedImage.setOnClickListener {
             startActivity<MatchScoringActivity>()
            }

            //retrieve team data from the database
        fetchTeamFromDatabase()

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
        val uid=mAuth?.uid

        val playerRef= FirebaseDatabase.getInstance().getReference("/PlayerBasicProfile/$uid")
        playerRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                val playerImage=p0.child("profile_img").value.toString()
                val playerName=p0.child("name").value.toString()
                Picasso
                    .get()
                    .load(playerImage)
                    .fit() // use fit() and centerInside() for making it memory efficient.
                    .centerInside()
                    .into(profile_Image_DashboardActivity)
                playerName_DashboardActivity.text=playerName


            }
        })
    }


    fun selectProfileImage() {
        val gallery = Intent(Intent.ACTION_PICK)
        gallery.type = "image/*"
        startActivityForResult(gallery,CODE_IMAGE_GALLERY)

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
            R.id.upcomingMatch->{
                makeViewsInvisible(dashboard_layout)
            }//Upcoming Matches Activity
            R.id.startMatch->{ startActivity<StartMatchActivity>() }
            R.id.signOut->signOutUser()
            R.id.editProfile->startActivity<EditProfileActivity>()
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

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun signOutUser() {
        mAuth?.signOut()
        startActivity<MainActivity>()
    }

private fun fetchTeamFromDatabase()
{
    val playerId =mAuth?.uid.toString()
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
                               val teamCaptain=p0.child("captainName").value.toString()
                               val teamCity=p0.child("city").value.toString()

                               //cardView color
                               val red=(10..230).random()
                               val green=(10..230).random()
                               val blue=(10..230).random()
                               val color= Color.argb(255,red,green,blue)
                               adapter.add(MyTeamOnDashboard(teamLogo,teamName,teamCaptain,teamCity,team_Id,color))
                           }

                       })
                   }
               }

               dashboard_team_recyclerView.adapter=adapter

           }
        }
    })
}


    class MyTeamOnDashboard(var teamLogo:String,
                            var teamName:String,
                            var teamCaptain:String,
                            var teamCity:String,
                            var teamId:String,
                            val color:Int):Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.my_team_list_ondashboard
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.team_logo_cardView.setCardBackgroundColor(color)
            val logo=viewHolder.itemView.findViewById<ImageView>(R.id.my_team_logo_dashboard)
            Picasso.get().load(teamLogo).into(logo)
            viewHolder.itemView.my_team_name_dashboard.text=teamName

        }


    }






}