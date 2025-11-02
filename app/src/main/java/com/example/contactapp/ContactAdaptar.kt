package com.example.contactapp

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ContactAdaptar(private val contactList: MutableList<ContactModel>,  private val onDeleteClick: (position: Int) -> Unit) :
    RecyclerView.Adapter<ContactAdaptar.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.user_name)
        val contactEmail: TextView = itemView.findViewById(R.id.user_email)
        val contactPhone: TextView = itemView.findViewById(R.id.user_phone)
        val contactImage: ImageView = itemView.findViewById(R.id.user_image)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]

        holder.contactName.text = contact.name
        holder.contactEmail.text = contact.email
        holder.contactPhone.text = contact.phone

        val imgFile = File(contact.imagePath)
        if (imgFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            holder.contactImage.setImageBitmap(bitmap)
        } else {

            holder.contactImage.setImageResource(R.drawable.im)
        }


        holder.deleteButton.setOnClickListener {
            removeContact(position)
        }
    }

    override fun getItemCount(): Int = contactList.size

    fun addContact(contact: ContactModel) {
        contactList.add(contact)
        notifyItemInserted(contactList.size - 1)
    }

    fun removeContact(position: Int) {

        if (position in 0 until contactList.size) {
            contactList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, contactList.size)
        }
    }
}
