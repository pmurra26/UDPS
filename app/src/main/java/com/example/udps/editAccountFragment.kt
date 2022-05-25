package com.example.udps

import android.R
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.udps.databinding.FragmentEditAccountBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var _binding: FragmentEditAccountBinding? = null
private val binding get() =_binding!!

/**
 * A simple [Fragment] subclass.
 * Use the [editAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class editAccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        val view = binding.root


        return view

    }

    override fun onResume() {
        super.onResume()
        val userList = arrayOf("kerry", "annie_mum", "twin_mum", "test_teacher01", "test_teacher02", "test_parent01", "test_parent02")


        for (i in userList.indices){
            val containerTop = LinearLayout(activity)
            val containerLabel = LinearLayout(activity)
            val containerButtons = LinearLayout(activity)
            val label = TextView(activity)
            label.setTextColor(resources.getColor(R.color.white))
            val editButton = Button(activity)
            val deleteButton = Button(activity)

            containerButtons.gravity = Gravity.RIGHT
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.weight = 55f
            val lp2 = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp2.weight = 45f
            containerLabel.layoutParams = lp
            containerButtons.layoutParams=lp2
            label.textSize = 22F

            editButton.text = "edit"
            deleteButton.text = "delete"
            label.text = userList[i]
            if(label.parent== null) containerLabel.addView(label)
            else Log.d("label","label has a parent, : "+label.parent)
            if(editButton.parent== null) containerButtons.addView(editButton)
            else Log.d("label","edit has a parent, : "+editButton.parent)
            if(deleteButton.parent== null) containerButtons.addView(deleteButton)
            else Log.d("label","delete has a parent, : "+deleteButton.parent)
            if(containerLabel.parent== null) containerTop.addView(containerLabel)
            if(containerButtons.parent== null) containerTop.addView(containerButtons)
            if(containerTop.parent== null) binding.llEditAccount.addView(containerTop)
            else Log.d("label","container has a parent, : "+containerTop.parent)
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment editAccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            editAccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}