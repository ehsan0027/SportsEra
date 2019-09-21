package view.team.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import view.team.ui.TeamMatchTabs.TeamCompletedMatchesFragment
import view.team.ui.TeamMatchTabs.TeamUpcomingMatchFragment

class TeamMatchSectionPagerAdapter(private val teamId:String,fm:FragmentManager):FragmentPagerAdapter(fm)
{
    override fun getItem(position: Int): Fragment {
        return when(position)
        {
            0->{TeamUpcomingMatchFragment(teamId)}
            1->{TeamCompletedMatchesFragment()}
            else->{return TeamUpcomingMatchFragment(teamId)}
        }
    }

    override fun getCount(): Int {
        return 2

    }

    override fun getPageTitle(position: Int): CharSequence? {

        return when(position)
        {
            0->"Upcoming"
            1->"Completed"
            else->{return "Upcoming"}
        }
    }

}