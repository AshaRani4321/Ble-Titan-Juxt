package com.bleapplication.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat

import com.bleapplication.R
import com.bleapplication.interfaces.ReplaceFragmentListener
import com.bleapplication.modules.picker.NumberPicker
import com.bleapplication.modules.utils.MicroMotorUtility
import getListenerOrThrowException
import java.util.*
import kotlin.collections.HashSet

class HeightFragment : Fragment() {
    internal lateinit var profileActivityContext: Context
    lateinit var cm_picker: NumberPicker
    lateinit var ft_picker: NumberPicker
    lateinit var ft_cm_picker: NumberPicker
    internal var MINIMUM_HEIGHT = 32
    internal var MAXIMUM_HEIGHT = 190
    internal lateinit var heightInCm: Array<String>
    internal lateinit var heightInFt: Array<String>
    internal lateinit var heightInInchFullArray: Array<String>
    val displayedValues = arrayOf("Ft", "Cm")
    private var mPickerCordinate1: Int = -1
    private var mPickerCordinate2: Int = -1

    companion object {
        fun newInstance() = HeightFragment()
    }

    private lateinit var replaceFragmentListener: ReplaceFragmentListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        profileActivityContext = context!!
        replaceFragmentListener = getListenerOrThrowException(ReplaceFragmentListener::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_height, container, false)

        heightInCm = getTheHeightlistinCm()
        heightInFt = converCmToInches(heightInCm)
        heightInInchFullArray = converCmToInchesFullArray(heightInCm)


        cm_picker = view.findViewById(R.id.height_cm_picker) as NumberPicker
        ft_picker = view.findViewById(R.id.height_ft_picker) as NumberPicker
        ft_cm_picker = view.findViewById(R.id.ft_cm_picker) as NumberPicker

        cm_picker.visibility = View.GONE
        ft_picker.visibility = View.VISIBLE


        // Set divider color
        ft_picker.setDividerColor(ContextCompat.getColor(profileActivityContext, android.R.color.darker_gray))
        ft_picker.setDividerColorResource(android.R.color.darker_gray)
        ft_picker.setDividerDistance(35)

        cm_picker.setDividerColor(ContextCompat.getColor(profileActivityContext, android.R.color.darker_gray))
        cm_picker.setDividerColorResource(android.R.color.darker_gray)
        cm_picker.setDividerDistance(35)

        ft_cm_picker.setDividerColor(ContextCompat.getColor(profileActivityContext, android.R.color.darker_gray))
        ft_cm_picker.setDividerColorResource(android.R.color.darker_gray)
        ft_cm_picker.setDividerDistance(35)

        // Set formatter
        ft_picker.setFormatter(R.string.number_picker_formatter)
        cm_picker.setFormatter(R.string.number_picker_formatter)
        ft_cm_picker.setFormatter(R.string.number_picker_formatter)

        // Set selected text color
        ft_picker.setSelectedTextColor(ContextCompat.getColor(profileActivityContext, android.R.color.black))
        ft_picker.setSelectedTextColorResource(R.color.colorAccent)

        cm_picker.setSelectedTextColor(ContextCompat.getColor(profileActivityContext, android.R.color.black))
        cm_picker.setSelectedTextColorResource(R.color.colorAccent)

        ft_cm_picker.setSelectedTextColor(ContextCompat.getColor(profileActivityContext, android.R.color.black))
        ft_cm_picker.setSelectedTextColorResource(R.color.colorAccent)

        // Set selected text size
        ft_picker.setSelectedTextSize(resources.getDimension(R.dimen.selected_text_size))
        ft_picker.setSelectedTextSize(R.dimen.selected_text_size)

        cm_picker.setSelectedTextSize(resources.getDimension(R.dimen.selected_text_size))
        cm_picker.setSelectedTextSize(R.dimen.selected_text_size)

        ft_cm_picker.setSelectedTextSize(resources.getDimension(R.dimen.ft_cm_text_size))
        ft_cm_picker.setSelectedTextSize(R.dimen.ft_cm_text_size)

        // Set text color
        ft_picker.setTextColor(ContextCompat.getColor(profileActivityContext, R.color.colorAccent))
        ft_picker.setTextColorResource(R.color.colorAccent)

        cm_picker.setTextColor(ContextCompat.getColor(profileActivityContext, R.color.colorAccent))
        cm_picker.setTextColorResource(R.color.colorAccent)

        ft_cm_picker.setTextColor(ContextCompat.getColor(profileActivityContext, R.color.colorAccent))
        ft_cm_picker.setTextColorResource(R.color.colorAccent)

        // Set text size
        ft_picker.setTextSize(resources.getDimension(R.dimen.text_size))
        ft_picker.setTextSize(R.dimen.text_size)

        cm_picker.setTextSize(resources.getDimension(R.dimen.text_size))
        cm_picker.setTextSize(R.dimen.text_size)

