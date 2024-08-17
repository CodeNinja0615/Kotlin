package com.example.simpleapicalldemo

// Top-level data class
data class ResponseData(
    val message: String,
    val user_id: Int,
    val name: String,
    val email: String,
    val mobile: Long,
    val profile_details: ProfileDetails,
    val data_list: List<DataListDetail>
)

// Nested data class for profile details
data class ProfileDetails(
    val is_profile_completed: Boolean,
    val rating: Double
)

// Data class for items in the data_list
data class DataListDetail(
    val id: Int,
    val value: String
)
