package com.example.moodbook.ui.profile

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
//import com.bumptech.glide.Glide
import com.example.moodbook.LoginActivity
import com.example.moodbook.MainActivity
import com.example.moodbook.R
import com.example.moodbook.databinding.ActivityProfileBinding
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

class ProfileActivity : MainActivity() {

    private lateinit var binding: ActivityProfileBinding
    private  lateinit var edit_btn: Button
    private  lateinit var first_name_txt: TextView
    private  lateinit var country_txt: TextView
    private  lateinit  var dob_txt: TextView
    private lateinit var dateSetListener: OnDateSetListener
    private  lateinit var user: FirebaseUser
    private  lateinit var gender_txt: TextView
    private  lateinit var profilepic: ImageView
    private  lateinit var logout: Button
    lateinit  var  imageUri: Uri
    lateinit var myDatabase: FirebaseFirestore
    private lateinit var mStorageRef: StorageReference
    private val mUploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // val contentFrameLayout = findViewById<FrameLayout>(R.id.frag_container)
       // layoutInflater.inflate(R.layout.activity_profile, contentFrameLayout)

        first_name_txt = binding.firstnameTextview
        first_name_txt.getBackground().alpha = 75
        country_txt = findViewById(R.id.country_textview)
        country_txt.getBackground().alpha = 75
        dob_txt = findViewById(R.id.dob_textview)
        dob_txt.getBackground().alpha = 75
        gender_txt = findViewById(R.id.gender_textview)
        gender_txt.getBackground().alpha = 75
        edit_btn = findViewById(R.id.editButton)
        profilepic = findViewById(R.id.imagetoupload)
        logout = findViewById(R.id.logout)
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
            val b = AlertDialog.Builder(this@ProfileActivity)
            b.setMessage("Are you sure you want to sign out?")
            b.setCancelable(true)
            b.setNegativeButton("Yes") { dialog, which ->
                dialog.cancel()
                val i = Intent(applicationContext, LoginActivity::class.java)
                startActivity(i)
            }
            b.setPositiveButton("No") { dialog, which -> finish() }
            val a = b.create()
            a.show()
        })
        profilepic.setOnClickListener(View.OnClickListener {
            val gallery = Intent()
            gallery.type = "image/*"
            gallery.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE)
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
            dateSetListener = OnDateSetListener { view, year, month, dayOfMonth ->
                var month = month
                month = month + 1
                dob.setText("$dayOfMonth/$month/$year")
                Log.d("task added", "date:$dayOfMonth/$month/$year")
            }
            val dialog = AlertDialog.Builder(this@ProfileActivity)
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
                        this@ProfileActivity,
                        R.drawable.complete_task
                    )
                )
                .create()
            dialog.show()
        })
    }

    fun updateLabels() {
        if (user != null) {
            val uid = user!!.uid
            myDatabase!!.collection("users").document(uid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val docsnap = task.result
                    val name = docsnap.getString("Name")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                profilepic!!.setImageBitmap(bitmap)
                uploadFile(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
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
                Toast.makeText(this@ProfileActivity, "Uploaded Successfully", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@ProfileActivity,
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
                    this@ProfileActivity,
                    "Updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@ProfileActivity,
                    "Profile image failed...",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    companion object {
        private const val PICK_IMAGE = 1
    }
}