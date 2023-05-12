package com.example.androidapp.ui.startPage

import android.R
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidapp.data.user.User
import com.example.androidapp.databinding.FragmentApiUserBinding
import com.google.gson.GsonBuilder
import org.json.JSONObject


class ApiUserFragment : Fragment() {

    // change this to match your fragment name
    private var _binding: FragmentApiUserBinding? = null
    private lateinit var adapter: RecyclerAdapterUser

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // This is layout managetr recyclerviewe
    private lateinit var linearLayoutManager: LinearLayoutManager

    //var userName: String = ""
    var userNameArray = mutableListOf<String>()
    //var userId: String = ""
    var userIdArray = mutableListOf<String>()
    var fullArray = mutableListOf<String>()
    var search : String= ""



    //Funktio hakee User DB kaikki käyttäjät yhdistäen ne RecyclerAdapterUseriin
    fun getUser() {
        // this is the url where we want to get our data from
        val JSON_URL = "http://95.216.173.52:8055/items/user?access_token=bW-wll4QD6d84BLHnl9XbLx8VREoX_pH"

        val gson = GsonBuilder().setPrettyPrinting().create()
        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, JSON_URL,
            Response.Listener { response ->

                Log.d("User", response)
                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                val jObject = JSONObject(response)
                val jArray = jObject.getJSONArray("data")
                Log.d("User", "jOb" + jArray)

                val rows : List<User> = gson.fromJson(jArray.toString(), Array<User>::class.java).toList()
                Log.d("User", rows.toString())


                for(item: User in rows) {
                    //userName = item.name.toString()

                    //userName = userName + "-" + item.name.toString()
                    //userId = userId + "-" + item.id.toString()
                    userNameArray.add(item.name.toString())
                    userIdArray.add(item.id.toString())
                    fullArray.add("${item.name?.toLowerCase()}--${item.id}")
                    Log.d("User", "potilaan nimi: " + item.name + " potilaanID " + item.id)
                }

                a()
                adapter = RecyclerAdapterUser(rows)
                binding.recyclerViewTodos.adapter = adapter

            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.d("User", it.toString())
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                // basic headers for the data
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        // if using this in an activity, use "this" instead of "context"
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)

    }

    fun a() {

        // Declare array of elements, create an adapter
        // and display the array in the ListView

        val mArrayAdapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, R.id.text1, userNameArray)
        binding.listView.adapter = mArrayAdapter

        binding.editTextGiveName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //binding.recyclerViewTodos.adapter.fil
                mArrayAdapter.filter.filter(s)
                if (s != null) {
                    search = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do Nothing
            }
        })

        binding.listView.setOnItemClickListener { user, view, i, l ->
            //Log.d("bindingListView", userNameArray[i] + " id: " + userIdArray[i])

            var id: Int = Integer.parseInt(userIdArray[i])
            if (search != "") {
                fullArray = fullArray.filter { e -> e.contains(search) } as MutableList<String>
                Log.d("Search", fullArray.toString())
                id = Integer.parseInt(fullArray[i].split("--")[1])
            }

            val action = ApiUserFragmentDirections.actionApiUserFragmentToUserInfoFragment(id)
            view.findNavController().navigate(action)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApiUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Keyboard ei työnnä layouttia ylöspäin
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        getUser()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}