package view.match.inningsTabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class InningsSectionPager ( fm: FragmentManager,private val matchId:String):
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when(position)
        {
            0->{FirstInningTabFragment.newInstance(matchId)}
            1->{SecondInningTabFragment.newInstance(matchId)}
            else->{return FirstInningTabFragment.newInstance(matchId)}
        }
    }

    override fun getCount(): Int {
        return 2

    }

    override fun getPageTitle(position: Int): CharSequence? {

        return when(position)
        {
            0->"1st Inning"
            1->"2nd Inning"

            else->{return "1st Inning"}
        }
    }
}