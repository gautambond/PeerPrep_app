package com.bond.peerprep.courses



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bond.peerprep.R

class CodingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_coding, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ 1 — Subject Card (hero section)
        val subjectCard = SubjectCardFragment.newInstance(
            title = "Live online Coding classes for kids",
            subtitle = "Recommended for grades KG to 12",
            point1 = "Build logic & algorithmic thinking",
            point2 = "Create games, websites & apps",
            point3 = "Learn real world programming",
            image = R.drawable.coding1
        )

        // ✅ 2 — Subject Grid
        val subjectGrid = SubjectGridFragment.newInstance(
            title = "Why Coding?",
            subtitle = "Practical skills that prepare kids for the digital future.",
            card1Title = "Block-Based Coding",
            card1Desc = "Learn logic and sequencing with drag-and-drop programming.",
            card1Image = R.drawable.blockbasedcoding,
            card2Title = "Text-Based Coding",
            card2Desc = "Build websites, games and apps using Python & JavaScript.",
            card2Image = R.drawable.textbasedcoding,
            card3Title = "Real World Projects",
            card3Desc = "Apply coding to real scenarios like games and interactive web apps.",
            card3Image = R.drawable.realworld
        )

        // ✅ 3 — Curriculum
        val curriculum = CurriculumFragment.newInstance(
            title = "Featured Learning Tracks",
            items = arrayListOf(
                CurriculumItem(
                    subject = "Scratch Coding",
                    desc = "Create interactive animations and games using visual coding.",
                    image = R.drawable.textbasedcoding
                ),
                CurriculumItem(
                    subject = "Python & AI",
                    desc = "Learn Python, loops, functions and AI projects.",
                    image = R.drawable.realworld
                ),
                CurriculumItem(
                    subject = "Web Development",
                    desc = "Build modern websites using HTML, CSS, JavaScript & React.",
                    image = R.drawable.blockbasedcoding
                ),

            )
        )

        // ✅ 4 — Why Us
        val whyUs = WhyUsFragment.newInstance(
            title = "Most effective and loved platform for kids to unlock their coding potential",
            text1 = "Mastery based in-depth curriculum",        icon1 = R.drawable.ic_grid,
            text2 = "Hands-on project based learning",         icon2 = R.drawable.ic_computer,
            text3 = "Gamified learning platform with 1000s of exercises",   icon3 = R.drawable.ic_games,
            text4 = "Catering to multiple international board systems",  icon4 = R.drawable.ic_trophy,
            text5 = "Highly qualified and certified mentors",        icon5 = R.drawable.ic_check,
            text6 = "STEM.ORG accredited educational experience\n" +
                    "\n",    icon6 = R.drawable.ic_star
        )

        // ✅ Add all fragments to page
        childFragmentManager.beginTransaction()
            .add(R.id.coding_subject_card, subjectCard)
            .add(R.id.coding_subject_grid, subjectGrid)
            .add(R.id.coding_curriculum, curriculum)
            .add(R.id.coding_why_us, whyUs)
            .commit()
    }
}