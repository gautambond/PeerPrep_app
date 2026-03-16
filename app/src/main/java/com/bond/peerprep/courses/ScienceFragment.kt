package com.bond.peerprep.courses


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bond.peerprep.R

class ScienceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_science, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ 1 — Subject Card (hero section)
        val subjectCard = SubjectCardFragment.newInstance(
            title = "Live online Science classes for kids",
            subtitle = "Recommended for grades KG to 12",
            point1 = "Hands-on experiments & demos",
            point2 = "Real world scientific learning",
            point3 = "Build curiosity & innovation",
            image = R.drawable.science2
        )

        // ✅ 2 — Subject Grid (Physics, Chemistry, Biology)
        val subjectGrid = SubjectGridFragment.newInstance(
            title = "Why Science?",
            subtitle = "Practical learning that builds curiosity and scientific thinking.",
            card1Title = "Physics",
            card1Desc = "Motion, force, energy and real-world experiments.",
            card1Image = R.drawable.phy,
            card2Title = "Chemistry",
            card2Desc = "Chemical reactions, acids, bases and lab activities.",
            card2Image = R.drawable.che,
            card3Title = "Biology",
            card3Desc = "Human body, plants, animals and life processes.",
            card3Image = R.drawable.boi
        )

        // ✅ 3 — Curriculum (horizontally scrollable cards)
        val curriculum = CurriculumFragment.newInstance(
            title = "Curriculum Highlights",
            items = arrayListOf(
                CurriculumItem(
                    subject = "Chemistry",
                    desc = "Elements, reactions, acids & bases through fun activities.",
                    image = R.drawable.che
                ),
                CurriculumItem(
                    subject = "Biology",
                    desc = "Human body, plants & animals simplified.",
                    image = R.drawable.boi
                ),
                CurriculumItem(
                    subject = "Physics",
                    desc = "Motion, force, energy and real-world experiments.",
                    image = R.drawable.phy
                )
            )
        )

        // ✅ 4 — Why Us (6 icon grid)
        val whyUs = WhyUsFragment.newInstance(
            title = "Why kids love learning Science with Peer Prep",
            text1 = "Hands-on experiments & demonstrations", icon1 = R.drawable.ic_experiment,
            text2 = "Real world scientific applications",    icon2 = R.drawable.ic_world,
            text3 = "Concept clarity with animations",       icon3 = R.drawable.ic_bulb,
            text4 = "CBSE & ICSE aligned curriculum",        icon4 = R.drawable.ic_star,
            text5 = "Certified science mentors",             icon5 = R.drawable.ic_check,
            text6 = "International science standards",       icon6 = R.drawable.ic_star
        )



        // ✅ Add all fragments to page
        childFragmentManager.beginTransaction()
            .add(R.id.science_subject_card, subjectCard)
            .add(R.id.science_subject_grid, subjectGrid)
            .add(R.id.science_curriculum, curriculum)
            .add(R.id.science_why_us, whyUs)

            .commit()
    }
}