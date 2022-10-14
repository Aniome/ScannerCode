package com.example.scannercode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class CheckQR : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_qr)
        sendRequest()
    }
    companion object{
        const val query = "request"
    }
    private fun sendRequest(){

        var request = intent.getStringExtra(query)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val txtView = findViewById<TextView>(R.id.textView)
        val image = findViewById<ImageView>(R.id.imageView)

        progressBar.visibility = View.VISIBLE

        request = request?.replace("{", "")
        request = request?.replace("}", "")
        request = request?.replace("\"", "")

        

        var listArg = request?.split(",")

        //val url = "http://192.168.0.29:5000/checkqr" // notebook
        //val url = "http://192.168.0.77:5000/checkqr" // desktop
        val url = "http://192.168.190.87:5000/checkqr" // desktop

        val itemsObject = JSONObject()

        val numbers: Array<Int> = arrayOf(1, 3, 4)
        val doubleNumbers: Array<Int> = arrayOf(2)
        val str: Array<Int> = arrayOf(5, 6, 7, 8)
        if (listArg != null) {
            var ind: Int = 1
            for (i in listArg){
                var t = i.split(":").toMutableList()
                t[0] = t[0].replaceFirst(" ", "")
                t[1] = t[1].replaceFirst(" ", "")
                if (ind in numbers)
                    itemsObject.put(t[0], t[1].toInt())
                if (ind in doubleNumbers)
                    itemsObject.put(t[0], t[1].toDouble())
                if (ind in str)
                    itemsObject.put(t[0], t[1])
                ind += 1
            }
        }

        val jsonRequest = object : JsonObjectRequest(
            Request.Method.POST, url, itemsObject,
            Response.Listener { response ->
                try {
                    progressBar.visibility = View.INVISIBLE
                    if (response.toString() == "{\"chain\":true}") {
                        image.setImageResource(R.drawable.ic_check_sign)
                        txtView.text = "Товар оригинальный. Блок находится в блокчейне"
                    }
                    else if (response.toString() == "{\"chain\":false}"){
                        image.setImageResource(R.drawable.ic_sign_error)
                        txtView.text = "Товар не оригинальный. Блок не находится в блокчейне"
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    txtView.text = e.toString()
                }
            },
            Response.ErrorListener { error ->
                txtView.text = error.toString()
            })
        {}
        /*
        @Throws(AuthFailureError::class)
        override fun getBodyContentType(): String {
            return "application/json"
        }
        override fun getHeaders(): Map<String, String> {
            val apiHeader = HashMap<String, String>()
            apiHeader["Authorization"] = "Bearer"
            return apiHeader
        }
        */

        val queue = Volley.newRequestQueue(this@CheckQR)
        queue.add(jsonRequest)

        // Volley request policy, only one time request to avoid duplicate transaction
        jsonRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )


    }
}