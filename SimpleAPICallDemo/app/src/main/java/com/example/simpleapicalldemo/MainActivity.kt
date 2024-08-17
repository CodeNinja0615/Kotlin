package com.example.simpleapicalldemo

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.simpleapicalldemo.databinding.ActivityMainBinding
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        //binding?.toolBarMainActivity?.setNavigationOnClickListener {
            //onBackPressed()
        //}
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hide navigation bar (home, back buttons)
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Keep the app in immersive mode
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Extend content to cover the status bar
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Extend content to cover the navigation bar
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Keep layout stable during transitions
            or View.STATUS_BAR_HIDDEN // Hides the status bar
        )


        window.statusBarColor = ContextCompat.getColor(this, R.color.red)

        CallAPILoginAsyncTask("Sameer", "123456").execute()
    }
    @SuppressLint("StaticFieldLeak")
    private inner class CallAPILoginAsyncTask(val username: String, val password: String): AsyncTask<Any, Void, String>(){
        private lateinit var customProgressDialog: Dialog

        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Any?): String { // The background task method for performing network operations.
            var result: String // Variable to store the final result (you'll need to set it appropriately).
            var connection: HttpURLConnection? = null // Declare a HttpURLConnection object.

            try {
                // URL of the web service.
                val url = URL("https://run.mocky.io/v3/c8f4c787-a33d-4733-891e-3d2c8a641855")

                // Open a connection to the specified URL.
                connection = url.openConnection() as HttpURLConnection

                // Allow both input and output streams for this connection.
                connection.doInput = true
                connection.doOutput = true

                connection.instanceFollowRedirects = false

                //----------------------------------------------------------------POST----------------------------------------------------------//
                connection.requestMethod = "POST" //----Can use any
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.useCaches = false


                val writeDataOutputStream = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                jsonRequest.put("username", username)
                jsonRequest.put("password", password)


                writeDataOutputStream.writeBytes(jsonRequest.toString())
                writeDataOutputStream.flush()
                writeDataOutputStream.close()

                //----------------------------------------------------------------POST----------------------------------------------------------//


                // Get the HTTP response code.
                val httpResult: Int = connection.responseCode

                // Check if the response code indicates success.
                if (httpResult == HttpURLConnection.HTTP_OK) {
                    // Get the input stream from the connection.
                    val inputStream = connection.inputStream

                    // Create a BufferedReader to read the input stream line by line.
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    // Use a StringBuilder to accumulate the response data.
                    val stringBuilder = StringBuilder()
                    var line: String?

                    try {
                        // Read each line from the input stream until the end is reached.
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line + "\n") // Append each line to the StringBuilder.
                        }
                    } catch (e: IOException) {
                        // Handle any exceptions that occur during reading.
                        e.printStackTrace()
                    } finally {
                        // Close the input stream after reading is complete.
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    // Set the result with the accumulated response.
                    result = stringBuilder.toString()
                } else {
                    // Handle the case where the response code is not HTTP_OK.
                    result = "Error: HTTP response code ${connection.responseMessage}"
                }
            } catch (e: SocketTimeoutException) {
                // Handle any exceptions that occur during connection or data retrieval.
                e.printStackTrace()
                result = "Connection Timeout: ${e.message}" // Set the result with the error message.
            }catch (e: Exception){
                result = "Error: " + e.message
            } finally {
                // Disconnect the connection if it was established.
                connection?.disconnect()
            }

            return result // Return the final result.
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            cancelProgressDialog()
            Log.i("JSON Response Result: ", result!!)


            //-----------------------------------------------------------GSON--------------------------------------------------------------//



            val responseData = Gson().fromJson(result, ResponseData::class.java)

            Log.i("Message: ", responseData.message)
            Log.i("profile data: ", responseData.profile_details.is_profile_completed.toString())
            for (item in responseData.data_list.indices){
                Log.i("Value $item: ", responseData.data_list[item].toString())
                Log.i("ID: ", responseData.data_list[item].id.toString())
                Log.i("ValueD: ", responseData.data_list[item].value.toString())
            }








            //-----------------------------------------------------------GSON--------------------------------------------------------------//


            val jsonObject = JSONObject(result)

            // Extract and log simple string values
            val message = jsonObject.optString("message")
            Log.e("JSON Parsing", "Message: $message")

            val userId = jsonObject.optString("user_id")
            Log.e("JSON Parsing", "User ID: $userId")

            val name = jsonObject.optString("name")
            Log.e("JSON Parsing", "Name: $name")

            val email = jsonObject.optString("email")
            Log.e("JSON Parsing", "Email: $email")

            val mobile = jsonObject.optString("mobile")
            Log.e("JSON Parsing", "Mobile: $mobile")

            // Extract and log nested JSON object
            val profileDetailsObject = jsonObject.optJSONObject("profile_details")
            Log.e("JSON Parsing", "Profile Details: $profileDetailsObject")

            val isProfileCompleted = profileDetailsObject?.optBoolean("is_profile_completed")
            Log.e("JSON Parsing", "Is Profile Completed: $isProfileCompleted")

            // Extract and log JSON array
            val dataListArray = jsonObject.optJSONArray("data_list")
            Log.i("JSON Parsing", "Data List Array: $dataListArray")

            // Loop through the JSON array and log each item
            for (item in 0 until dataListArray!!.length()) {
                val dataItemObj: JSONObject = dataListArray[item] as JSONObject // because dataListArray[item] returns any
                Log.i("JSON Parsing", "Data Item at index $item: $dataItemObj")

                val id = dataItemObj.optInt("id")
                Log.i("ID", "$id")
                val value = dataItemObj.optString("value")
                Log.i("Value", value)
            }

            binding?.tvMain?.text = message

        }




        private fun showProgressDialog() {
            // Create a Dialog instance
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }
        private fun cancelProgressDialog() {
            // cancel a Dialog instance
            customProgressDialog.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null){
            binding = null
        }
    }
}