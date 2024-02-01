package com.example.journalapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.journalapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Sign_UpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
// ...
// Initialize Firebase Auth

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUPButton.setOnClickListener {
            CreateUser()

        }
        auth = Firebase.auth
    }

    private fun CreateUser() {
        val email = binding.email.text.toString()
        val password = binding.passwordSignUp.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Email and password cannot be empty.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("diaa", "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        requireContext(),
                        "Authentication Succeeded.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(user)
                  findNavController().navigate(R.id.action_sign_UpFragment_to_journalListFragment)

                } else {
                    // If sign in fails, display a message to the user.
                    val exception = task.exception
                    exception?.message?.let { errorMessage ->
                        Log.e("diaa", "createUserWithEmail:failure", exception)
                        Toast.makeText(requireContext(), ". $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun reload() {
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}