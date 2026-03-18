package com.bond.peerprep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val prefs  = requireActivity().getSharedPreferences("PeerPrep",
            android.content.Context.MODE_PRIVATE)
        val userId = prefs.getString("userId", "") ?: ""

        // Load profile from Firestore
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val name      = doc.getString("name") ?: ""
                val email     = doc.getString("email") ?: ""
                val phone     = doc.getString("phone") ?: ""
                val role      = doc.getString("role") ?: ""
                val createdAt = doc.getLong("createdAt") ?: 0L

                val firstName = name.split(" ").firstOrNull() ?: name

                // Avatar — first letter
                view.findViewById<TextView>(R.id.profile_avatar).text =
                    firstName.firstOrNull()?.uppercase() ?: "U"

                // Name
                view.findViewById<TextView>(R.id.profile_name).text = name

                // Role badge
                view.findViewById<TextView>(R.id.profile_role).text =
                    when (role) {
                        "student"    -> "🎓 Student"
                        "instructor" -> "👨‍🏫 Instructor"
                        "admin"      -> "👑 Admin"
                        else         -> role
                    }

                // Email
                view.findViewById<TextView>(R.id.profile_email).text = email

                // Phone
                view.findViewById<TextView>(R.id.profile_phone).text =
                    phone.ifEmpty { "Not provided" }

                // Member since
                view.findViewById<TextView>(R.id.profile_since).text =
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(Date(createdAt))
            }

        // Logout button
        view.findViewById<MaterialButton>(R.id.profile_logout).setOnClickListener {
            auth.signOut()
            prefs.edit().clear().apply()
            findNavController().navigate(R.id.loginFragment)
        }

        return view
    }
}