package com.example.androidapp.ui.startPage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.data.user.User
import com.example.androidapp.databinding.RecyclerviewUserBinding

class RecyclerAdapterUser(private val user: List<User>) :
    RecyclerView.Adapter<RecyclerAdapterUser.UserHolder>() {

    //Alustetaan bindig layout -> recyclerview_todo.xml
    private var _binding: RecyclerviewUserBinding? = null
    private val binding get() = _binding!!

    //RecyclerAdapter vaatti että luokassa on toteutettuna:
    //getItemCount, onCreateView, OnBindViewHolder

    //Jotta RecyclerView tietää kuinka monta itemiä listassa on
    override fun getItemCount() = user.size

    //Tämä funktio kytkee jokaisen yksittäisen
    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val userComment = user[position]
        holder.userComment(userComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        _binding = RecyclerviewUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return UserHolder(binding)
    }

    class UserHolder(v: RecyclerviewUserBinding) : RecyclerView.ViewHolder(v.root), View.OnClickListener {

        private var view: RecyclerviewUserBinding = v
        private var user: User? = null

        init {
            v.root.setOnClickListener(this)
        }

        fun userComment(user: User) {
            this.user = user

            view.textViewUserName.text = user.name
        }

        override fun onClick(v: View) {
            Log.d("RecyclerUser", "user: " + user?.name.toString())

            val action = ApiUserFragmentDirections.actionApiUserFragmentToUserInfoFragment(user?.id as Int)
            v.findNavController().navigate(action)
        }
    }

}