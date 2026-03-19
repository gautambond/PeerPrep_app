package com.bond.peerprep

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class BookDemoFragment : Fragment() {

    // ✅ Replace with your WhatsApp number
    private val whatsappNumber = "919546352013"

    // ✅ Replace with your actual Google Form link
    private val googleFormUrl = "https://docs.google.com/forms/d/e/1FAIpQLSerJ2_KaI5Zl70Z2uJdEw81nsP91Af1Dwt4gqi6TUgCL4D6bw/viewform?usp=header"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_book_demo, container, false)

        setupDropdowns(view)
        setupButtons(view)

        return view
    }

    // ✅ Setup all dropdowns
    private fun setupDropdowns(view: View) {

        // Subjects dropdown
        val subjects = listOf(
            "Mathematics",
            "Science",
            "Physics",
            "Chemistry",
            "Biology",
            "English",
            "Computer Science",
            "Coding / Python",
            "Economics",
            "History",
            "Geography",
            "Other"
        )
        val subjectAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            subjects
        )
        view.findViewById<AutoCompleteTextView>(R.id.demo_subject)
            .setAdapter(subjectAdapter)

        // Grades dropdown
        val grades = listOf(
            "KG", "Grade 1", "Grade 2", "Grade 3",
            "Grade 4", "Grade 5", "Grade 6", "Grade 7",
            "Grade 8", "Grade 9", "Grade 10",
            "Grade 11", "Grade 12"
        )
        val gradeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            grades
        )
        view.findViewById<AutoCompleteTextView>(R.id.demo_grade)
            .setAdapter(gradeAdapter)

        // Country codes dropdown
        val countryCodes = listOf(
            "+91 🇮🇳 India",
            "+1 🇺🇸 USA",
            "+1 🇨🇦 Canada",
            "+44 🇬🇧 UK",
            "+61 🇦🇺 Australia",
            "+971 🇦🇪 UAE",
            "+966 🇸🇦 Saudi Arabia",
            "+974 🇶🇦 Qatar",
            "+965 🇰🇼 Kuwait",
            "+968 🇴🇲 Oman",
            "+973 🇧🇭 Bahrain",
            "+60 🇲🇾 Malaysia",
            "+65 🇸🇬 Singapore",
            "+64 🇳🇿 New Zealand",
            "+49 🇩🇪 Germany",
            "+33 🇫🇷 France",
            "+92 🇵🇰 Pakistan",
            "+880 🇧🇩 Bangladesh",
            "+94 🇱🇰 Sri Lanka",
            "+977 🇳🇵 Nepal"
        )
        val countryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            countryCodes
        )
        val countryCodeView = view.findViewById<AutoCompleteTextView>(
            R.id.demo_country_code)
        countryCodeView.setAdapter(countryAdapter)
        // Set India as default
        countryCodeView.setText("+91 🇮🇳 India", false)
    }

    // ✅ Setup buttons
    private fun setupButtons(view: View) {

        // WhatsApp button
        view.findViewById<MaterialButton>(R.id.btn_whatsapp).setOnClickListener {
            if (validateForm(view)) {
                sendViaWhatsApp(view)
            }
        }

        // Google Form button
        view.findViewById<MaterialButton>(R.id.btn_google_form).setOnClickListener {
            if (validateForm(view)) {
                openGoogleForm(view)
            }
        }
    }

    // ✅ Validate form
    private fun validateForm(view: View): Boolean {
        val studentName = view.findViewById<TextInputEditText>(
            R.id.demo_student_name).text.toString().trim()
        val fatherName  = view.findViewById<TextInputEditText>(
            R.id.demo_father_name).text.toString().trim()
        val subject     = view.findViewById<AutoCompleteTextView>(
            R.id.demo_subject).text.toString().trim()
        val grade       = view.findViewById<AutoCompleteTextView>(
            R.id.demo_grade).text.toString().trim()
        val phone       = view.findViewById<TextInputEditText>(
            R.id.demo_phone).text.toString().trim()
        val location    = view.findViewById<TextInputEditText>(
            R.id.demo_location).text.toString().trim()

        when {
            studentName.isEmpty() -> {
                Toast.makeText(requireContext(),
                    "Enter student name!", Toast.LENGTH_SHORT).show()
                return false
            }
            fatherName.isEmpty() -> {
                Toast.makeText(requireContext(),
                    "Enter father's name!", Toast.LENGTH_SHORT).show()
                return false
            }
            subject.isEmpty() -> {
                Toast.makeText(requireContext(),
                    "Select a subject!", Toast.LENGTH_SHORT).show()
                return false
            }
            grade.isEmpty() -> {
                Toast.makeText(requireContext(),
                    "Select a grade!", Toast.LENGTH_SHORT).show()
                return false
            }
            phone.isEmpty() -> {
                Toast.makeText(requireContext(),
                    "Enter phone number!", Toast.LENGTH_SHORT).show()
                return false
            }
            phone.length < 7 -> {
                Toast.makeText(requireContext(),
                    "Enter a valid phone number!", Toast.LENGTH_SHORT).show()
                return false
            }
            location.isEmpty() -> {
                Toast.makeText(requireContext(),
                    "Enter your location!", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    // ✅ Get form data as formatted string
    private fun getFormData(view: View): String {
        val studentName  = view.findViewById<TextInputEditText>(
            R.id.demo_student_name).text.toString().trim()
        val fatherName   = view.findViewById<TextInputEditText>(
            R.id.demo_father_name).text.toString().trim()
        val subject      = view.findViewById<AutoCompleteTextView>(
            R.id.demo_subject).text.toString().trim()
        val grade        = view.findViewById<AutoCompleteTextView>(
            R.id.demo_grade).text.toString().trim()
        val countryCode  = view.findViewById<AutoCompleteTextView>(
            R.id.demo_country_code).text.toString().trim()
            .split(" ").firstOrNull() ?: "+91"
        val phone        = view.findViewById<TextInputEditText>(
            R.id.demo_phone).text.toString().trim()
        val location     = view.findViewById<TextInputEditText>(
            R.id.demo_location).text.toString().trim()

        return """
            📚 *DEMO CLASS BOOKING - PeerPrep*
            
            👤 *Student Name:* $studentName
            👨 *Father's Name:* $fatherName
            📖 *Subject:* $subject
            🎓 *Grade:* $grade
            📞 *Phone:* $countryCode $phone
            📍 *Location:* $location
            
            _Sent from PeerPrep App_
        """.trimIndent()
    }

    // ✅ Send via WhatsApp
    private fun sendViaWhatsApp(view: View) {
        val message = getFormData(view)

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://api.whatsapp.com/send?phone=$whatsappNumber" +
                            "&text=${Uri.encode(message)}"
                )
            }
            startActivity(intent)
        } catch (e: Exception) {
            // WhatsApp not installed — try browser
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                        "https://wa.me/$whatsappNumber" +
                                "?text=${Uri.encode(message)}"
                    )
                }
                startActivity(intent)
            } catch (ex: Exception) {
                Toast.makeText(requireContext(),
                    "WhatsApp not available!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ✅ Open Google Form with pre-filled data
    private fun openGoogleForm(view: View) {
        val studentName = view.findViewById<TextInputEditText>(
            R.id.demo_student_name).text.toString().trim()
        val fatherName  = view.findViewById<TextInputEditText>(
            R.id.demo_father_name).text.toString().trim()
        val subject     = view.findViewById<AutoCompleteTextView>(
            R.id.demo_subject).text.toString().trim()
        val grade       = view.findViewById<AutoCompleteTextView>(
            R.id.demo_grade).text.toString().trim()
        val countryCode = view.findViewById<AutoCompleteTextView>(
            R.id.demo_country_code).text.toString().trim()
            .split(" ").firstOrNull() ?: "+91"
        val phone       = view.findViewById<TextInputEditText>(
            R.id.demo_phone).text.toString().trim()
        val location    = view.findViewById<TextInputEditText>(
            R.id.demo_location).text.toString().trim()

        // ✅ Pre-fill Google Form fields
        // Replace entry.XXXXXXXXX with your actual Google Form entry IDs
        val formUrl = "$googleFormUrl" +
                "?entry.1000000=${Uri.encode(studentName)}" +
                "&entry.1000001=${Uri.encode(fatherName)}" +
                "&entry.1000002=${Uri.encode(subject)}" +
                "&entry.1000003=${Uri.encode(grade)}" +
                "&entry.1000004=${Uri.encode("$countryCode $phone")}" +
                "&entry.1000005=${Uri.encode(location)}"

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formUrl))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(),
                "Cannot open form!", Toast.LENGTH_SHORT).show()
        }
    }
}