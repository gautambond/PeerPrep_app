package com.bond.peerprep

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InstructorDashboardFragment : Fragment() {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var selectedFileUri: Uri? = null
    private var dialogSelectedFileUri: Uri? = null
    private val studentNames  = mutableListOf<String>()
    private val studentIds    = mutableListOf<String>()

    // Main file picker
    private val filePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedFileUri = result.data?.data
        }
    }

    // Dialog file picker
    private val dialogFilePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            dialogSelectedFileUri = result.data?.data
            currentDialogFileNameView?.text =
                dialogSelectedFileUri?.lastPathSegment ?: "File selected"
        }
    }

    // Reference to dialog views
    private var currentDialogFileNameView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_instructor_dashboard, container, false)

        val prefs    = requireActivity().getSharedPreferences("PeerPrep",
            android.content.Context.MODE_PRIVATE)
        val userName = prefs.getString("userName", "Instructor")
        val userId   = prefs.getString("userId", "") ?: ""

        view.findViewById<TextView>(R.id.instructor_welcome).text =
            "Welcome $userName 👨‍🏫"

        // Sections
        val coursesSection    = view.findViewById<LinearLayout>(R.id.courses_section)
        val myStudentsSection = view.findViewById<LinearLayout>(R.id.my_students_section)
        val scheduleSection   = view.findViewById<LinearLayout>(R.id.schedule_section)

        // Tabs
        val tabs = view.findViewById<TabLayout>(R.id.instructor_tabs)
        tabs.addTab(tabs.newTab().setText("Courses"))
        tabs.addTab(tabs.newTab().setText("My Students"))
        tabs.addTab(tabs.newTab().setText("Schedule"))

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        coursesSection.visibility    = View.VISIBLE
                        myStudentsSection.visibility = View.GONE
                        scheduleSection.visibility   = View.GONE
                    }
                    1 -> {
                        coursesSection.visibility    = View.GONE
                        myStudentsSection.visibility = View.VISIBLE
                        scheduleSection.visibility   = View.GONE
                        loadMyStudents(view, userId)
                    }
                    2 -> {
                        coursesSection.visibility    = View.GONE
                        myStudentsSection.visibility = View.GONE
                        scheduleSection.visibility   = View.VISIBLE
                        loadStudentsForSpinner(userId)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Load courses
        loadCourses(view, userId)

        // Create course button
        view.findViewById<MaterialButton>(R.id.btn_create_course).setOnClickListener {
            val name = view.findViewById<TextInputEditText>(R.id.course_name)
                .text.toString().trim()
            val desc = view.findViewById<TextInputEditText>(R.id.course_description)
                .text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Enter course name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (desc.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Enter course description!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createCourse(view, userId, name, desc)
        }

        // Post schedule button
        view.findViewById<MaterialButton>(R.id.btn_post_schedule).setOnClickListener {
            val subject  = view.findViewById<TextInputEditText>(R.id.schedule_subject)
                .text.toString().trim()
            val time     = view.findViewById<TextInputEditText>(R.id.schedule_time)
                .text.toString().trim()
            val zoomLink = view.findViewById<TextInputEditText>(R.id.schedule_zoom)
                .text.toString().trim()

            if (subject.isEmpty() || time.isEmpty() || zoomLink.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Fill all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val spinner = view.findViewById<Spinner>(R.id.student_spinner)
            val selectedPosition = spinner.selectedItemPosition
            if (studentIds.isEmpty() || selectedPosition < 0) {
                Toast.makeText(requireContext(),
                    "No students available!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedStudentId = studentIds[selectedPosition]
            postScheduleToStudent(userId, selectedStudentId, subject, time, zoomLink)
        }

        // Logout
        view.findViewById<MaterialButton>(R.id.instructor_logout).setOnClickListener {
            auth.signOut()
            prefs.edit().clear().apply()
            findNavController().navigate(R.id.loginFragment)
        }

        return view
    }

    // ✅ Create Course
    private fun createCourse(
        view: View,
        instructorId: String,
        name: String,
        desc: String
    ) {
        val createBtn = view.findViewById<MaterialButton>(R.id.btn_create_course)
        createBtn.isEnabled = false
        createBtn.text = "Creating..."

        val courseData = hashMapOf(
            "name"         to name,
            "description"  to desc,
            "instructorId" to instructorId,
            "createdAt"    to System.currentTimeMillis()
        )

        db.collection("courses")
            .add(courseData)
            .addOnSuccessListener {
                createBtn.isEnabled = true
                createBtn.text = "Create Course"

                // Clear fields
                view.findViewById<TextInputEditText>(R.id.course_name).text?.clear()
                view.findViewById<TextInputEditText>(R.id.course_description).text?.clear()

                Toast.makeText(requireContext(),
                    "✅ Course created!", Toast.LENGTH_SHORT).show()
                loadCourses(view, instructorId)
            }
            .addOnFailureListener {
                createBtn.isEnabled = true
                createBtn.text = "Create Course"
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Load Courses
    private fun loadCourses(view: View, instructorId: String) {
        val container = view.findViewById<LinearLayout>(R.id.courses_container)

        db.collection("courses")
            .whereEqualTo("instructorId", instructorId)
            .get()
            .addOnSuccessListener { docs ->
                container.removeAllViews()

                if (docs.isEmpty) {
                    val empty = TextView(requireContext())
                    empty.text = "No courses created yet"
                    empty.setTextColor(android.graphics.Color.parseColor("#AAAAAA"))
                    empty.setPadding(0, 16, 0, 16)
                    container.addView(empty)
                    return@addOnSuccessListener
                }

                for (doc in docs) {
                    val courseId   = doc.id
                    val courseName = doc.getString("name") ?: ""
                    val courseDesc = doc.getString("description") ?: ""

                    val cardView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_course_card, container, false)

                    cardView.findViewById<TextView>(R.id.course_item_name).text =
                        courseName
                    cardView.findViewById<TextView>(R.id.course_item_desc).text =
                        courseDesc

                    // Get enrolled students count
                    db.collection("courses").document(courseId)
                        .collection("students")
                        .get()
                        .addOnSuccessListener { students ->
                            cardView.findViewById<TextView>(
                                R.id.course_item_students).text =
                                "👥 ${students.size()} students enrolled"
                        }

                    // Files button
                    cardView.findViewById<MaterialButton>(
                        R.id.btn_course_upload).setOnClickListener {
                        showFilesDialog(courseId, courseName, instructorId)
                    }

                    // Students button
                    cardView.findViewById<MaterialButton>(
                        R.id.btn_course_students).setOnClickListener {
                        showCourseStudents(courseId, courseName)
                    }

                    // Delete button
                    cardView.findViewById<MaterialButton>(
                        R.id.btn_course_delete).setOnClickListener {
                        deleteCourse(view, courseId, instructorId)
                    }

                    container.addView(cardView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Show Files Dialog for a course
    private fun showFilesDialog(
        courseId: String,
        courseName: String,
        instructorId: String
    ) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_course_files, null)

        dialogView.findViewById<TextView>(R.id.dialog_course_name).text =
            "📚 $courseName"

        currentDialogFileNameView =
            dialogView.findViewById(R.id.dialog_selected_file)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Pick file
        dialogView.findViewById<MaterialButton>(R.id.dialog_pick_file)
            .setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                dialogFilePicker.launch(intent)
            }

        // Upload file
        dialogView.findViewById<MaterialButton>(R.id.dialog_upload_btn)
            .setOnClickListener {
                val fileName = dialogView
                    .findViewById<TextInputEditText>(R.id.dialog_file_name)
                    .text.toString().trim()

                if (fileName.isEmpty()) {
                    Toast.makeText(requireContext(),
                        "Enter file name!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (dialogSelectedFileUri == null) {
                    Toast.makeText(requireContext(),
                        "Please select a file!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                uploadFileToCourse(
                    dialogView,
                    dialogSelectedFileUri!!,
                    fileName,
                    courseId,
                    instructorId
                )
            }

        // Load existing files
        loadCourseFiles(dialogView, courseId)

        dialog.show()
    }

    // ✅ Upload file to course
    private fun uploadFileToCourse(
        dialogView: View,
        uri: Uri,
        fileName: String,
        courseId: String,
        instructorId: String
    ) {
        val uploadBtn = dialogView.findViewById<MaterialButton>(R.id.dialog_upload_btn)
        uploadBtn.isEnabled = false
        uploadBtn.text = "Uploading..."

        MediaManager.get().upload(uri)
            .option("public_id", "courses/$courseId/$fileName")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}

                override fun onProgress(
                    requestId: String,
                    bytes: Long,
                    totalBytes: Long
                ) {
                    val progress = (bytes * 100 / totalBytes).toInt()
                    activity?.runOnUiThread {
                        uploadBtn.text = "Uploading $progress%..."
                    }
                }

                override fun onSuccess(
                    requestId: String,
                    resultData: Map<*, *>
                ) {
                    val url = resultData["secure_url"].toString()
                    val type = if (url.contains("image")) "image" else "file"

                    val fileData = hashMapOf(
                        "name"         to fileName,
                        "url"          to url,
                        "type"         to type,
                        "courseId"     to courseId,
                        "instructorId" to instructorId,
                        "uploadedAt"   to System.currentTimeMillis()
                    )

                    // Save to course files subcollection
                    db.collection("courses").document(courseId)
                        .collection("files")
                        .add(fileData)
                        .addOnSuccessListener {
                            // Also save to main files collection
                            db.collection("files").add(fileData)

                            activity?.runOnUiThread {
                                uploadBtn.isEnabled = true
                                uploadBtn.text = "Upload"
                                dialogSelectedFileUri = null
                                currentDialogFileNameView?.text = "No file selected"
                                dialogView.findViewById<TextInputEditText>(
                                    R.id.dialog_file_name).text?.clear()

                                Toast.makeText(requireContext(),
                                    "✅ File uploaded!", Toast.LENGTH_SHORT).show()
                                loadCourseFiles(dialogView, courseId)
                            }
                        }
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    activity?.runOnUiThread {
                        uploadBtn.isEnabled = true
                        uploadBtn.text = "Upload"
                        Toast.makeText(requireContext(),
                            "Upload failed: ${error.description}",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onReschedule(
                    requestId: String,
                    error: ErrorInfo
                ) {}
            })
            .dispatch()
    }

    // ✅ Load course files
    private fun loadCourseFiles(dialogView: View, courseId: String) {
        val container = dialogView.findViewById<LinearLayout>(
            R.id.dialog_files_container)

        db.collection("courses").document(courseId)
            .collection("files")
            .get()
            .addOnSuccessListener { docs ->
                container.removeAllViews()

                if (docs.isEmpty) {
                    val empty = TextView(requireContext())
                    empty.text = "No files uploaded yet"
                    empty.setTextColor(android.graphics.Color.parseColor("#AAAAAA"))
                    container.addView(empty)
                    return@addOnSuccessListener
                }

                for (doc in docs) {
                    val name = doc.getString("name") ?: ""
                    val url  = doc.getString("url") ?: ""
                    val type = doc.getString("type") ?: "file"

                    val icon = if (type == "image") "🖼️" else "📁"

                    val fileView = TextView(requireContext())
                    fileView.text = "$icon $name"
                    fileView.setTextColor(
                        android.graphics.Color.parseColor("#2979FF"))
                    fileView.textSize = 14f
                    fileView.setPadding(0, 8, 0, 8)
                    fileView.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }

                    container.addView(fileView)
                }
            }
    }

    // ✅ Show course students with details
    private fun showCourseStudents(courseId: String, courseName: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_students_list, null)

        val container = dialogView.findViewById<LinearLayout>(
            R.id.dialog_students_container)
        val titleView = dialogView.findViewById<TextView>(
            R.id.dialog_students_title)

        titleView.text = "Students in $courseName"

        db.collection("courses").document(courseId)
            .collection("students")
            .get()
            .addOnSuccessListener { studentDocs ->
                container.removeAllViews()

                if (studentDocs.isEmpty) {
                    val empty = TextView(requireContext())
                    empty.text = "No students enrolled yet"
                    empty.setTextColor(
                        android.graphics.Color.parseColor("#AAAAAA"))
                    container.addView(empty)
                } else {
                    for (studentDoc in studentDocs) {
                        val studentId = studentDoc.getString("studentId") ?: ""

                        // Get full student details
                        db.collection("users").document(studentId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                val name  = userDoc.getString("name") ?: ""
                                val email = userDoc.getString("email") ?: ""
                                val phone = userDoc.getString("phone") ?: ""

                                val cardView = LayoutInflater.from(requireContext())
                                    .inflate(R.layout.item_student_detail_card,
                                        container, false)

                                cardView.findViewById<TextView>(
                                    R.id.student_detail_name).text = name
                                cardView.findViewById<TextView>(
                                    R.id.student_detail_email).text = email
                                cardView.findViewById<TextView>(
                                    R.id.student_detail_phone).text = phone

                                // Call button
                                cardView.findViewById<MaterialButton>(
                                    R.id.btn_call_student).setOnClickListener {
                                    val intent = Intent(Intent.ACTION_DIAL,
                                        Uri.parse("tel:$phone"))
                                    startActivity(intent)
                                }

                                // Email button
                                cardView.findViewById<MaterialButton>(
                                    R.id.btn_email_student).setOnClickListener {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:$email")
                                    }
                                    startActivity(intent)
                                }

                                container.addView(cardView)
                            }
                    }
                }
            }

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    // ✅ Load my students (all students under this instructor)
    private fun loadMyStudents(view: View, instructorId: String) {
        val container = view.findViewById<LinearLayout>(R.id.my_students_container)

        db.collection("users")
            .whereEqualTo("role", "student")
            .whereEqualTo("instructorId", instructorId)
            .get()
            .addOnSuccessListener { docs ->
                container.removeAllViews()

                if (docs.isEmpty) {
                    val empty = TextView(requireContext())
                    empty.text = "No students assigned yet"
                    empty.setTextColor(
                        android.graphics.Color.parseColor("#AAAAAA"))
                    empty.setPadding(0, 16, 0, 16)
                    container.addView(empty)
                    return@addOnSuccessListener
                }

                for (doc in docs) {
                    val name  = doc.getString("name") ?: ""
                    val email = doc.getString("email") ?: ""
                    val phone = doc.getString("phone") ?: ""

                    val cardView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_student_detail_card,
                            container, false)

                    cardView.findViewById<TextView>(
                        R.id.student_detail_name).text = name
                    cardView.findViewById<TextView>(
                        R.id.student_detail_email).text = email
                    cardView.findViewById<TextView>(
                        R.id.student_detail_phone).text = phone

                    // Call button
                    cardView.findViewById<MaterialButton>(
                        R.id.btn_call_student).setOnClickListener {
                        val intent = Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:$phone"))
                        startActivity(intent)
                    }

                    // Email button
                    cardView.findViewById<MaterialButton>(
                        R.id.btn_email_student).setOnClickListener {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$email")
                        }
                        startActivity(intent)
                    }

                    container.addView(cardView)
                }
            }
    }

    // ✅ Load students for spinner in schedule tab
    private fun loadStudentsForSpinner(instructorId: String) {
        studentNames.clear()
        studentIds.clear()

        db.collection("users")
            .whereEqualTo("role", "student")
            .whereEqualTo("instructorId", instructorId)
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    studentNames.add(doc.getString("name") ?: "")
                    studentIds.add(doc.id)
                }

                view?.let { v ->
                    val spinner = v.findViewById<Spinner>(R.id.student_spinner)
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        studentNames
                    )
                    adapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
            }
    }

    // ✅ Post schedule to specific student
    private fun postScheduleToStudent(
        instructorId: String,
        studentId: String,
        subject: String,
        time: String,
        zoomLink: String
    ) {
        val scheduleData = hashMapOf(
            "subject"      to subject,
            "time"         to time,
            "zoomLink"     to zoomLink,
            "instructorId" to instructorId,
            "studentId"    to studentId,
            "createdAt"    to System.currentTimeMillis()
        )

        db.collection("schedules")
            .add(scheduleData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(),
                    "✅ Schedule posted to student!",
                    Toast.LENGTH_SHORT).show()

                // Clear fields
                view?.let { v ->
                    v.findViewById<TextInputEditText>(
                        R.id.schedule_subject).text?.clear()
                    v.findViewById<TextInputEditText>(
                        R.id.schedule_time).text?.clear()
                    v.findViewById<TextInputEditText>(
                        R.id.schedule_zoom).text?.clear()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),
                    "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Delete course
    private fun deleteCourse(view: View, courseId: String, instructorId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete this course?")
            .setPositiveButton("Delete") { _, _ ->
                db.collection("courses").document(courseId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(),
                            "Course deleted!", Toast.LENGTH_SHORT).show()
                        loadCourses(view, instructorId)
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}