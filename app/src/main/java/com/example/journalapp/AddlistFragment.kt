package com.example.journalapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.journalapp.databinding.FragmentAddlistBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date


class AddlistFragment : Fragment() {

    private var _binding: FragmentAddlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var getContent: ActivityResultLauncher<Intent>

    // credentials

    var currentUserId: String = ""
    var currentUserName: String = ""

    //firebase
    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser

    //Firebase Firestore
    var db: FirebaseFirestore = FirebaseFirestore
        .getInstance()
    lateinit var storageReference: StorageReference
    var collectionReference: CollectionReference =
        db.collection("Journal")
    lateinit var imageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddlistBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storageReference = FirebaseStorage.getInstance().reference


//        auth = FirebaseAuth.getInstance()
        // //or
        auth  = Firebase.auth

        binding.apply {

            postProgressBar.visibility = View.GONE
            if (JournalUser.instance != null) {
//                currentUserId = JournalUser.instance!!.userId.toString()
//                currentUserName = JournalUser.instance!!.username.toString()

                currentUserId = auth.currentUser?.uid.toString()
                currentUserName = auth.currentUser?.displayName.toString()


                postUsernameTextview.text = currentUserName

            }
            getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val uri: Uri? = data?.data
                    if (uri != null) {
                        imageUri = uri
                        binding.postImageView.setImageURI(imageUri)
                    }
                }

            }

            // Getting image from Gallery
            postCameraButton.setOnClickListener {

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                getContent.launch(intent)
            }

            postSaveJournalButton.setOnClickListener {
                saveJournal()

            }

        }


    }

    private fun saveJournal() {
        var title: String = binding.postTitle.text.toString().trim()
        var description = binding.description.text.toString().trim()
        binding.postProgressBar.visibility = View.VISIBLE

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && imageUri != null) {


            //saving the path of image in storage
            // ......../journal_image/our_image.png
            val filepath: StorageReference = storageReference
                .child("journal_image")
                .child("my_image_" + Timestamp.now().seconds)

            //uploading the image
            filepath.putFile(imageUri)
                .addOnCompleteListener {
                filepath.downloadUrl.addOnSuccessListener {
                    var imageUri: Uri = it
                    var timeStamp: Timestamp = Timestamp(Date())

                    // Creating the object of Journal
                    var journal: Journal = Journal(
                        title,
                        description,
                        imageUri.toString(),
                        currentUserId,
                        timeStamp,
                        currentUserName


                    )
                    //adding the new journal
                    collectionReference.add(journal).addOnSuccessListener {
                        binding.postProgressBar.visibility = View.GONE
                        findNavController().navigate(R.id.action_addlistFragment_to_journalListFragment)

                    }


                }

            }.addOnFailureListener {
                binding.postProgressBar.visibility = View.GONE
            }
        } else {
            binding.postProgressBar.visibility = View.GONE
        }

    }



    override fun onStart() {
        super.onStart()
        user = auth.currentUser!!
    }

    override fun onStop() {
        super.onStop()
        if (auth != null) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}