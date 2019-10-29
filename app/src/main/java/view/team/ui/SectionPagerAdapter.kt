package view.team.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionPagerAdapter(private val team_A_Id:String, private val team_A_Name:String, private val team_A_Logo:String, private val captainId_A:String,fm:FragmentManager):FragmentPagerAdapter(fm)
{
    override fun getItem(position: Int): Fragment {
        return when(position)
        {
            0->{TeamMemberFragment(team_A_Id,captainId_A)}
            1->{TeamRequestMatchFragment(team_A_Id,team_A_Name,team_A_Logo,captainId_A)}
            2->{TeamSquadFragment(team_A_Id,captainId_A)}
            3->{TeamStatsFragment()}
            4->{TeamMatchFragment(team_A_Id)}
            else->{return TeamMatchFragment(team_A_Id)}
        }
    }

    override fun getCount(): Int {
        return 5

    }

    override fun getPageTitle(position: Int): CharSequence? {

        return when(position)
        {
            0->"Member"
            1->"Invites"
            2->"Squad"
            3->"Stats"
            4->"Match"
            else->{return "Match"}
        }
    }

}