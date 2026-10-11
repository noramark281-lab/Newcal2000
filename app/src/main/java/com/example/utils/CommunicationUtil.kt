package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object CommunicationUtil {

    fun dialPhoneNumber(context: Context, phoneNumber: String) {
        val cleanNumber = phoneNumber.trim().replace(" ", "").replace("-", "")
        if (cleanNumber.isEmpty()) {
            Toast.makeText(context, "رقم الهاتف غير متوفر", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق الاتصال: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendWhatsAppMessage(context: Context, phoneNumber: String, message: String) {
        val rawNumber = phoneNumber.trim().replace(" ", "").replace("-", "").replace("+", "")
        if (rawNumber.isEmpty()) {
            Toast.makeText(context, "رقم الهاتف غير متوفر", Toast.LENGTH_SHORT).show()
            return
        }
        // If Yemen local 9 digits starting with 7, prepend 967 country code if not present
        val internationalNumber = when {
            rawNumber.startsWith("967") -> rawNumber
            rawNumber.length == 9 && rawNumber.startsWith("7") -> "967$rawNumber"
            else -> rawNumber
        }

        try {
            val encodedMsg = Uri.encode(message)
            val url = "https://api.whatsapp.com/send?phone=$internationalNumber&text=$encodedMsg"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to generic share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(shareIntent, "إرسال عبر"))
        }
    }

    fun sendEmail(context: Context, email: String, subject: String, body: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                if (email.isNotBlank()) {
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                }
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(intent, "إرسال بريد إلكتروني"))
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق البريد: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareFile(context: Context, fileUri: Uri, mimeType: String, title: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر مشاركة الملف: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
