package com.bond.peerprep

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class ExploreCoursesFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val allCoursesList = mutableListOf<Map<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_explore_courses, container, false)

        val prefs     = requireActivity().getSharedPreferences("PeerPrep",
            android.content.Context.MODE_PRIVATE)
        val userId    = prefs.getString("userId", "") ?: ""
        val userName  = prefs.getString("userName", "") ?: ""
        val userEmail = prefs.getString("userEmail", "") ?: ""
        val userRole  = prefs.getString("userRole", "") ?: ""

        // Back button
        view.findViewById<android.widget.ImageButton>(R.id.btn_back)
            .setOnClickListener {
                findNavController().popBackStack()
            }

        // Load all courses
        loadAllCourses(view, userId, userName, userEmail, userRole)

        // Search
        view.findViewById<TextInputEditText>(R.id.search_courses)
            .addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterCourses(view, s.toString().trim(),
                        userId, userName, userEmail, userRole)
                }
                override fun afterTextChanged(s: Editable?) {}
            })

        return view
    }

    // ✅ Load all courses
    private fun loadAllCourses(
        view: View,
        userId: String,
        userName: String,
        userEmail: String,
        userRole: String
    ) {
        val container = view.findViewById<LinearLayout>(
            R.id.explore_courses_container)
        val countView = view.findViewById<TextView>(R.id.courses_count)

        container.removeAllViews()
        allCoursesList.clear()

        db.collection("courses")
            .get()
            .addOnSuccessListener { courseDocs ->
                countView.text = "${courseDocs.size()} courses available"

                if (courseDocs.isEmpty) {
                    showEmpty(container, "No courses available yet")
                    return@addOnSuccessListener
                }

                for (courseDoc in courseDocs) {
                    val courseId     = courseDoc.id
                    val courseName   = courseDoc.getString("name") ?: ""
                    val courseDesc   = courseDoc.getString("description") ?: ""
                    val instructorId = courseDoc.getString("instructorId") ?: ""

                    // Get instructor details
                    db.collection("users").document(instructorId)
                        .get()
                        .addOnSuccessListener { instructorDoc ->
                            val instructorName  =
                                instructorDoc.getString("name") ?: ""
                            val instructorEmail =
                                instructorDoc.getString("email") ?: ""

                            // Store for search
                            allCoursesList.add(mapOf(
                                "courseId"        to courseId,
                                "courseName"      to courseName,
                                "courseDesc"      to courseDesc,
                                "instructorId"    to instructorId,
                                "instructorName"  to instructorName,
                                "instructorEmail" to instructorEmail
                            ))

                            addCourseCard(
                                container,
                                courseId,
                                courseName,
                                courseDesc,
                                instructorId,
                                instructorName,
                                instructorEmail,
                                userId,
                                userName,
                                userEmail,
                                userRole
                            )
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Add course card to container
    @SuppressLint("MissingInflatedId")
    private fun addCourseCard(
        container: LinearLayout,
        courseId: String,
        courseName: String,
        courseDesc: String,
        instructorId: String,
        instructorName: String,
        instructorEmail: String,
        userId: String,
        userName: String,
        userEmail: String,
        userRole: String
    ) {
        val cardView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_explore_course_card, container, false)

        cardView.findViewById<TextView>(
            R.id.explore_course_name).text = courseName
        cardView.findViewById<TextView>(
            R.id.explore_instructor_name).text = instructorName
        cardView.findViewById<TextView>(
            R.id.explore_course_desc).text = courseDesc

        // Get enrolled count
        db.collection("courses").document(courseId)
            .collection("students")
            .get()
            .addOnSuccessListener { students ->
                cardView.findViewById<TextView>(
                    R.id.explore_enrolled_count).text =
                    "👥 ${students.size()} enrolled"
            }

        val enrollBtn = cardView.findViewById<MaterialButton>(
            R.id.explore_enroll_btn)

        // ✅ Check login and role first
        when {
            // Not logged in
            userId.isEmpty() -> {
                enrollBtn.text = "Login to Enroll"
                enrollBtn.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#AAAAAA"))
                enrollBtn.setOnClickListener {
                    Toast.makeText(requireContext(),
                        "Please login as a Student to enroll!",
                        Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.loginFragment)
                }
            }

            // Logged in but not a student
            userRole != "student" -> {
                enrollBtn.text = "Students Only"
                enrollBtn.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#AAAAAA"))
                enrollBtn.isEnabled = false
                enrollBtn.setOnClickListener {
                    val message = when (userRole) {
                        "instructor" -> "Instructors cannot enroll in courses!"
                        "admin"      -> "Admins cannot enroll in courses!"
                        else         -> "Only students can enroll in courses!"
                    }
                    Toast.makeText(requireContext(),
                        message, Toast.LENGTH_LONG).show()
                }
                // Keep button clickable to show toast
                enrollBtn.isEnabled = true
            }

            // Logged in as student — check enrollment status
            else -> {
                db.collection("enrollments")
                    .whereEqualTo("studentId", userId)
                    .whereEqualTo("courseId", courseId)
                    .get()
                    .addOnSuccessListener { docs ->
                        if (!docs.isEmpty) {
                            // Already enrolled
                            enrollBtn.text = "Enrolled ✅"
                            enrollBtn.backgroundTintList =
                                android.content.res.ColorStateList.valueOf(
                                    android.graphics.Color.parseColor("#22C55E"))
                            enrollBtn.isEnabled = false
                        } else {
                            // Not enrolled — can enroll
                            enrollBtn.text = "Enroll Now"
                            enrollBtn.backgroundTintList =
                                android.content.res.ColorStateList.valueOf(
                                    android.graphics.Color.parseColor("#2979FF"))
                            enrollBtn.isEnabled = true

                            enrollBtn.setOnClickListener {
                                confirmEnroll(
                                    courseId, courseName,
                                    userId, userName, userEmail,
                                    instructorId, instructorName, instructorEmail,
                                    enrollBtn
                                )
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(),
                            "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        container.addView(cardView)
    }

    // ✅ Filter courses by search
    private fun filterCourses(
        view: View,
        query: String,
        userId: String,
        userName: String,
        userEmail: String,
        userRole: String
    ) {
        val container = view.findViewById<LinearLayout>(
            R.id.explore_courses_container)
        val countView = view.findViewById<TextView>(R.id.courses_count)
        container.removeAllViews()

        if (query.isEmpty()) {
            loadAllCourses(view, userId, userName, userEmail, userRole)
            return
        }

        val filtered = allCoursesList.filter {
            it["courseName"]?.contains(query, ignoreCase = true) == true ||
                    it["instructorName"]?.contains(query, ignoreCase = true) == true ||
                    it["courseDesc"]?.contains(query, ignoreCase = true) == true
        }

        countView.text = "${filtered.size} courses found"

        if (filtered.isEmpty()) {
            showEmpty(container, "No courses found for \"$query\"")
            return
        }

        for (course in filtered) {
            addCourseCard(
                container,
                course["courseId"] ?: "",
                course["courseName"] ?: "",
                course["courseDesc"] ?: "",
                course["instructorId"] ?: "",
                course["instructorName"] ?: "",
                course["instructorEmail"] ?: "",
                userId,
                userName,
                userEmail,
                userRole
            )
        }
    }

    // ✅ Confirm enrollment dialog
    private fun confirmEnroll(
        courseId: String,
        courseName: String,
        studentId: String,
        studentName: String,
        studentEmail: String,
        instructorId: String,
        instructorName: String,
        instructorEmail: String,
        enrollBtn: MaterialButton
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle("Enroll in Course")
            .setMessage("Do you want to enroll in \"$courseName\" by $instructorName?")
            .setPositiveButton("Enroll") { _, _ ->
                enrollInCourse(
                    courseId, courseName,
                    studentId, studentName, studentEmail,
                    instructorId, instructorName, instructorEmail,
                    enrollBtn
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ✅ Enroll in course
    private fun enrollInCourse(
        courseId: String,
        courseName: String,
        studentId: String,
        studentName: String,
        studentEmail: String,
        instructorId: String,
        instructorName: String,
        instructorEmail: String,
        enrollBtn: MaterialButton
    ) {
        enrollBtn.isEnabled = false
        enrollBtn.text = "Enrolling..."

        val enrollmentData = hashMapOf(
            "courseId"        to courseId,
            "courseName"      to courseName,
            "studentId"       to studentId,
            "studentName"     to studentName,
            "studentEmail"    to studentEmail,
            "instructorId"    to instructorId,
            "instructorName"  to instructorName,
            "instructorEmail" to instructorEmail,
            "enrolledAt"      to System.currentTimeMillis()
        )

        db.collection("enrollments")
            .add(enrollmentData)
            .addOnSuccessListener {

                // Add to course students subcollection
                db.collection("courses").document(courseId)
                    .collection("students")
                    .document(studentId)
                    .set(hashMapOf(
                        "studentId"  to studentId,
                        "enrolledAt" to System.currentTimeMillis()
                    ))

                enrollBtn.isEnabled = false
                enrollBtn.text = "Enrolled ✅"
                enrollBtn.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#22C55E"))

                Toast.makeText(requireContext(),
                    "✅ Enrolled in $courseName!",
                    Toast.LENGTH_SHORT).show()

                // Send email to student
                sendEmail(
                    to      = studentEmail,
                    subject = "Enrollment Confirmed - $courseName",
                    message = """
                        Hi $studentName,
                        
                        You have successfully enrolled in: $courseName
                        Instructor: $instructorName
                        
                        You can now access all course files and materials.
                        
                        Best regards,
                        PeerPrep Team
                    """.trimIndent()
                )

                // Send email to instructor
                sendEmail(
                    to      = instructorEmail,
                    subject = "New Enrollment - $courseName",
                    message = """
                        Hi $instructorName,
                        
                        A new student enrolled in your course: $courseName
                        
                        Student Name:  $studentName
                        Student Email: $studentEmail
                        
                        Best regards,
                        PeerPrep Team
                    """.trimIndent()
                )
            }
            .addOnFailureListener {
                enrollBtn.isEnabled = true
                enrollBtn.text = "Enroll Now"
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Send email
    private fun sendEmail(to: String, subject: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$to")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        try {
            startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (e: Exception) {
            // Email app not available
        }
    }

    // ✅ Show empty message
    private fun showEmpty(container: LinearLayout, message: String) {
        val empty = TextView(requireContext())
        empty.text = message
        empty.setTextColor(android.graphics.Color.parseColor("#AAAAAA"))
        empty.setPadding(0, 24, 0, 24)
        container.addView(empty)
    }
}