package com.example.meeting_organizer.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

class EmailSender{
    fun sendEmail(context: Context, recipients: Array<String>, subject: String, message: String) {
        val mIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        mIntent.putExtra(Intent.EXTRA_EMAIL, recipients)
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mIntent.putExtra(Intent.EXTRA_TEXT, message)

        try{
            context.startActivity(Intent.createChooser(mIntent, "Send email..."))
        }catch (e: Exception){
            Toast.makeText(context,e.message, Toast.LENGTH_LONG).show()
        }

    }

}