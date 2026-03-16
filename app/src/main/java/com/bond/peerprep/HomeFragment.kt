package com.bond.peerprep

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bond.peerprep.utils.Constants
import com.google.android.material.imageview.ShapeableImageView

class HomeFragment : Fragment() {

    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // ✅ Fetch data
        val contentList = Constants.getContent1()

        // ✅ Find views
        val contentBlock = view.findViewById<LinearLayout>(R.id.content_block)
        val cardTitle = view.findViewById<TextView>(R.id.item_title)
        val cardBody = view.findViewById<TextView>(R.id.item_body)
        val cardImage = view.findViewById<ImageView>(R.id.item_image)

        // ✅ Function to update content with fade animation
        fun updateCard(index: Int) {
            val content = contentList[index]

            // Slide out to left + fade out
            contentBlock.animate()
                .alpha(0f)
                .translationX(-100f)
                .setDuration(400)
                .setInterpolator(android.view.animation.AccelerateInterpolator())
                .withEndAction {

                    // Update content while invisible
                    cardTitle.text = content.title
                    cardBody.text = content.body
                    cardImage.setImageResource(content.image)

                    // Reset position to right side
                    contentBlock.translationX = 100f

                    // Slide in from right + fade in
                    contentBlock.animate()
                        .alpha(1f)
                        .translationX(0f)
                        .setDuration(400)
                        .setInterpolator(android.view.animation.DecelerateInterpolator())
                        .start()

                }.start()
        }

        // ✅ Show first item immediately
        updateCard(currentIndex)

        // ✅ Auto change every 5 seconds
        runnable = Runnable {
            currentIndex = (currentIndex + 1) % contentList.size
            updateCard(currentIndex)
            handler.postDelayed(runnable, 5000)
        }
        handler.postDelayed(runnable, 5000)

        // ✅ Spannable text
        val firstLineText = view.findViewById<TextView>(R.id.first_line_text)
        val text = "WELCOME TO THE PLACE\nWHERE CURIOSITY\nBECOMES CONFIDENCE"
        val spannable = SpannableString(text)

        val toStart = text.indexOf("TO")
        spannable.setSpan(ForegroundColorSpan(android.graphics.Color.parseColor("#22C55E")), toStart, toStart + "TO".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val curiosityStart = text.indexOf("CURIOSITY")
        spannable.setSpan(ForegroundColorSpan(android.graphics.Color.parseColor("#FACC15")), curiosityStart, curiosityStart + "CURIOSITY".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        firstLineText.text = spannable

        // ✅ Logo container glow
        val logoContainer = view.findViewById<LinearLayout>(R.id.logo_container)
        logoContainer.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    logoContainer.setBackgroundResource(R.drawable.container_glow)
                    logoContainer.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150).start()
                }
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    logoContainer.setBackgroundResource(R.drawable.container_default)
                    logoContainer.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
                }
            }
            false
        }


        // Floating button
        view.findViewById<com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton>(R.id.fab_book).setOnClickListener {
            // Navigate to contact or booking page
            val navHostFragment = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment)
            navHostFragment?.findNavController()?.navigate(R.id.contactFragment)
        }

        return view
    }

    // ✅ Stop auto change when fragment not visible
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    // ✅ Resume auto change when fragment visible again
    override fun onResume() {
        super.onResume()
        if (::runnable.isInitialized) {
            handler.postDelayed(runnable, 5000)
        }
    }



}