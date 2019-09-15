package view.team

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsplayer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_team_detail.*
import kotlinx.android.synthetic.main.remove_player_popup.*
import view.fragment.SearchPlayerToAddInTeam
import view.team.ui.SectionPagerAdapter
import view.team.ui.TeamMatchFragment
import view.team.ui.TeamMemberFragment
import view.team.ui.TeamStatsFragment
import org.jetbrains.anko.*

class TeamDetailActivity : AppCompatActivity(), View.OnClickListener,
    TeamStatsFragment.OnFragmentInteractionListener,
    TeamMatchFragment.OnFragmentInteractionListener,
    TeamMemberFragment.OnFragmentInteractionListener
     {



         override fun onFragmentInteraction(uri: Uri) {}

         lateinit var captainId:String

         override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)

             setViewsContent() //setViewsContent

             //assign Click Listener to Button
        addNewPlayer_TeamDetailActivity.setOnClickListener(this)

    }

         private fun getUserInfo(teamId:String)
         {
             val teamRef= FirebaseDatabase.getInstance().getReference("/Team/$teamId")
             teamRef.addListenerForSingleValueEvent(object: ValueEventListener {
                 override fun onCancelled(p0: DatabaseError) {
                     TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                 }
                 override fun onDataChange(p0: DataSnapshot) {
                     captainId=p0.child("captainId").value.toString()
                     val playerRef= FirebaseDatabase.getInstance().getReference("/PlayerBasicProfile/$captainId")
                     playerRef.addListenerForSingleValueEvent(object: ValueEventListener {
                         override fun onCancelled(p0: DatabaseError) {}
                         override fun onDataChange(p0: DataSnapshot) {
                             val captainName=p0.child("name").value.toString()
                             teamCaptain_TeamDetailActivity.text=captainName
                         }
                     })
                 }
             })
         }

         private fun setViewsContent()
         {
             val teamId=intent.getStringExtra("teamId")
             val teamLogo=intent.getStringExtra("teamLogo")
             val teamName=intent.getStringExtra("teamName")
             val teamCity=intent.getStringExtra("teamCity")
             supportActionBar?.title=teamName

             //[groupAdapter initialization]
             val fragmentAdapter=SectionPagerAdapter(teamId,supportFragmentManager)
             viewPager.adapter=fragmentAdapter
             tabLayout.setupWithViewPager(viewPager)

             Picasso.get().load(teamLogo).into(team_logo_TeamDetailActivity)
             teamCity_TeamDetailActivity.text=teamCity
             getUserInfo(teamId)

         }


         private fun checkCaptainValidity()
         {
             val currentPlayer=FirebaseAuth.getInstance().uid
             if(currentPlayer==captainId)
             { val teamId=intent.getStringExtra("teamId")
                 startActivityForResult<SearchPlayerToAddInTeam>(SEARCH_PLAYER,"teamId" to teamId)
             }else {
                 alert {
                     title = "Captain Authentication"
                     message = "Permission Denied,only Team Captain can add new player"
                     okButton { dialog -> dialog.dismiss() }
                 }.show()
             }

         }
    override fun onClick(view: View?) {
        when(view?.id)
        {
            R.id.addNewPlayer_TeamDetailActivity->{
                checkCaptainValidity()
            }

        }
    }

         override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
             super.onActivityResult(requestCode, resultCode, data)

         if(resultCode==Activity.RESULT_OK && data !=null)
         {when(requestCode)
         {
             SEARCH_PLAYER->{setViewsContent()}

         }
         }
         }

         companion object{
             const val SEARCH_PLAYER=1
         }

}
