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

class SignupFragment : Fragment() {

    private var selectedRole = "student"
    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        // Role views
        val roleStudent    = view.findViewById<LinearLayout>(R.id.role_student)
        val roleInstructor = view.findViewById<LinearLayout>(R.id.role_instructor)
        val roleAdmin      = view.findViewById<LinearLayout>(R.id.role_admin)

        // ✅ Role selection
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

        val name            = view.findViewById<TextInputEditText>(R.id.signup_name)
        val email           = view.findViewById<TextInputEditText>(R.id.signup_email)
        val phone           = view.findViewById<TextInputEditText>(R.id.signup_phone)
        val password        = view.findViewById<TextInputEditText>(R.id.signup_password)
        val confirmPassword = view.findViewById<TextInputEditText>(R.id.signup_confirm_password)
        val signupBtn       = view.findViewById<MaterialButton>(R.id.signup_button)

        // ✅ Signup button
        signupBtn.setOnClickListener {
            val nameText  = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val passText  = password.text.toString().trim()
            val confText  = confirmPassword.text.toString().trim()

            // Validation
            if (nameText.isEmpty()) {
                name.error = "Please enter your name"
                return@setOnClickListener
            }
            if (emailText.isEmpty()) {
                email.error = "Please enter your email"
                return@setOnClickListener
            }
            if (phoneText.isEmpty()) {
                phone.error = "Please enter your phone number"
                return@setOnClickListener
            }
            if (passText.isEmpty()) {
                password.error = "Please enter a password"
                return@setOnClickListener
            }
            if (passText != confText) {
                confirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }
            if (passText.length < 6) {
                password.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            signupBtn.isEnabled = false
            signupBtn.text = "Creating account..."

            // ✅ Create user in Firebase Auth
            auth.createUserWithEmailAndPassword(emailText, passText)
                .addOnSuccessListener { result ->
                    val userId = result.user?.uid ?: return@addOnSuccessListener

                    // Save user data to Firestore
                    val userData = hashMapOf(
                        "userId"    to userId,
                        "name"      to nameText,
                        "email"     to emailText,
                        "phone"     to phoneText,
                        "role"      to selectedRole,
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("users").document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            signupBtn.isEnabled = true
                            signupBtn.text = "Create Account"
                            Toast.makeText(requireContext(),
                                "Account created! Please login.",
                                Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.loginFragment)
                        }
                        .addOnFailureListener {
                            signupBtn.isEnabled = true
                            signupBtn.text = "Create Account"
                            Toast.makeText(requireContext(),
                                "Error: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener {
                    signupBtn.isEnabled = true
                    signupBtn.text = "Create Account"
                    Toast.makeText(requireContext(),
                        "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        // Go to login
        view.findViewById<TextView>(R.id.go_to_login).setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        return view
    }

    // ✅ Update role selection UI
    private fun updateRoleSelection(view: View, selectedRole: String) {
        val roleStudent    = view.findViewById<LinearLayout>(R.id.role_student)
        val roleInstructor = view.findViewById<LinearLayout>(R.id.role_instructor)
        val roleAdmin      = view.findViewById<LinearLayout>(R.id.role_admin)

        roleStudent.setBackgroundResource(R.drawable.role_unselected)
        roleInstructor.setBackgroundResource(R.drawable.role_unselected)
        roleAdmin.setBackgroundResource(R.drawable.role_unselected)

        roleStudent.getChildAt(1).let { (it as TextView).setTextColor(
            android.graphics.Color.parseColor("#AAAAAA")) }
        roleInstructor.getChildAt(1).let { (it as TextView).setTextColor(
            android.graphics.Color.parseColor("#AAAAAA")) }
        roleAdmin.getChildAt(1).let { (it as TextView).setTextColor(
            android.graphics.Color.parseColor("#AAAAAA")) }

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