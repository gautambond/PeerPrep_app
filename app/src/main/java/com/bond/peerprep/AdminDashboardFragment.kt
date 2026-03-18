package com.bond.peerprep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardFragment : Fragment() {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false)

        // Get user name from SharedPreferences
        val prefs    = requireActivity().getSharedPreferences("PeerPrep",
            android.content.Context.MODE_PRIVATE)
        val userName = prefs.getString("userName", "Admin")
        view.findViewById<TextView>(R.id.admin_welcome).text = "Welcome $userName 👑"

        // Sections
        val studentsSection    = view.findViewById<LinearLayout>(R.id.students_section)
        val instructorsSection = view.findViewById<LinearLayout>(R.id.instructors_section)
        val filesSection       = view.findViewById<LinearLayout>(R.id.files_section)

        // Tabs
        val tabs = view.findViewById<TabLayout>(R.id.admin_tabs)
        tabs.addTab(tabs.newTab().setText("Students"))
        tabs.addTab(tabs.newTab().setText("Instructors"))
        tabs.addTab(tabs.newTab().setText("Files"))

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        studentsSection.visibility    = View.VISIBLE
                        instructorsSection.visibility = View.GONE
                        filesSection.visibility       = View.GONE
                    }
                    1 -> {
                        studentsSection.visibility    = View.GONE
                        instructorsSection.visibility = View.VISIBLE
                        filesSection.visibility       = View.GONE
                    }
                    2 -> {
                        studentsSection.visibility    = View.GONE
                        instructorsSection.visibility = View.GONE
                        filesSection.visibility       = View.VISIBLE
                        loadFiles(view)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Load data
        loadStudents(view)
        loadInstructors(view)

        // Logout
        view.findViewById<com.google.android.material.button.MaterialButton>(
            R.id.admin_logout).setOnClickListener {
            auth.signOut()
            prefs.edit().clear().apply()
            findNavController().navigate(R.id.loginFragment)
        }

        return view
    }

    // ✅ Load all students
    private fun loadStudents(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.students_container)
        val totalView = view.findViewById<TextView>(R.id.total_students)

        db.collection("users")
            .whereEqualTo("role", "student")
            .get()
            .addOnSuccessListener { docs ->
                totalView.text = docs.size().toString()
                container.removeAllViews()

                for (doc in docs) {
                    val name         = doc.getString("name") ?: ""
                    val email        = doc.getString("email") ?: ""
                    val instructorId = doc.getString("instructorId") ?: "Not assigned"

                    val cardView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_user_card, container, false)

                    cardView.findViewById<TextView>(R.id.user_name).text = name
                    cardView.findViewById<TextView>(R.id.user_email).text = email
                    cardView.findViewById<TextView>(R.id.user_role).text = "🎓 Student"
                    cardView.findViewById<TextView>(R.id.user_extra).text =
                        "Instructor: $instructorId"

                    container.addView(cardView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Load all instructors
    private fun loadInstructors(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.instructors_container)
        val totalView = view.findViewById<TextView>(R.id.total_instructors)

        db.collection("users")
            .whereEqualTo("role", "instructor")
            .get()
            .addOnSuccessListener { docs ->
                totalView.text = docs.size().toString()
                container.removeAllViews()

                for (doc in docs) {
                    val name     = doc.getString("name") ?: ""
                    val email    = doc.getString("email") ?: ""
                    val subjects = doc.get("subjects") as? List<*> ?: emptyList<String>()

                    val cardView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_user_card, container, false)

                    cardView.findViewById<TextView>(R.id.user_name).text = name
                    cardView.findViewById<TextView>(R.id.user_email).text = email
                    cardView.findViewById<TextView>(R.id.user_role).text = "👨‍🏫 Instructor"
                    cardView.findViewById<TextView>(R.id.user_extra).text =
                        "Subjects: ${subjects.joinToString(", ")}"

                    container.addView(cardView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Load all files
    private fun loadFiles(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.files_container)

        db.collection("files")
            .get()
            .addOnSuccessListener { docs ->
                container.removeAllViews()

                for (doc in docs) {
                    val name         = doc.getString("name") ?: ""
                    val type         = doc.getString("type") ?: ""
                    val instructorId = doc.getString("instructorId") ?: ""

                    val cardView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_user_card, container, false)

                    cardView.findViewById<TextView>(R.id.user_name).text = name
                    cardView.findViewById<TextView>(R.id.user_email).text = "Type: $type"
                    cardView.findViewById<TextView>(R.id.user_role).text = "📁 File"
                    cardView.findViewById<TextView>(R.id.user_extra).text =
                        "Instructor: $instructorId"

                    container.addView(cardView)
                }
            }
    }
}