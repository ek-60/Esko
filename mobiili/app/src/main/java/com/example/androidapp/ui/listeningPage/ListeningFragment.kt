package com.example.androidapp.ui.listeningPage

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidapp.data.user.User
import com.example.androidapp.databinding.FragmentListeningBinding
import com.example.androidapp.databinding.FragmentUserInfoBinding
import com.example.androidapp.ui.userPage.UserInfoFragmentArgs
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.typeOf


class ListeningFragment : Fragment() {
    private var _binding: FragmentListeningBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_CODE_SPEECH_INPUT = 1
    private var speachtotext = "null"

    val args : ListeningFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListeningBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Log.d("Listening", "UserID: " + args.id)
        startListening()

        binding.imageViewListen.setOnClickListener{
            startListening()
        }

        binding.imageViewBackToUserInfo.setOnClickListener { v: View ->
            val action = ListeningFragmentDirections.actionListeningFragmentToUserInfoFragment(args.id)
            v.findNavController().navigate(action)
        }

        binding.imageViewSave.setOnClickListener { v : View ->
            Log.d("Listening", "Tiedot tallennettu")
            saveAllData()

            Toast.makeText(context,"Tiedot tallennettu", Toast.LENGTH_LONG).show()
            //val action = ListeningFragmentDirections.actionListeningFragmentToApiUserFragment()
            //v.findNavController().navigate(action)
        }

        binding.buttonNewTime.setOnClickListener{ v: View ->
            val action = ListeningFragmentDirections.actionListeningFragmentToAppointmentFragment(args.id)
            v.findNavController().navigate(action)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun saveAllData() {

        if (binding.textViewGetBloodpressure.text != "TextView") {
            val jsonBody = JSONObject()
            jsonBody.put("user_id", args.id)
            jsonBody.put("tyyppi", "bloodPressure")
            jsonBody.put("arvo", "${binding.textViewGetBloodpressure.text}")
            saveData(jsonBody)
        }

        if (binding.textViewGetWeight.text != "TextView") {
            val jsonBody = JSONObject()
            jsonBody.put("user_id", args.id)
            jsonBody.put("tyyppi", "weight")
            jsonBody.put("arvo", "${binding.textViewGetWeight.text}")
            saveData(jsonBody)
        }

        if (binding.textViewGetOxygen.text != "TextView") {
            val jsonBody = JSONObject()
            jsonBody.put("user_id", args.id)
            jsonBody.put("tyyppi", "oxygen")
            jsonBody.put("arvo", "${binding.textViewGetOxygen.text}")
            saveData(jsonBody)
        }
    }

    fun saveData(jsonBody: JSONObject) {
        val SAVE_URL = "http://95.216.173.52:8055/items/potilastiedot/?access_token=bW-wll4QD6d84BLHnl9XbLx8VREoX_pH"

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

    fun startListening() {
        // on below line we are calling speech recognizer intent.
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        // on below line we are passing language model
        // and model free form in our intent
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        // on below line we are passing our
        // language as a default language.
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

        // on below line we are specifying a prompt
        // message as speak to text on below line.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Log.e("Listening", "${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // in this method we are checking request
        // code with our result code.
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            // on below line we are checking if result code is ok
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {

                // in that case we are extracting the
                // data from our array list
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                // on below line we are setting data
                // to our output text view.
                speachtotext = "null " + Objects.requireNonNull(res)[0]
                Log.d("Listening", speachtotext)

                parseSpeach((speachtotext))
            }
        }
    }

    fun parseSpeach(input: String) {
        try {
            val arr = input.replace(Regex("(on |ja |yl?(ä|äpaine) |al?(a|apaine) |/)"), "").split(" ")

            val res = object {
                var verenpaine =
                    "${arr[arr.indexOf("verenpaine") + 1]}/${arr[arr.indexOf("verenpaine") + 2]}"
                var paino = arr[arr.indexOf("paino") + 1]
                var pituus = arr[arr.indexOf("pituus") + 1]
                var happisaturaatio = arr[arr.indexOf("happisaturaatio") + 1]
                var lampo = arr[arr.indexOf("lämpötila") + 1]
            }

            Log.d("Listening", "paino ${res.paino}")
            Log.d("Listening", "happisaturaatio ${res.happisaturaatio}")
            Log.d("Listening", "paino ${res.paino}")
            Log.d("Listening", "verenpaine ${res.verenpaine}")

            if (!res.verenpaine.contains("null")) {
                binding.textViewGetBloodpressure.text = res.verenpaine
            }
            if (!res.happisaturaatio.contains("null")) {
                binding.textViewGetOxygen.text = res.happisaturaatio
            }
            if (!res.paino.contains("null")) {
                binding.textViewGetWeight.text = res.paino
            }

        } catch (e: Exception) {
            Log.e("Listening", e.toString())
        }
    }
}