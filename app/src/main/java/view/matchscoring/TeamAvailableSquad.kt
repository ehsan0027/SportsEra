package view.matchscoring

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsplayer.R

class TeamAvailableSquad : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_available_squad)
    }


    override fun onStart() {
        super.onStart()
    val team_Id=intent.getStringExtra("teamId")
        showAvailableSquad(team_Id)
    }

    private fun showAvailableSquad(teamId: String?) {


    }
}
