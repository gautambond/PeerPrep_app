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

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val email    = view.findViewById<TextInputEditText>(R.id.login_email)
        val password = view.findViewById<TextInputEditText>(R.id.login_password)
        val loginBtn = view.findViewById<MaterialButton>(R.id.login_button)

        // Login button click
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

            // TODO: Add your login logic here
            Toast.makeText(requireContext(), "Logging in...", Toast.LENGTH_SHORT).show()
        }

        // Go to signup
        view.findViewById<TextView>(R.id.go_to_signup).setOnClickListener {
            findNavController().navigate(R.id.signupFragment)
        }

        // Forgot password
        view.findViewById<TextView>(R.id.forgot_password).setOnClickListener {
            Toast.makeText(requireContext(), "Reset link sent!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}