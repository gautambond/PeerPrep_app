package com.bond.peerprep.utils

import com.bond.peerprep.R
import com.bond.peerprep.model.Content1

object Constants {

    fun getContent1(): MutableList<Content1>{
        val contents=mutableListOf<Content1>()

        val c1=Content1(1,"Subjects and classes",
            "Mathematics: Covering everything from basic arithmetic to algebra, geometry, trigonometry, calculus, and statistics." +
                    "\nScience: Physics, chemistry, and biology taught with visual explanations and strong concept clarity.\n" +
                    "English: Focused on grammar, comprehension, creative writing, literature, and spoken fluency.\n" +
                    "Computer Science: Basics of computing, Python, coding logic, and digital literacy skills." +
                    "\nSocial Studies: Engaging lessons in history, geography, civics, and economics with storytelling methods." +
                    "\nEconomics & Business Studies: Designed for senior students with real-world applications and insights." +
                    "\nCoding & Tech Skills: Introduces young learners to HTML, Python, and logical thinking through hands-on coding activities.",
                R.drawable.subjects
        )
        contents.add(c1)

        val c2= Content1(2,"Special preparation for\ncompetitive exams",
            "Complete coaching for grades KG to 12 — tailored support across all levels, from foundational to advanced." +
                    "\n We cover all major curriculums, including the US Common Core Curriculum, Canadian Provincial Boards, IGCSE, GCSE, 11+ & 12+ (UK), CBSE, ICSE, State Boards, and Middle Eastern international schools." +
                    "\nOur program includes exam-focused training, SAT preparation (available on request for higher grades), and guidance for scholarship and subject-specific competitive exams.",
            R.drawable.exam2
        )
        contents.add(c2)

        val c3= Content1(3,"Students-Centered fee policy",
            "Education comes first." +
                    "\nWe offer flexible fee options with no rigid contracts. " +
                    "\nScholarships and discounts are available, with special offers for meritorious students, siblings, and long-term enrollments." +
                    "\nWe reward consistency and commitment — because we believe in investing in learning, not just tuition." +
                    "\nNo advance payment required.",
            R.drawable.scfp
        )
        contents.add(c3)

        val c4= Content1(4,"Referral and reward Policy",
            "Get 15% off per referral.stack your savings!." +
                    "\nRefer a friend, and once they start their classes,you'll instantly receive 15% off your current month's fee." +
                    "\nIf your total referral discount exceeds 100%, no problem — the extra savings will roll over to the next month's fee." +
                    "\nSimple and hassle-free.",
            R.drawable.referral2
        )
        contents.add(c4)
         val c5= Content1(5,"Flexible Payment mode",
             "We accept payments through all major methods to make the process smooth and convenient for every family." +
                     "\nThis includes international payments (Wise, PayPal, Razorpay,etc)." +
                     "\nBank transfers (NEFT/IMPS).Debit and credit cards." +
                     "\nUPI options like Google Pay, PhonePe, Paytm, and more."
             ,R.drawable.payment2

         )
        contents.add(c5)

        return contents




    }
}