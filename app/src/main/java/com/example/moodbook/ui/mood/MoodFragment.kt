package com.example.moodbook.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moodbook.databinding.FragmentMoodtrackerBinding

class MoodFragment : Fragment() {

    private var _binding: FragmentMoodtrackerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(MoodViewModel::class.java)

        _binding = FragmentMoodtrackerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMood
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}