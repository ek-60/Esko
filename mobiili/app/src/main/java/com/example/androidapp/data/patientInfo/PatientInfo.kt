package com.example.androidapp.data.patientInfo

import com.google.gson.annotations.SerializedName


data class PatientInfo (

  @SerializedName("pid"       ) var pid       : Int?    = null,
  @SerializedName("user_id"   ) var userId    : Int?    = null,
  @SerializedName("tyyppi"    ) var tyyppi    : String? = null,
  @SerializedName("arvo"      ) var arvo      : String? = null,
  @SerializedName("timestamp" ) var timestamp : String? = null

)