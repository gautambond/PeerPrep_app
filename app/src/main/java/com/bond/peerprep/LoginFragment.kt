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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private var selectedRole = "student"
    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Role views
        val roleStudent    = view.findViewById<LinearLayout>(R.id.role_student)
        val roleInstructor = view.findViewById<LinearLayout>(R.id.role_instructor)
        val roleAdmin      = view.findViewById<LinearLayout>(R.id.role_admin)

        // ✅ Role selection click listeners
        roleStudent.setOnClickListener {
            selectedRole = "student"
            updateRoleSelection(view, "student")
        }
        roleInstructor.setOnClickListener {
            selectedRole = "instructor"
            updateRoleSelection(view, "instructor")
        }
        roleAdmin.setOnClickListener {
            selectedRole = "admin"
            updateRoleSelection(view, "admin")
        }

        val email     = view.findViewById<TextInputEditText>(R.id.login_email)
        val password  = view.findViewById<TextInputEditText>(R.id.login_password)
        val loginBtn  = view.findViewById<MaterialButton>(R.id.login_button)

        // ✅ Login button
        loginBtn.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passText  = password.text.toString().trim()

            if (emailText.isEmpty()) {
                email.error = "Please enter your email"
                return@setOnClickListener
            }
            if (passText.isEmpty()) {
                password.error = "Please enter your password"
                return@setOnClickListener
            }

            loginBtn.isEnabled = false
            loginBtn.text = "Logging in..."

            auth.signInWithEmailAndPassword(emailText, passText)
                .addOnSuccessListener { result ->
                    val userId = result.user?.uid ?: return@addOnSuccessListener

                    // Check user role in Firestore
                    db.collection("users").document(userId)
                        .get()
                        .addOnSuccessListener { doc ->
                            if (!doc.exists()) {
                                // ✅ Not registered toast
                                auth.signOut()
                                loginBtn.isEnabled = true
                                loginBtn.text = "Login"
                                Toast.makeText(requireContext(),
                                    "Not registered! Please register first.",
                                    Toast.LENGTH_LONG).show()
                                return@addOnSuccessListener
                            }

                            val role = doc.getString("role") ?: ""
                            val name = doc.getString("name") ?: ""

                            // ✅ Check if role matches selected role
                            if (role != selectedRole) {
                                auth.signOut()
                                loginBtn.isEnabled = true
                                loginBtn.text = "Login"
                                Toast.makeText(requireContext(),
                                    "Please select correct role! You are a $role",
                                    Toast.LENGTH_LONG).show()
                                return@addOnSuccessListener
                            }

                            // Save to SharedPreferences
                            val prefs = requireActivity()
                                .getSharedPreferences("PeerPrep",
                                    android.content.Context.MODE_PRIVATE)
                            prefs.edit()
                                .putString("userId", userId)
                                .putString("userName", name)
                                .putString("userRole", role)
                                .putString("userEmail", emailText)
                                .apply()

                            loginBtn.isEnabled = true
                            loginBtn.text = "Login"

                            Toast.makeText(requireContext(),
                                "Welcome $name!", Toast.LENGTH_SHORT).show()

                            // ✅ Navigate based on role
                            when (role) {
                                "admin"      -> findNavController()
                                    .navigate(R.id.adminDashboardFragment)
                                "instructor" -> findNavController()
                                    .navigate(R.id.instructorDashboardFragment)
                                "student"    -> findNavController()
                                    .navigate(R.id.studentDashboardFragment)
                            }
                        }
                        .addOnFailureListener {
                            loginBtn.isEnabled = true
                            loginBtn.text = "Login"
                            Toast.makeText(requireContext(),
                                "Error: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener {
                    loginBtn.isEnabled = true
                    loginBtn.text = "Login"
                    Toast.makeText(requireContext(),
                        "Not registered! Please register first.",
                        Toast.LENGTH_LONG).show()
                }
        }

        // Go to signup
        view.findViewById<TextView>(R.id.go_to_signup).setOnClickListener {
            findNavController().navigate(R.id.signupFragment)
        }

        // Forgot password
        view.findViewById<TextView>(R.id.forgot_password).setOnClickListener {
            val emailText = email.text.toString().trim()
            if (emailText.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Enter your email first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(emailText)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(),
                        "Reset link sent to $emailText", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),
                        "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    // ✅ Update role selection UI
    private fun updateRoleSelection(view: View, selectedRole: String) {
        val roleStudent    = view.findViewById<LinearLayout>(R.id.role_student)
        val roleInstructor = view.findViewById<LinearLayout>(R.id.role_instructor)
        val roleAdmin      = view.findViewById<LinearLayout>(R.id.role_admin)

        // Reset all to unselected
        roleStudent.setBackgroundResource(R.drawable.role_unselected)
        roleInstructor.setBackgroundResource(R.drawable.role_unselected)
        roleAdmin.setBackgroundResource(R.drawable.role_unselected)

        // Update text colors
        roleStudent.getChildAt(1).let { (it as TextView).setTextColor(
            android.graphics.Color.parseColor("#AAAAAA")) }
        roleInstructor.getChildAt(1).let { (it as TextView).setTextColor(
            android.graphics.Color.parseColor("#AAAAAA")) }
        roleAdmin.getChildAt(1).let { (it as TextView).setTextColor(
            android.graphics.Color.parseColor("#AAAAAA")) }

        // Set selected
        when (selectedRole) {
            "student" -> {
                roleStudent.setBackgroundResource(R.drawable.role_selected)
                roleStudent.getChildAt(1).let { (it as TextView).setTextColor(
                    android.graphics.Color.parseColor("#FFFFFF")) }
            }
            "instructor" -> {
                roleInstructor.setBackgroundResource(R.drawable.role_selected)
                roleInstructor.getChildAt(1).let { (it as TextView).setTextColor(
                    android.graphics.Color.parseColor("#FFFFFF")) }
            }
            "admin" -> {
                roleAdmin.setBackgroundResource(R.drawable.role_selected)
                roleAdmin.getChildAt(1).let { (it as TextView).setTextColor(
                    android.graphics.Color.parseColor("#FFFFFF")) }
            }
        }
    }
}