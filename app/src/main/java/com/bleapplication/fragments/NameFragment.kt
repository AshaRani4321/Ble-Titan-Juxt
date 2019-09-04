package com.bleapplication.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.NumberPicker

import com.bleapplication.R
import com.bleapplication.interfaces.ReplaceFragmentListener
import getListenerOrThrowException


class NameFragment : Fragment() {
    companion object {

        fun newInstance() = NameFragment()
    }

    private lateinit var replaceFragmentListener: ReplaceFragmentListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        replaceFragmentListener = getListenerOrThrowException(ReplaceFragmentListener::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_name, container, false)

        val btn_next = view.findViewById(R.id.name_btn_next) as ImageButton
        btn_next.setOnClickListener { view ->
            onButtonClicked()
        }
        return view
    }

    private fun onButtonClicked() {
        replaceFragmentListener.showMainFragment(GenderFragment.newInstance())
    }

}
