package com.example.androidapp.ui.appointmentPage

import android.R
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidapp.databinding.FragmentAppointmentBinding
import com.example.androidapp.ui.checkAppointment.AppointmentData
import com.google.gson.GsonBuilder
import org.json.JSONObject
import kotlin.math.log


class AppointmentFragment : Fragment() {

    // change this to match your fragment name
    private var _binding: FragmentAppointmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val args : AppointmentFragmentArgs by navArgs()

    var time = mutableListOf<String>()
    var dbTable = mutableListOf<String>()

    private var dbTime: String = ""
    private var dbDate: String = ""

    //Hakee DB staattiset ajat
    fun getTimes() {
        // this is the url where we want to get our data from
        val JSON_URL = "http://95.216.173.52:8055/items/appointmentTime?access_token=bW-wll4QD6d84BLHnl9XbLx8VREoX_pH"

        val gson = GsonBuilder().setPrettyPrinting().create()
        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, JSON_URL,
            Response.Listener { response ->

                Log.d("Appointment", response)
                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                val jObject = JSONObject(response)
                val jArray = jObject.getJSONArray("data")
                Log.d("Appointment", "jOb" + jArray)

                val rows: List<AppointmentTime> = gson.fromJson(jArray.toString(), Array<AppointmentTime>::class.java).toList()
                Log.d("Appointment", "Kellonajat: " + rows)

                for(item: AppointmentTime in rows) {
                    time.add(item.kellonaika.toString())
                }

                Log.d("Appointment", "Time: " + time)

                //adapter = RecyclerAdapterUser(rows)
                //binding.recyclerViewTodos.adapter = adapter

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

    //Hakee DB mitkä ajat ovat varattuina
    fun getAppointmentData() {
        // this is the url where we want to get our data from
        val DATA_URL = "http://95.216.173.52:8055/items/varatutAjat?access_token=bW-wll4QD6d84BLHnl9XbLx8VREoX_pH"


        val gson = GsonBuilder().setPrettyPrinting().create()
        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, DATA_URL,
            Response.Listener { response ->

                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                val jObject = JSONObject(response)
                val jArray = jObject.getJSONArray("data")

                val rows: List<AppointmentData> = gson.fromJson(jArray.toString(), Array<AppointmentData>::class.java).toList()

                for(item: AppointmentData in rows) {
                    Log.d("Appointment", "dbTime: " + item.time + " dbDate: " + item.date)

                    dbDate = item.date.toString()
                    dbTime = item.time.toString()

                    dbTable.add(dbTime + " " + dbDate)
                    Log.d("Appointment", "dbTable: " + dbTable)
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
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Log.d("Appointment", "UserID: " + args.id)

        var date: String = ""

        val arrayAdapter: ArrayAdapter<*>

        val mListView = binding.listViewTimes
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, time)

        binding.textViewDate.text = date


        binding.calendarView.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
            date = (dayOfMonth.toString() + "-" + (month + 1) + "-" + year)
            Log.d("Appointment", date)

            binding.textViewDate.text = date

            mListView.adapter = arrayAdapter
            mListView.visibility = View.VISIBLE

        })


        binding.listViewTimes.setOnItemClickListener { adapterView, view, i, l, ->
            Log.d("Appointment", "Päivämäärä: " + date + " Kellonaika: " + time[i])

            var time: String  = time[i]
            var date: String = date

            Log.d("Appointment", "Item: " + i)

            var test = false
            dbTable.forEach()  {
                Log.d("Appointment", "it: " + it)
                if (it.split(" ")[0] == time && it.split(" ")[1] == date) {
                    test = true
                }
            }
            if (test) {
                binding.listViewTimes.getChildAt(i).setBackgroundColor(Color.RED)
                Toast.makeText(context,"Aika varattuna jo..", Toast.LENGTH_LONG).show()
            }
            else {
                binding.listViewTimes.getChildAt(i).setBackgroundColor(Color.GREEN)
                val action = AppointmentFragmentDirections.actionAppointmentFragmentToCheckAppointmentFragment(time, date, args.id)
                view.findNavController().navigate(action)
            }

        }

        binding.imageViewBackToListen.setOnClickListener { v: View ->
            val action = AppointmentFragmentDirections.actionAppointmentFragmentToListeningFragment(args.id)
            v.findNavController().navigate(action)
        }

        getTimes()
        getAppointmentData()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}