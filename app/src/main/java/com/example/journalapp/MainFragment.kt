package com.example.journalapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.journalapp.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createAccountButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_sign_UpFragment)

        }

         binding.signINButton.setOnClickListener {
             LoginWithEmailPassword(
                 binding.username.text.toString().trim(),
                 binding.password.text.toString().trim()


                 )

         }
// auth Ref
        auth = Firebase.auth

    }
    private fun LoginWithEmailPassword(email: String,password:String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("diaamain", "createUserWithEmail:success")
                    val journal:JournalUser=JournalUser.instance!!
                    journal.userId = auth.currentUser?.uid
                    journal.username = auth.currentUser?.displayName

                    gotojournalList()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("diaamain", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext() ,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
//                    gotojournalList()
                }
            }



    }
    private fun gotojournalList(){
        findNavController().navigate(R.id.action_mainFragment_to_journalListFragment)


    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
           gotojournalList()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}