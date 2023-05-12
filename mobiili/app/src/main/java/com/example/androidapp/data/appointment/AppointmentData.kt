package com.example.androidapp.ui.checkAppointment

import com.google.gson.annotations.SerializedName


data class AppointmentData (

  @SerializedName("id"     ) var id     : Int?    = null,
  @SerializedName("userId" ) var userId : String? = null,
  @SerializedName("date"   ) var date   : String? = null,
  @SerializedName("time"   ) var time   : String? = null

)