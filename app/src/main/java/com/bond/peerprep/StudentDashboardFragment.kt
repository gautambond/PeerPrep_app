package com.bond.peerprep

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentDashboardFragment : Fragment() {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_student_dashboard, container, false)

        val prefs    = requireActivity().getSharedPreferences("PeerPrep",
            android.content.Context.MODE_PRIVATE)
        val userName = prefs.getString("userName", "Student") ?: ""
        val userId   = prefs.getString("userId", "") ?: ""
        val userEmail = prefs.getString("userEmail", "") ?: ""

        view.findViewById<TextView>(R.id.student_welcome).text =
            "Welcome $userName 🎓"

        // Sections
        val allCoursesSection      = view.findViewById<LinearLayout>(
            R.id.all_courses_section)
        val enrolledCoursesSection = view.findViewById<LinearLayout>(
            R.id.enrolled_courses_section)
        val scheduleSection        = view.findViewById<LinearLayout>(
            R.id.schedule_section)

        // Tabs
        val tabs = view.findViewById<TabLayout>(R.id.student_tabs)
        tabs.addTab(tabs.newTab().setText("All Courses"))
        tabs.addTab(tabs.newTab().setText("My Courses"))
        tabs.addTab(tabs.newTab().setText("Schedule"))

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        allCoursesSection.visibility      = View.VISIBLE
                        enrolledCoursesSection.visibility = View.GONE
                        scheduleSection.visibility        = View.GONE
                        loadAllCourses(view, userId, userName, userEmail)
                    }
                    1 -> {
                        allCoursesSection.visibility      = View.GONE
                        enrolledCoursesSection.visibility = View.VISIBLE
                        scheduleSection.visibility        = View.GONE
                        loadEnrolledCourses(view, userId, userName, userEmail)
                    }
                    2 -> {
                        allCoursesSection.visibility      = View.GONE
                        enrolledCoursesSection.visibility = View.GONE
                        scheduleSection.visibility        = View.VISIBLE
                        loadSchedule(view, userId)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Load all courses by default
        loadAllCourses(view, userId, userName, userEmail)

        // Logout
        view.findViewById<MaterialButton>(R.id.student_logout).setOnClickListener {
            auth.signOut()
            prefs.edit().clear().apply()
            findNavController().navigate(R.id.loginFragment)
        }

        return view
    }

    // ✅ Load ALL courses with instructor name
    private fun loadAllCourses(
        view: View,
        userId: String,
        userName: String,
        userEmail: String
    ) {
        val container = view.findViewById<LinearLayout>(R.id.all_courses_container)
        container.removeAllViews()

        db.collection("courses")
            .get()
            .addOnSuccessListener { courseDocs ->
                if (courseDocs.isEmpty) {
                    showEmpty(container, "No courses available yet")
                    return@addOnSuccessListener
                }

                for (courseDoc in courseDocs) {
                    val courseId     = courseDoc.id
                    val courseName   = courseDoc.getString("name") ?: ""
                    val courseDesc   = courseDoc.getString("description") ?: ""
                    val instructorId = courseDoc.getString("instructorId") ?: ""

                    // Get instructor name
                    db.collection("users").document(instructorId)
                        .get()
                        .addOnSuccessListener { instructorDoc ->
                            val instructorName  = instructorDoc.getString("name") ?: ""
                            val instructorEmail = instructorDoc.getString("email") ?: ""

                            val cardView = LayoutInflater.from(requireContext())
                                .inflate(R.layout.item_course_student_card,
                                    container, false)

                            cardView.findViewById<TextView>(
                                R.id.student_course_name).text = courseName
                            cardView.findViewById<TextView>(
                                R.id.student_course_instructor).text = instructorName
                            cardView.findViewById<TextView>(
                                R.id.student_course_desc).text = courseDesc

                            // Check if already enrolled
                            checkEnrollmentStatus(
                                courseId,
                                userId,
                                cardView,
                                courseId,
                                courseName,
                                userId,
                                userName,
                                userEmail,
                                instructorId,
                                instructorName,
                                instructorEmail,
                                false
                            )

                            container.addView(cardView)
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Load only enrolled courses
    private fun loadEnrolledCourses(
        view: View,
        userId: String,
        userName: String,
        userEmail: String
    ) {
        val container = view.findViewById<LinearLayout>(
            R.id.enrolled_courses_container)
        container.removeAllViews()

        db.collection("enrollments")
            .whereEqualTo("studentId", userId)
            .get()
            .addOnSuccessListener { enrollDocs ->
                if (enrollDocs.isEmpty) {
                    showEmpty(container, "You haven't enrolled in any course yet")
                    return@addOnSuccessListener
                }

                for (enrollDoc in enrollDocs) {
                    val courseId = enrollDoc.getString("courseId") ?: ""

                    db.collection("courses").document(courseId)
                        .get()
                        .addOnSuccessListener { courseDoc ->
                            val courseName   = courseDoc.getString("name") ?: ""
                            val courseDesc   = courseDoc.getString("description") ?: ""
                            val instructorId = courseDoc.getString("instructorId") ?: ""

                            db.collection("users").document(instructorId)
                                .get()
                                .addOnSuccessListener { instructorDoc ->
                                    val instructorName  =
                                        instructorDoc.getString("name") ?: ""
                                    val instructorEmail =
                                        instructorDoc.getString("email") ?: ""

                                    val cardView = LayoutInflater.from(requireContext())
                                        .inflate(R.layout.item_course_student_card,
                                            container, false)

                                    cardView.findViewById<TextView>(
                                        R.id.student_course_name).text = courseName
                                    cardView.findViewById<TextView>(
                                        R.id.student_course_instructor).text =
                                        instructorName
                                    cardView.findViewById<TextView>(
                                        R.id.student_course_desc).text = courseDesc

                                    // Already enrolled — show unenroll + view files
                                    val enrollBtn = cardView.findViewById<MaterialButton>(
                                        R.id.btn_enroll)
                                    val viewFilesBtn = cardView.findViewById<MaterialButton>(
                                        R.id.btn_view_files)
                                    val statusText = cardView.findViewById<TextView>(
                                        R.id.enrollment_status)

                                    enrollBtn.text = "Unenroll"
                                    enrollBtn.backgroundTintList =
                                        android.content.res.ColorStateList.valueOf(
                                            android.graphics.Color.parseColor("#DC0917"))
                                    statusText.text = "✅ Enrolled"
                                    statusText.setTextColor(
                                        android.graphics.Color.parseColor("#22C55E"))
                                    viewFilesBtn.visibility = View.VISIBLE

                                    // Unenroll click
                                    enrollBtn.setOnClickListener {
                                        confirmUnenroll(
                                            courseId,
                                            courseName,
                                            userId,
                                            userName,
                                            userEmail,
                                            instructorId,
                                            instructorName,
                                            instructorEmail,
                                            container,
                                            view
                                        )
                                    }

                                    // View files click
                                    viewFilesBtn.setOnClickListener {
                                        showCourseFiles(courseId, courseName)
                                    }

                                    container.addView(cardView)
                                }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Check if student is enrolled in a course
    private fun checkEnrollmentStatus(
        courseId: String,
        userId: String,
        cardView: View,
        cId: String,
        cName: String,
        sId: String,
        sName: String,
        sEmail: String,
        instructorId: String,
        instructorName: String,
        instructorEmail: String,
        isEnrolledSection: Boolean
    ) {
        val enrollBtn    = cardView.findViewById<MaterialButton>(R.id.btn_enroll)
        val viewFilesBtn = cardView.findViewById<MaterialButton>(R.id.btn_view_files)
        val statusText   = cardView.findViewById<TextView>(R.id.enrollment_status)

        db.collection("enrollments")
            .whereEqualTo("studentId", userId)
            .whereEqualTo("courseId", courseId)
            .get()
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    // Already enrolled
                    enrollBtn.text = "Unenroll"
                    enrollBtn.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#DC0917"))
                    statusText.text = "✅ Enrolled"
                    statusText.setTextColor(
                        android.graphics.Color.parseColor("#22C55E"))
                    viewFilesBtn.visibility = View.VISIBLE

                    // Unenroll click
                    enrollBtn.setOnClickListener {
                        confirmUnenroll(
                            cId, cName, sId, sName, sEmail,
                            instructorId, instructorName, instructorEmail,
                            null, null
                        )
                    }

                    // View files click
                    viewFilesBtn.setOnClickListener {
                        showCourseFiles(cId, cName)
                    }

                } else {
                    // Not enrolled
                    enrollBtn.text = "Enroll"
                    enrollBtn.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#22C55E"))
                    statusText.text = "Not enrolled"
                    statusText.setTextColor(
                        android.graphics.Color.parseColor("#AAAAAA"))
                    viewFilesBtn.visibility = View.GONE

                    // Enroll click
                    enrollBtn.setOnClickListener {
                        confirmEnroll(
                            cId, cName, sId, sName, sEmail,
                            instructorId, instructorName, instructorEmail,
                            enrollBtn, viewFilesBtn, statusText
                        )
                    }
                }
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
        enrollBtn: MaterialButton,
        viewFilesBtn: MaterialButton,
        statusText: TextView
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle("Enroll in Course")
            .setMessage("Do you want to enroll in \"$courseName\"?")
            .setPositiveButton("Enroll") { _, _ ->
                enrollInCourse(
                    courseId, courseName,
                    studentId, studentName, studentEmail,
                    instructorId, instructorName, instructorEmail,
                    enrollBtn, viewFilesBtn, statusText
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
        enrollBtn: MaterialButton,
        viewFilesBtn: MaterialButton,
        statusText: TextView
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

        // Save to enrollments collection
        db.collection("enrollments")
            .add(enrollmentData)
            .addOnSuccessListener {

                // Also add student to course subcollection
                db.collection("courses").document(courseId)
                    .collection("students")
                    .document(studentId)
                    .set(hashMapOf(
                        "studentId"  to studentId,
                        "enrolledAt" to System.currentTimeMillis()
                    ))

                enrollBtn.isEnabled = true
                enrollBtn.text = "Unenroll"
                enrollBtn.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#DC0917"))
                statusText.text = "✅ Enrolled"
                statusText.setTextColor(
                    android.graphics.Color.parseColor("#22C55E"))
                viewFilesBtn.visibility = View.VISIBLE

                Toast.makeText(requireContext(),
                    "✅ Enrolled in $courseName!", Toast.LENGTH_SHORT).show()

                // ✅ Send email to student
                sendEnrollmentEmail(
                    to          = studentEmail,
                    subject     = "Enrollment Confirmed - $courseName",
                    message     = """
                        Hi $studentName,
                        
                        You have successfully enrolled in the course: $courseName
                        Instructor: $instructorName
                        
                        You can now access all course files and materials.
                        
                        Best regards,
                        PeerPrep Team
                    """.trimIndent()
                )

                // ✅ Send email to instructor
                sendEnrollmentEmail(
                    to          = instructorEmail,
                    subject     = "New Enrollment - $courseName",
                    message     = """
                        Hi $instructorName,
                        
                        A new student has enrolled in your course: $courseName
                        
                        Student Name: $studentName
                        Student Email: $studentEmail
                        
                        Best regards,
                        PeerPrep Team
                    """.trimIndent()
                )
            }
            .addOnFailureListener {
                enrollBtn.isEnabled = true
                enrollBtn.text = "Enroll"
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Confirm unenroll dialog
    private fun confirmUnenroll(
        courseId: String,
        courseName: String,
        studentId: String,
        studentName: String,
        studentEmail: String,
        instructorId: String,
        instructorName: String,
        instructorEmail: String,
        container: LinearLayout?,
        view: View?
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle("Unenroll from Course")
            .setMessage("Are you sure you want to unenroll from \"$courseName\"?")
            .setPositiveButton("Unenroll") { _, _ ->
                unenrollFromCourse(
                    courseId, courseName,
                    studentId, studentName, studentEmail,
                    instructorId, instructorName, instructorEmail,
                    container, view
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ✅ Unenroll from course
    private fun unenrollFromCourse(
        courseId: String,
        courseName: String,
        studentId: String,
        studentName: String,
        studentEmail: String,
        instructorId: String,
        instructorName: String,
        instructorEmail: String,
        container: LinearLayout?,
        view: View?
    ) {
        // Find and delete enrollment document
        db.collection("enrollments")
            .whereEqualTo("studentId", studentId)
            .whereEqualTo("courseId", courseId)
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    doc.reference.delete()
                }

                // Remove from course students subcollection
                db.collection("courses").document(courseId)
                    .collection("students")
                    .document(studentId)
                    .delete()

                Toast.makeText(requireContext(),
                    "✅ Unenrolled from $courseName",
                    Toast.LENGTH_SHORT).show()

                // Refresh enrolled courses list
                container?.removeAllViews()
                view?.let {
                    loadEnrolledCourses(
                        it, studentId, studentName, studentEmail)
                }

                // ✅ Send email to student
                sendEnrollmentEmail(
                    to      = studentEmail,
                    subject = "Unenrollment Confirmed - $courseName",
                    message = """
                        Hi $studentName,
                        
                        You have successfully unenrolled from the course: $courseName
                        Instructor: $instructorName
                        
                        You will no longer have access to course files.
                        
                        Best regards,
                        PeerPrep Team
                    """.trimIndent()
                )

                // ✅ Send email to instructor
                sendEnrollmentEmail(
                    to      = instructorEmail,
                    subject = "Student Unenrolled - $courseName",
                    message = """
                        Hi $instructorName,
                        
                        A student has unenrolled from your course: $courseName
                        
                        Student Name: $studentName
                        Student Email: $studentEmail
                        
                        Best regards,
                        PeerPrep Team
                    """.trimIndent()
                )
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Send enrollment email using Intent
    private fun sendEnrollmentEmail(
        to: String,
        subject: String,
        message: String
    ) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$to")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        try {
            startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (e: Exception) {
            // Email app not available — silently fail
        }
    }

    // ✅ Show course files (only for enrolled students)
    private fun showCourseFiles(courseId: String, courseName: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_course_files_student, null)

        dialogView.findViewById<TextView>(
            R.id.student_dialog_course_name).text = "📚 $courseName"

        val container = dialogView.findViewById<LinearLayout>(
            R.id.student_files_container)

        db.collection("courses").document(courseId)
            .collection("files")
            .get()
            .addOnSuccessListener { docs ->
                container.removeAllViews()

                if (docs.isEmpty) {
                    showEmpty(container, "No files uploaded yet")
                    return@addOnSuccessListener
                }

                for (doc in docs) {
                    val name = doc.getString("name") ?: ""
                    val url  = doc.getString("url") ?: ""
                    val type = doc.getString("type") ?: "file"
                    val icon = if (type == "image") "🖼️" else "📁"

                    val fileCard = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_user_card, container, false)

                    fileCard.findViewById<TextView>(R.id.user_name).text =
                        "$icon $name"
                    fileCard.findViewById<TextView>(R.id.user_email).text =
                        "Tap to open"
                    fileCard.findViewById<TextView>(R.id.user_role).text =
                        if (type == "image") "Image" else "File"
                    fileCard.findViewById<TextView>(R.id.user_extra).text = ""

                    fileCard.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }

                    container.addView(fileCard)
                }
            }

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    // ✅ Load weekly schedule
    private fun loadSchedule(view: View, userId: String) {
        val container = view.findViewById<LinearLayout>(R.id.schedule_container)
        container.removeAllViews()

        db.collection("schedules")
            .whereEqualTo("studentId", userId)
            .get()
            .addOnSuccessListener { docs ->
                if (docs.isEmpty) {
                    showEmpty(container, "No schedule posted yet")
                    return@addOnSuccessListener
                }

                for (scheduleDoc in docs) {
                    val subject  = scheduleDoc.getString("subject") ?: ""
                    val time     = scheduleDoc.getString("time") ?: ""
                    val zoomLink = scheduleDoc.getString("zoomLink") ?: ""

                    val cardView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_schedule_card, container, false)

                    cardView.findViewById<TextView>(
                        R.id.schedule_subject).text = subject
                    cardView.findViewById<TextView>(
                        R.id.schedule_time).text = "🕐 $time"
                    cardView.findViewById<TextView>(
                        R.id.schedule_zoom).text = zoomLink

                    // Open zoom link
                    cardView.findViewById<TextView>(
                        R.id.schedule_zoom).setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(zoomLink))
                        startActivity(intent)
                    }

                    container.addView(cardView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Helper to show empty message
    private fun showEmpty(container: LinearLayout, message: String) {
        val empty = TextView(requireContext())
        empty.text = message
        empty.setTextColor(android.graphics.Color.parseColor("#AAAAAA"))
        empty.setPadding(0, 16, 0, 16)
        container.addView(empty)
    }
}