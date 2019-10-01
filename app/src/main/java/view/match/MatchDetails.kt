package view.match

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsplayer.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.match_details_layout.*
import model.MatchInfo
import org.jetbrains.anko.*
import view.GlobalVariable
import view.match.ui.SearchTeamForMatch
import view.team.TeamDetailActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MatchDetails : AppCompatActivity(){


    lateinit var matchType:String
    lateinit var ballType:String
    lateinit var team_B_id:String
    lateinit var team_B_Logo:String
    lateinit var team_B_Name:String
    lateinit var newRequestId:String
    lateinit var captain_B_Id:String
    var databaseRef: FirebaseDatabase?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.match_details_layout)
        supportActionBar?.title="Match Details"
        //[Team id fields initialization]

        team_B_id=""

        databaseRef= FirebaseDatabase.getInstance()

        val autoCompleteTextViewAdapter=ArrayAdapter(this,android.R.layout.select_dialog_item,GlobalVariable.listOfPakistanCities)
        matchCity_Match_Details.threshold=1
        matchCity_Match_Details.setAdapter(autoCompleteTextViewAdapter)
        //Click Listener for Team_A and Team_B
      //  team_A_StartMatchActivity.setOnClickListener { selectTeamA() }
        team_B_Match_Details.setOnClickListener {

            startActivityForResult<SearchTeamForMatch>(team_B)
        }

        //RadioGroup Click Listener
        matchType_radio_group.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = find(checkedId)
            matchType=radio.text.toString()
            when(matchType){
                "Test"->{ matchOvers_Match_Details.visibility= View.GONE }
                "Limited Overs"->{matchOvers_Match_Details.visibility=View.VISIBLE}
            }
            toast("Match Type: $matchType")
        }
        ballType_radio_group.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = find(checkedId)
            ballType=radio.text.toString()
            toast("Ball Type: $ballType")

        }

        //matchTime Click and FocusChange Listener
        matchTime_Match_Details.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
            { setMatchTime() }
        }
        matchTime_Match_Details.setOnClickListener {
            setMatchTime()
        }
       //matchDate Click and FocusChange Listener
        matchDate_Match_Details.setOnClickListener{
            setMatchDate()
        }
    matchDate_Match_Details.setOnFocusChangeListener { _, hasFocus ->
        if(hasFocus)
        {setMatchDate()}
    }

    //saveMatch Button Click Listener
        send_challenge_request_button_match_details.setOnClickListener {
            sendRequestForMatch()
            toast("Request For Challenge Sent")
        }


    }


    override fun onResume() {
        super.onResume()

        val teamData=intent.extras
        if(teamData!==null)
        {
            val teamId=teamData.getString("team_A_Id")
            val teamLogo=teamData.getString("team_A_Logo")
            Log.d("Select Team Activity",teamId)
        }else{
            Log.d("Select Team Activity","  NULL")
        }


    }



    private fun setMatchDate()
    {
        val cal = Calendar.getInstance()
        // cal.add(Calendar.YEAR)
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            matchDate_Match_Details.setText(sdf.format(cal.time))
        }

        DatePickerDialog(
            this, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()

    }


    private fun setMatchTime()
    {
       val matchHour=0
        val matchMinute=0
        val timePicker = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
                matchTime_Match_Details.setText("$hourOfDay : $minutes")
            },matchHour,matchMinute,false)
        timePicker.show()

    }

    private fun teamBcaptain(teamId:String){
        val newDatabaseReference=FirebaseDatabase.getInstance().reference
        newDatabaseReference.child("Team").child(teamId).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                     captain_B_Id= p0.child("captainId").value.toString()
                }
            }

        })
    }

