package com.bond.peerprep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        val name            = view.findViewById<TextInputEditText>(R.id.signup_name)
        val email           = view.findViewById<TextInputEditText>(R.id.signup_email)
        val phone           = view.findViewById<TextInputEditText>(R.id.signup_phone)
        val password        = view.findViewById<TextInputEditText>(R.id.signup_password)
        val confirmPassword = view.findViewById<TextInputEditText>(R.id.signup_confirm_password)
        val signupBtn       = view.findViewById<MaterialButton>(R.id.signup_button)

        // Signup button click
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

            // TODO: Add your signup logic here
            Toast.makeText(requireContext(), "Account created!", Toast.LENGTH_SHORT).show()
        }

        // Go to login
        view.findViewById<TextView>(R.id.go_to_login).setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        return view
    }
}