        ft_cm_picker.setTextSize(resources.getDimension(R.dimen.ft_cm_text_size))
        ft_cm_picker.setTextSize(R.dimen.ft_cm_text_size)

        // Set value
        ft_picker.setMaxValue(heightInFt.size)
        ft_picker.setMinValue(1)
        ft_picker.setDisplayedValues(heightInFt)
        ft_picker.setWrapSelectorWheel(false)

        cm_picker.setMaxValue(heightInCm.size)
        cm_picker.setMinValue(1)
        cm_picker.setDisplayedValues(heightInCm)
        cm_picker.setWrapSelectorWheel(false)

        ft_cm_picker.setMaxValue(displayedValues.size)
        ft_cm_picker.setMinValue(1)
        ft_cm_picker.setDisplayedValues(displayedValues)
        ft_cm_picker.setWrapSelectorWheel(false)


        // Set fading edge enabled
        ft_picker.setFadingEdgeEnabled(false)
        ft_picker.setValue(3)

        cm_picker.setFadingEdgeEnabled(false)
        cm_picker.setValue(3)

        ft_cm_picker.setFadingEdgeEnabled(false)
        ft_cm_picker.setValue(1)


        // OnValueChangeListener
        cm_picker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker, oldVal: Int, newVal: Int) {
                mPickerCordinate1 = newVal
            }
        })

        ft_picker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker, oldVal: Int, newVal: Int) {
                mPickerCordinate2 = newVal
                mPickerCordinate1 = -1
            }
        })

        ft_cm_picker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker, oldVal: Int, newVal: Int) {
                mPickerCordinate2 = newVal
                if (mPickerCordinate2 == 1) {
                    ChangeTheState("Ft")
                } else {
                    ChangeTheState("Cm")

                }
            }
        })

        val btn_next = view.findViewById(R.id.height_btn_next) as ImageButton
        btn_next.setOnClickListener { view ->
            onButtonClicked()
        }
        return view
    }


    private fun ChangeTheState(s: String) {

        when (s) {
            "Cm" -> {
                if (mPickerCordinate1 == -1) {
                    val selectedHeight = heightInFt[ft_picker.getValue() - 1]
                    for (i in heightInInchFullArray.indices) {
                        if (selectedHeight == heightInInchFullArray[i]) {
                            cm_picker.setValue(i + 1)
                            break
                        }
                    }
                } else {
                    cm_picker.setTheValue(mPickerCordinate1)
                }

                ft_picker.setVisibility(View.GONE)
                cm_picker.setVisibility(View.VISIBLE)

            }
            "Ft" -> {
                val selectedPosition = cm_picker.getValue() - 1
                val selectedItem = heightInInchFullArray[selectedPosition]
                for (i in heightInFt.indices) {
                    if (heightInFt[i] == selectedItem) {
                        ft_picker.setValue(i + 1)
                        break
                    }
                }

                ft_picker.setVisibility(View.VISIBLE)
                cm_picker.setVisibility(View.GONE)

            }
        }
    }

    private fun converCmToInchesFullArray(heightincm: Array<String>): Array<String> {
        val theHeightListInInches = Array<String>(heightincm.size, { _ -> "" })
        var i = 0
        for (height in heightincm) {
            theHeightListInInches[i] = convertToInch(height)
            i++
        }
        return theHeightListInInches
    }

    private fun converCmToInches(heightincm: Array<String>): Array<String> {
        val theHeightlistinCm = Array<String>(heightincm.size, { _ -> "" })

        var i = 0
        for (height in heightincm) {
            theHeightlistinCm[i] = convertToInch(height)
            i++
        }
       // Arrays.asList(*theHeightlistinCm).distinct().toTypedArray()
        return   Arrays.asList(*theHeightlistinCm).distinct().toTypedArray()
    }

    private fun convertToInch(height: String): String {
        val calculatedVal =
            MicroMotorUtility.getInstance().round((java.lang.Float.parseFloat(height) * 0.0328084).toFloat(), 1)
                .toString().plus("")
        val beforeDecimalVal = calculatedVal.substring(0, calculatedVal.indexOf('.'))
        val afterDecimalVal = calculatedVal.substring(calculatedVal.indexOf('.') + 1)
        return beforeDecimalVal + "' " + afterDecimalVal + "\""
    }

    fun getTheHeightlistinCm(): Array<String> {
        val theHeightlistinCm = Array<String>(MAXIMUM_HEIGHT - MINIMUM_HEIGHT, { _ -> "" })
        var counter = 0
        for (i in MINIMUM_HEIGHT until MAXIMUM_HEIGHT) {
            theHeightlistinCm[counter] = i.toString()
            counter++
        }
        return theHeightlistinCm
    }

    private fun onButtonClicked() {
        replaceFragmentListener.showMainFragment(WeightFragment.newInstance())
    }

}
