package com.bond.peerprep.courses


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bond.peerprep.R
import com.google.android.material.button.MaterialButton

class MathsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_maths, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ 1 — Subject Card (hero section)
        val subjectCard = SubjectCardFragment.newInstance(
            title = "Live online Maths classes for kids",
            subtitle = "Recommended for grades KG to 12",
            point1 = "Improve problem solving ability",
            point2 = "Build mental calculation speed",
            point3 = "Master concepts with clarity",
            image = R.drawable.maths2
        )

        // ✅ 2 — Subject Grid
        val subjectGrid = SubjectGridFragment.newInstance(
            title = "Why Maths?",
            subtitle = "Fun, interactive lessons that build real world problem solving ability.",
            card1Title = "School Maths",
            card1Desc = "Core arithmetic, fractions, decimals and problem solving.",
            card1Image = R.drawable.math1,
            card2Title = "Vedic Maths",
            card2Desc = "Fast calculation tricks & mental math mastery.",
            card2Image = R.drawable.vedinmths,
            card3Title = "Logical Reasoning",
            card3Desc = "Puzzles, patterns & analytical thinking.",
            card3Image = R.drawable.resong
        )

        // ✅ 3 — Curriculum
        val curriculum = CurriculumFragment.newInstance(
            title = "Curriculum Highlights",
            items = arrayListOf(
                CurriculumItem(
                    subject = "KG-12",
                    desc = "Foundations of arithmetic, shapes, counting & sorting.",
                    image = R.drawable.math1
                ),
                CurriculumItem(
                    subject = "Competitive Examination",
                    desc = "Number systems, addition, subtraction, patterns & money",
                    image = R.drawable.resong
                ),
                CurriculumItem(
                    subject = "Homework Help",
                    desc = "Fast mental calculation & logical reasoning techniques.",
                    image = R.drawable.vedinmths
                )
            )
        )

        // ✅ 4 — Why Us
        val whyUs = WhyUsFragment.newInstance(
            title = "Why kids love learning Maths with Peer Prep",
            text1 = "Strong conceptual clarity",   icon1 = R.drawable.ic_calculator,
            text2 = "Mental math speed techniques",   icon2 = R.drawable.ic_bulb,
            text3 = "Logical reasoning & puzzle learning",  icon3 = R.drawable.ic_games,
            text4 = "Board exam aligned syllabus", icon4 = R.drawable.ic_star,
            text5 = "Certified math mentors",         icon5 = R.drawable.ic_check,
            text6 = "International curriculum standards",   icon6 = R.drawable.ic_star
        )

        // ✅ Add all fragments to page
        childFragmentManager.beginTransaction()
            .add(R.id.maths_subject_card, subjectCard)
            .add(R.id.maths_subject_grid, subjectGrid)
            .add(R.id.maths_curriculum, curriculum)
            .add(R.id.maths_why_us, whyUs)
            .commit()


        view.findViewById<MaterialButton>(R.id.instructors_button).setOnClickListener {
            findNavController().navigate(R.id.exploreCoursesFragment)
        }
    }
}