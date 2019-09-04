package com.bleapplication.fragments

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bleapplication.R
import com.bleapplication.interfaces.ReplaceFragmentListener
import com.bleapplication.modules.picker.NumberPicker
import getListenerOrThrowException


class GenderFragment : Fragment() {
    companion object {

        fun newInstance() = GenderFragment()
    }

    val displayedValues = arrayOf("Male", "Female")
    lateinit var gender_picker: NumberPicker
    internal lateinit var profileActivityContext: Context

    private lateinit var replaceFragmentListener: ReplaceFragmentListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        profileActivityContext = context!!
        replaceFragmentListener = getListenerOrThrowException(ReplaceFragmentListener::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_gender, container, false)

        gender_picker = view.findViewById(R.id.gender_picker) as NumberPicker

        // Set divider color
        gender_picker.setDividerColor(ContextCompat.getColor(profileActivityContext,android.R.color.darker_gray))
        gender_picker.setDividerColorResource(android.R.color.darker_gray)
        gender_picker.setDividerDistance(35)

        // Set formatter
        gender_picker.setFormatter(R.string.number_picker_formatter)

        // Set selected text color
        gender_picker.setSelectedTextColor(ContextCompat.getColor(profileActivityContext,android.R.color.black))
        gender_picker.setSelectedTextColorResource(R.color.colorAccent)
        // Set selected text size
        gender_picker.setSelectedTextSize(resources.getDimension(R.dimen.selected_text_size))
        gender_picker.setSelectedTextSize(R.dimen.selected_text_size)

        // Set text color
        gender_picker.setTextColor(ContextCompat.getColor(profileActivityContext, android.R.color.darker_gray))
        gender_picker.setTextColorResource(android.R.color.darker_gray)

        // Set text size
        gender_picker.setTextSize(resources.getDimension(R.dimen.text_size))
        gender_picker.setTextSize(R.dimen.text_size)


        // Set value
        gender_picker.setMaxValue(displayedValues.size)
        gender_picker.setMinValue(1)
        gender_picker.setDisplayedValues(displayedValues)
        gender_picker.setWrapSelectorWheel(false)

        // Set fading edge enabled
        gender_picker.setFadingEdgeEnabled(true)
        //gender_picker.setValue(3)

        gender_picker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker, oldVal: Int, newVal: Int) {
               var initialSteps = newVal.toString()
            }
        })

        val btn_next = view.findViewById(R.id.gender_btn_next) as ImageButton
        btn_next.setOnClickListener { view ->
            onButtonClicked()
        }
        return view
    }

    private fun onButtonClicked() {
        replaceFragmentListener.showMainFragment(HeightFragment.newInstance())
    }
}
