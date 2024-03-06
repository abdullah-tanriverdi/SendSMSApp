package com.tanriverdi.sendsms

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tanriverdi.sendsms.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contactAdapter: ArrayAdapter<String>

    // Permission codes are defined.
    private val PERMISSIONS_REQUEST_SEND_SMS = 100
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // ArrayAdapter and ListView are set.
        contactAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList())
        binding.contactsListView.adapter = contactAdapter


        // Details of the person selected from the ListView are shown.
        binding.contactsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedContactNumber = getContactNumber(contactAdapter.getItem(position).toString())
            showContactDetails(contactAdapter.getItem(position).toString())
            binding.editTextPhone.setText(selectedContactNumber)
        }


        // When the SMS send button is clicked, the action is taken.
        binding.btnSent.setOnClickListener {
            if (hasSendSmsPermission()) {
                sendSMS()
            } else {
                requestSendSmsPermission()
            }
        }

        // Show Contacts button click handler.
        binding.showContactsButton.setOnClickListener {
            if (hasReadContactsPermission()) {
                displayContacts()
            } else {
                requestReadContactsPermission()
            }
        }
    }


    // Function to check if SEND_SMS permission is granted.
    private fun hasSendSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }


    // Request SEND_SMS permission
    private fun requestSendSmsPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.SEND_SMS),
            PERMISSIONS_REQUEST_SEND_SMS
        )
    }


    // Function to check if READ_CONTACTS permission is granted.
    private fun hasReadContactsPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }


    // Request READ_CONTACTS permission.
    private fun requestReadContactsPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.READ_CONTACTS),
            PERMISSIONS_REQUEST_READ_CONTACTS
        )
    }

    // Display contacts in the ListView.
    private fun displayContacts() {
        val contacts = getContacts()
        contactAdapter.clear()
        contactAdapter.addAll(contacts)
    }


    // Function to retrieve contacts from the device.
    private fun getContacts(): List<String> {
        val contactsList = ArrayList<String>()
        val contentResolver: ContentResolver = contentResolver
        val uri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name: String? = it.getString(nameIndex)
                val number: String? = it.getString(numberIndex)

                // Null check before processing
                if (!name.isNullOrBlank() && !number.isNullOrBlank()) {
                    val contactInfo = "$name : $number"
                    contactsList.add(contactInfo)
                }
            }
        }

        return contactsList
    }


    // Extract contact number from the formatted string.
    private fun getContactNumber(contact: String): String {
        val parts = contact.split(" : ").toTypedArray()
        return parts[1]
    }


    // Display contact details in a TextView.
    private fun showContactDetails(contact: String) {
        binding.contactDetailsTextView.text = contact
        binding.contactDetailsTextView.visibility = View.VISIBLE
    }

    // Send SMS using SmsManager.
    private fun sendSMS() {
        val phone: String = binding.editTextPhone.text.toString()
        val message: String = binding.editTextSMS.text.toString()

        if (!phone.isEmpty() && !message.isEmpty()) {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phone, null, message, null, null)
            Toast.makeText(this@MainActivity, "SMS SENT SUCCESSFULLY", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this@MainActivity,
                "PLEASE ENTER PHONE AND MESSAGE",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Handle permission results.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission Denied. Cannot send SMS.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayContacts()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission Denied. Cannot display contacts.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }
}