private fun sendRequestForMatch() {

    val team_A_id = intent.getStringExtra("team_A_Id")
    val team_A_Logo=intent.getStringExtra("team_A_Logo")
    val team_A_Name=intent.getStringExtra("team_A_Name")
    val team_A_City = intent.getStringExtra("teamCity_A")
    val team_A_Captain = intent.getStringExtra("captainId_A")

    Log.d("team",team_A_id)
    var overs = matchOvers_Match_Details.text.toString().trim()
    val city = matchCity_Match_Details.text.toString().trim()
    val venue = matchVenue_Match_Details.text.toString().trim()
    val date = matchDate_Match_Details.text.toString().trim()
    val time = matchTime_Match_Details.text.toString().trim()
    val squad = squad_count_Match_Details.text.toString().trim()
    if (matchType=="Test"){
        overs = "450"
    }
    if (overs.isNotEmpty()
        && city.isNotEmpty()
        && venue.isNotEmpty()
        && date.isNotEmpty()
        && time.isNotEmpty()
        && squad.isNotEmpty()
        && matchType.isNotEmpty()
        && ballType.isNotEmpty()
        && team_A_id.isNotEmpty()
        && team_B_id.isNotEmpty()
        && captain_B_Id.isNotEmpty()
    ) {
        val newDatabaseReference=databaseRef?.reference

        //generate unique id for match
        val matchId=newDatabaseReference?.push()?.key!!
        Log.d("requestId ",matchId)
        newRequestId=matchId

        val newMatchInvite=MatchInfo(matchType,overs,city,venue,date,time,ballType,squad,team_A_id,team_B_id,team_A_Name,team_B_Name,team_A_Logo,team_B_Logo,matchId,team_A_Captain,captain_B_Id,"","")

        Log.d("Team_A_Id ",team_A_id)
        Log.d("team_B_Id ",team_B_id)
        val addRequest=HashMap<String,Any>()
        addRequest["/MatchInfo/$matchId"]=newMatchInvite
        addRequest["/TeamsMatchInfo/$team_A_id/$matchId"]=true
        addRequest["/TeamsMatchInfo/$team_B_id/$matchId"]=true

        newDatabaseReference.updateChildren(addRequest).addOnCompleteListener { task->
            if(task.isSuccessful){
                Log.d("MatchSaved ",matchId)
             val setStatus=FirebaseDatabase.getInstance().reference
                val m_status=HashMap<String,Any>()
                m_status["/MatchInfo/$matchId/matchStatus"]="Request"

                setStatus.updateChildren(m_status).addOnCompleteListener {
                    task -> if(task.isSuccessful)
                {
                    toast("Status Changed")
                }
                }
                toast("Request Sent")
                //progressDialog.dismiss()
                startActivity<TeamDetailActivity>("team_A_Id" to team_A_id,
                    "team_A_Logo" to team_A_Logo,
                    "team_A_Name" to team_A_Name,
                    "team_A_City" to team_A_City,
                    "captainId_A" to team_A_Captain,
                "team_B_Id" to team_B_id
                )
                finish()
            }
        }.addOnFailureListener { exception ->
            toast(exception.localizedMessage.toString())
            //progressDialog.dismiss()

        }





    } else {
        alert {
            title="Missing Field"
            message="Please Fill All Provided Fields"
            okButton {
                dialog ->
                dialog.dismiss()
            }
        }.show()
         }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_CANCELED)
        {Log.d("resultCode","canceled")}
        if (data != null && resultCode == Activity.RESULT_OK)
        {
            when(requestCode)
            {
                team_B->{
                    val team2_id = data.getStringExtra("team_B_Id")
                    val team2_logo = data.getStringExtra("team_B_Logo")
                    val team2_Name = data.getStringExtra("team_B_Name")
                    Log.d("MatchDetails_Team_B",team2_id)
                    if (team2_id.isNotEmpty() && team2_logo.isNotEmpty() && team2_Name.isNotEmpty() )
                    {
                        team_B_id=team2_id
                        team_B_Logo=team2_logo
                        team_B_Name=team2_Name

                        teamBcaptain(team_B_id)

                        Picasso.get().load(team2_logo).into(team_B_Match_Details)
                        selected_Team_B_Name_Match_Details.text=team2_Name
                    }
                }
            }
        }
    }






    companion object{
        const val team_B=2
    }


}
