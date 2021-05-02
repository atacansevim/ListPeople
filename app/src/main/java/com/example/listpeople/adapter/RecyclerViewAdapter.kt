package com.example.listpeople.adapter

import com.example.listpeople.Model.Person
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listpeople.R

class RecyclerViewAdapter(private var PersonList: Set<Person>) : RecyclerView.Adapter<RecyclerViewAdapter.RowHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {// Determines what adapter  will show?


        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout,parent,false)
        return RowHolder(view)

    }


    class RowHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        fun bind(person:Person)
        {

            itemView.findViewById<TextView>(R.id.name_textview)?.text = person.fullName
            itemView.findViewById<TextView>(R.id.id_textview)?.text = person.id.toString()
        }
    }


    override fun getItemCount(): Int {
        return PersonList.size
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {

        holder.bind(PersonList.elementAt(position))

    }

    fun emptyList(){
        PersonList = emptySet()
        notifyDataSetChanged()
    }


}