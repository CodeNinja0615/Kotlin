package com.example.happyplaces.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import java.lang.StringBuilder
import java.util.Locale

class GetAddressFromLatLng(context: Context, private val latitude: Double, private val longitude: Double)
    : AsyncTask<Void, String, String>() {

    private val geoCoder: Geocoder = Geocoder(context, Locale.getDefault())
    private lateinit var mAddressListener:AddressListener

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): String {
        val addressList: List<Address>? = geoCoder.getFromLocation(latitude, longitude, 1) //-------To translate LatLng to readable address
        try {
            if (addressList != null && addressList.isNotEmpty()){
                val address: Address = addressList[0] //Since max result is 1

                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex){
                    sb.append(address.getAddressLine(i)).append(" ")
                }
                sb.deleteCharAt(sb.length - 1) //-----remove last empty space

                return sb.toString()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return ""
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(resultString: String?) {
        if (resultString == null){
            mAddressListener.onError() //----- Error if returned string from doInBackground is null
        }else{
            mAddressListener.onAddressFound(resultString) //--------------taking address with interface to another file
        }
        super.onPostExecute(resultString)
    }

    //------------Below function will be used to access the interface below
    fun setAddressListener(addressListener: AddressListener){
        mAddressListener = addressListener
    }

    fun getAddress(){
        execute()
    }

    interface AddressListener{
        fun onAddressFound(address: String)
        fun onError()
    }
}