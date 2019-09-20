package view.team.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionPagerAdapter(private val teamId:String, private val teamName:String, private val teamLogo:String, private val captainId:String,fm:FragmentManager):FragmentPagerAdapter(fm)
{
    override fun getItem(position: Int): Fragment {
        return when(position)
        {
            0->{TeamMemberFragment(teamId,captainId)}
            1->{TeamRequestMatchFragment(teamId,teamName,teamLogo,captainId)}
            2->{TeamSquadFragment(teamId)}
            3->{TeamStatsFragment()}
            4->{TeamMatchFragment()}
            else->{return TeamMatchFragment()}
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