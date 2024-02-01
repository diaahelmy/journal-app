package com.example.journalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.journalapp.databinding.FragmentJournalListBinding
import com.example.journalapp.databinding.FragmentMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference


class journalListFragment : Fragment() {


    private var _binding: FragmentJournalListBinding? = null
    private val binding get() = _binding!!

    //
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var users: FirebaseUser
    val db = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference
    var collectionReference: CollectionReference = db.collection("Journal")

    lateinit var journalList: MutableList<Journal>
    lateinit var adapter: JournalRecyclerAdapter
    lateinit var noPostsTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentJournalListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //firebaseAuth
        firebaseAuth = Firebase.auth
        users = firebaseAuth.currentUser!!
        //RecyclerView
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        //postlist
        journalList = arrayListOf<Journal>()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
            R.id.action_Add -> if (users != null && firebaseAuth != null) {
                    findNavController().navigate(R.id.action_journalListFragment_to_addlistFragment)


            }

            R.id.action_signout -> if (users != null && firebaseAuth != null) {
                    firebaseAuth.signOut()
                    findNavController().navigate(R.id.action_journalListFragment_to_mainFragment)


            }


        }
        return super.onOptionsItemSelected(item)

    }

    override fun onStart() {
        super.onStart()
        collectionReference.whereEqualTo(
            "userId",
            users.uid)
            .get()
            .addOnSuccessListener {
                Log.i("TAGY", "sizey: ${it.size()}")

                if (!it.isEmpty) {

                    for (document in it) {
                        //convert snapshots to journal objects
                        var journal = Journal(
                            document.data["title"].toString(),
                            document.data["thoughts"].toString(),
                            document.data["imageUrl"].toString(),
                            document.data["userId"].toString(),
                            document.data["timeAdded"] as Timestamp,
                            document.data["username"].toString(),
                        )
                        journalList.add(journal)

                    }
                    //recyclerView
                    adapter = JournalRecyclerAdapter(
                        requireContext(), journalList

                    )
                    binding.recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    binding.textView2.visibility = View.VISIBLE
                }


            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Opps! Something went wrong!",
                    Toast.LENGTH_LONG
                ).show()

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}