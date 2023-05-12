package com.example.androidapp.ui.checkAppointment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidapp.databinding.FragmentCheckAppointmentBinding
import org.json.JSONObject
import java.util.HashMap

class CheckAppointmentFragment : Fragment() {
    // change this to match your fragment name
    private var _binding: FragmentCheckAppointmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val args : CheckAppointmentFragmentArgs by navArgs()

    fun saveAppointment(jsonBody: JSONObject) {
        val SAVE_URL = "http://95.216.173.52:8055/items/varatutAjat?access_token=bW-wll4QD6d84BLHnl9XbLx8VREoX_pH"

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, SAVE_URL,
            Response.Listener {},
            Response.ErrorListener {  Log.e("Listening", it.toString())  })
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

    fun saveAllAppointment() {

        val jsonBody = JSONObject()
        jsonBody.put("date", args.date)
        jsonBody.put("time", args.time)
        jsonBody.put("userId", args.id)
        Log.d("Check", "Tallennettu: " + args.date + args.time + " ID: " + args.id)
        saveAppointment(jsonBody)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckAppointmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val date = args.date
        val time = args.time
        Log.d("Check", "Kellonaika: " + time + " Päivämäärä: " + date)

        Log.d("Check", "UserID: " + args.id)

        binding.textViewShowDate.text = date
        binding.textViewShowTime.text = time


        binding.imageViewBackToAppointment.setOnClickListener { v: View ->
            val action = CheckAppointmentFragmentDirections.actionCheckAppointmentFragmentToAppointmentFragment(args.id)
            v.findNavController().navigate(action)
        }

        binding.imageViewSaveAppointment.setOnClickListener { v: View ->
            saveAllAppointment()

            val action = CheckAppointmentFragmentDirections.actionCheckAppointmentFragmentToApiUserFragment()
            v.findNavController().navigate(action)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}