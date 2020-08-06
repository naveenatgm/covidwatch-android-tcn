package org.covidwatch.android.service

import android.util.Log
import org.covidwatch.android.GlobalConstants
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat

class ContactTracerService {

    @Throws(IOException::class, IllegalStateException::class)
    fun registerPhoneNumber(phoneNumber: String, isPrimary: Boolean): Boolean {
        val okHttpClient = OkHttpClient()

        val cal = java.util.Calendar.getInstance()
        val currentTime = cal.time

        val simpleDateFormat = SimpleDateFormat(GlobalConstants.MEDICAL_API_DATE_PATTERN)
        val formatted = simpleDateFormat.format(currentTime)

        val phoneInfo: HashMap<String, Any> =
            hashMapOf(
                "phone_number" to phoneNumber,
                "registration_time" to formatted,
                "is_primary" to isPrimary.toString()
            )

        val gson = Gson()
        val jsonPhoneInfo: String = gson.toJson(phoneInfo)
        val requestPost = Request.Builder()
            .url(GlobalConstants.MEDICAL_API_PHONE_REGISTRATION_URL)
            .addHeader("ct_key", GlobalConstants.MEDICAL_API_CT_KEY)
            .addHeader("Accept", "application/json")
            .post(jsonPhoneInfo.toRequestBody(contentType()))
            .build()

        val thread = Thread(Runnable {
            try {
                okHttpClient.newCall(requestPost).execute().use { response ->
                    response.isSuccessful
                    Log.i(TAG, "Registered phone with number $phoneNumber as infected.")
                }
            } catch (e: Exception) {
                println(e)
            }
        })
        thread.start()

        return false
    }

    private fun contentType(): MediaType {
        return "application/json; charset=utf-8".toMediaType()
    }

    companion object {
        private const val TAG = "ContactTracerService"
    }

}
