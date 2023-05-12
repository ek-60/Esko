package com.example.androidapp.data.user

import com.google.gson.annotations.SerializedName


data class User (

  @SerializedName("id"            ) var id            : Int?    = null,
  @SerializedName("name"          ) var name          : String? = null,
  @SerializedName("age"           ) var age           : String? = null,
  @SerializedName("weight"        ) var weight        : String? = null,
  @SerializedName("bloodPressure" ) var bloodPressure : String? = null,
  @SerializedName("oxygen"        ) var oxygen        : String? = null

)