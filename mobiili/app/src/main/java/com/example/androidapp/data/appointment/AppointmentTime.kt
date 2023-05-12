package com.example.androidapp.ui.appointmentPage

import com.google.gson.annotations.SerializedName


data class AppointmentTime (

  @SerializedName("id"         ) var id         : Int?    = null,
  @SerializedName("kellonaika" ) var kellonaika : String? = null

)