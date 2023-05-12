package com.example.androidapp.ui.userPage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidapp.data.patientInfo.PatientInfo
import com.example.androidapp.data.user.User
import com.example.androidapp.databinding.FragmentUserInfoBinding
import com.google.gson.GsonBuilder
import org.json.JSONObject


class UserInfoFragment : Fragment() {
    // change this to match your fragment name
    private var _binding: FragmentUserInfoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // get fragment parameters from previous fragment
    val args : UserInfoFragmentArgs by navArgs()

    //Funktio hakee Potilastiedot DB tiedot
    //Lisätään DATA_URL, User DB id, jotta pääsemme käsiksi valitun henkilön tietoihin
    fun getUserData(userId: String) {
        // this is the url where we want to get our data from
        val DATA_URL = "http://95.216.173.52:8055/items/potilastiedot?access_token=bW-wll4QD6d84BLHnl9XbLx8VREoX_pH&filter[user_id][_eq]=${userId}&sort=-timestamp"

        sendCurrentUserToDatabase(userId)

        val gson = GsonBuilder().setPrettyPrinting().create()
        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, DATA_URL,
            Response.Listener { response ->

                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                val jObject = JSONObject(response)
                val jArray = jObject.getJSONArray("data")

                val rows : List<PatientInfo> = gson.fromJson(jArray.toString(), Array<PatientInfo>::class.java).toList()
                Log.d("Info", rows.toString())

                for(item: PatientInfo in rows) {

                    when (item.tyyppi) {
                        "bloodPressure" ->  if (binding.textViewGetBloodpressure.text == "TextView") binding.textViewGetBloodpressure.text = item.arvo.toString()
                        "oxygen" -> if (binding.textViewGetOxygen.text == "TextView") binding.textViewGetOxygen.text = item.arvo.toString()
                        "weight" -> if (binding.textViewGetWeight.text == "TextView") binding.textViewGetWeight.text = item.arvo.toString()
                    }
                }
            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.e("Info", it.toString())
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

    fun sendCurrentUserToDatabase(userId: String) {
        //https://b6i43d47.directus.app
        // http://95.216.173.52:8055
        val PATCH_URL = "http://95.216.173.52:8055/items/currentUser/1?access_token=bW-wll4QD6d84BLHnl9XbLx8VREoX_pH"

        // Lyödään payloadi jsoniksi
        val jsonBody = JSONObject()
        jsonBody.put("pid", 1)
        jsonBody.put("active_user", "${userId}")

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.PATCH, PATCH_URL,
            Response.Listener {},
            Response.ErrorListener {  Log.e("currentUser", it.toString())  })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                // basic headers for the data
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }

            override fun getBody(): ByteArray {
                // Muutetaan JSONObjecti bitti arrayksi
                return jsonBody.toString().toByteArray();
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        // if using this in an activity, use "this" instead of "context"
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    fun printBasicData(userId: String) {
        val JSON_URL = "http://95.216.173.52:8055/items/user/${userId}?access_token=bW-wll4QD6d84BLHnl9XbLx8VREoX_pH"

        getUserData(userId.toString())

        val gson = GsonBuilder().setPrettyPrinting().create()

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, JSON_URL,
            Response.Listener { response ->
                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                val jObject = JSONObject(response)
                val jArray = jObject.getJSONObject("data")

                val item : User = gson.fromJson(jArray.toString(), User::class.java)
                Log.d("Info", "name ${item.name} age ${item.age} id ${args.id}")

                //Printataan valitun itemin tiedot ja laitetaan ne paikalleen fragmentissa
                binding.textViewGetName.text = item.name.toString()
                binding.textViewGetAge.text = item.age.toString()

                binding.textViewStartListening.setOnClickListener{ v:View ->
                    val action = UserInfoFragmentDirections.actionUserInfoFragmentToListeningFragment(args.id)
                    v.findNavController().navigate(action)
                }

            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.e("Info", it.toString())
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Ottaa User DB id, jotta pystymme yhdistämään User DB id:n, Potilastiedot DB user_id
        val userId = args.id.toString()

        printBasicData(userId)
        getUserData(userId)

        binding.imageViewBackToStart.setOnClickListener{ v: View ->
            val action = UserInfoFragmentDirections.actionUserInfoFragmentToApiUserFragment()
            v.findNavController().navigate(action)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}