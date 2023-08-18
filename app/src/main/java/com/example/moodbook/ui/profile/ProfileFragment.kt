package com.example.moodbook.ui.profile

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.content.SharedPreferences
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moodbook.LoginActivity
import com.example.moodbook.R
import com.example.moodbook.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Calendar

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
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        first_name_txt = binding.firstnameTextview
        first_name_txt.getBackground().alpha = 75
       /* profileViewModel.first_name_txt.observe(viewLifecycleOwner) {
           first_name_txt.text = it
            Log.i("create first name", first_name_txt.text.toString())
       }*/
        country_txt =binding.countryTextview
        country_txt.getBackground().alpha = 75
        profileViewModel.country_txt.observe(viewLifecycleOwner) {
            country_txt.text = it
        }
        dob_txt = binding.dobTextview
        dob_txt.getBackground().alpha = 75
        profileViewModel.dob_txt.observe(viewLifecycleOwner) {
            dob_txt.text = it
        }
        gender_txt = binding.genderTextview
        gender_txt.getBackground().alpha = 75
        profileViewModel.gender_txt.observe(viewLifecycleOwner) {
            gender_txt.text = it
        }
        edit_btn = binding.editButton
        profilepic = binding.imagetoupload
        logout = binding.logout
        myDatabase = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!

        mStorageRef = FirebaseStorage.getInstance().getReference(user!!.uid)
        updateLabels()
        if (user!!.photoUrl != null) {
            //  Glide.with(this)
            //   .load(user!!.photoUrl)
            //   .into(profilepic)
        }
        logout.setOnClickListener(View.OnClickListener {
            val b = AlertDialog.Builder(it.getContext())
            b.setMessage("Are you sure you want to sign out?")
            b.setCancelable(true)
            b.setNegativeButton("Yes") { dialog, which ->
                dialog.cancel()
                val i = Intent(activity, LoginActivity::class.java)
                startActivity(i)
                /*val sharedPref =
                    this.getActivity()?.getSharedPreferences("com.example.moodbook", Context.MODE_PRIVATE)
                val prefEditor = sharedPref?.edit()
                val number = sharedPref?.getInt("isLogged", 0)
                prefEditor?.putInt("isLogged", 0)
                    prefEditor?.commit()
                Log.i("isLogged:, ", number.toString())*/
            }

            b.setPositiveButton("No") { dialog, which -> dialog.dismiss() }
            val a = b.create()
            a.show()
        })
        profilepic.setOnClickListener(View.OnClickListener {
            val gallery = Intent()
            gallery.type = "image/*"
            gallery.action = Intent.ACTION_GET_CONTENT
            startActivity(
                Intent.createChooser(gallery, "Select Picture"),
                //ProfileActivity.PICK_IMAGE
                  )
            imageUri = gallery.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                profilepic!!.setImageBitmap(bitmap)
                uploadFile(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        edit_btn.setOnClickListener(View.OnClickListener {
            val editProfileDialogView = layoutInflater.inflate(R.layout.edit_profile_dialog, null)
            val name = editProfileDialogView.findViewById<EditText>(R.id.name)
            val dob = editProfileDialogView.findViewById<EditText>(R.id.dob)
            val country = editProfileDialogView.findViewById<EditText>(R.id.country)
            // final EditText gender = editProfileDialogView.findViewById(R.id.genderlabel);
            val uid = user!!.uid
            dob.setOnClickListener {
                val cal = Calendar.getInstance()
                val year = cal[Calendar.YEAR]
                val month = cal[Calendar.MONTH]
                val day = cal[Calendar.DAY_OF_MONTH]
                val d = DatePickerDialog(
                    editProfileDialogView.context,
                    dateSetListener,
                    year, month, day
                )
                d.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                d.show()
            }
            dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                var month = month
                month = month + 1
                dob.setText("$dayOfMonth/$month/$year")
                Log.d("task added", "date:$dayOfMonth/$month/$year")
            }
            val dialog = AlertDialog.Builder(it.getContext())
                .setView(editProfileDialogView)
                .setPositiveButton(null) { dialog, which ->
                    if (!name.text.toString().isEmpty()) {
                        myDatabase!!.collection("users").document(uid)
                            .update("Name", name.text.toString())
                    }
                    if (!dob.text.toString().isEmpty()) {
                        myDatabase!!.collection("users").document(uid)
                            .update("DOB", dob.text.toString())
                    }
                    if (!country.text.toString().isEmpty()) {
                        myDatabase!!.collection("users").document(uid)
                            .update("Country", country.text.toString())
                    }
                    //            if (!gender.getText().toString().isEmpty()) {
                    //               myDatabase.collection("users").document(uid).update("Gender", gender.getText().toString());
                    //         }
                    updateLabels()
                }
                .setPositiveButtonIcon(
                    AppCompatResources.getDrawable(
                        it.context,
                        R.drawable.complete_task
                    )
                )
                .create()
            dialog.show()
        })

        return root
    }


    fun updateLabels() {
        if (user != null) {
            val uid = user!!.uid
            myDatabase!!.collection("users").document(uid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val docsnap = task.result
                    val name = docsnap.getString("Name")
                    Log.i("docsnap", name.toString())
                    val country = docsnap.getString("Country")
                    val dob = docsnap.getString("DOB")
                    val gender = docsnap.getString("Gender")
                    first_name_txt!!.text = name
                    country_txt!!.text = country
                    dob_txt!!.text = dob
                    gender_txt!!.text = gender

                }
            }
        }
    }


    private fun getFileExtension(uri: Uri): String? {

        val cR = context?.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR!!.getType(uri))
    }

    private fun uploadFile(b: Bitmap) {
        val baos = ByteArrayOutputStream()
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val reference = FirebaseStorage.getInstance().reference
            .child("profileImages")
            .child("$uid.jpeg")
        reference.putBytes(baos.toByteArray())
            .addOnSuccessListener {
                getDownloadUrl(reference)
                Toast.makeText(getActivity(),"Uploaded Successfully", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    getActivity(),
                    "Upload Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun getDownloadUrl(reference: StorageReference) {
        reference.downloadUrl
            .addOnSuccessListener { uri -> setUserProfileUrl(uri) }
    }

    private fun setUserProfileUrl(uri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        val request = UserProfileChangeRequest.Builder()
            .setPhotoUri(uri)
            .build()
        user!!.updateProfile(request)
            .addOnSuccessListener {
                Toast.makeText(
                    getActivity(),
                    "Updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    getActivity(),
                    "Profile image failed...",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    companion object {
        private const val PICK_IMAGE = 1
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}