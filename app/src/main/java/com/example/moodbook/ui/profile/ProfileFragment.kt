package com.example.moodbook.ui.profile

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moodbook.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private  lateinit var edit_btn: Button
    private  lateinit var first_name_txt: TextView
    private  lateinit var country_txt: TextView
    private  lateinit  var dob_txt: TextView
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private  lateinit var user: FirebaseUser
    private  lateinit var gender_txt: TextView
    private  lateinit var profilepic: ImageView
    private  lateinit var logout: Button
    lateinit  var  imageUri: Uri
    lateinit var myDatabase: FirebaseFirestore
    private lateinit var mStorageRef: StorageReference
    private val mUploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val statViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

      //  val textView: TextView = binding.tvProfilo
       // statViewModel.text.observe(viewLifecycleOwner) {
          //  textView.text = it
       // }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}