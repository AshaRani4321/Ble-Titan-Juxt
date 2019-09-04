package com.bleapplication.fragments


import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
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

class WeightFragment : Fragment() {

    internal lateinit var profileActivityContext: Context
    lateinit var weight_kg_lb__picker: NumberPicker
    lateinit var kg_lbs_picker: NumberPicker
    internal var MINIMUM_WEIGHT_IN_KG = 32
    internal var MAXIMUM_WEIGHT_IN_KG = 190
    internal lateinit var weight_in_kg: Array<String>
    internal lateinit var weight_in_lbs: Array<String>
    internal lateinit var heightInInchFullArray: Array<String>
    val displayedValues = arrayOf("Kg", "Lbs")

    companion object {

        fun newInstance() = WeightFragment()
    }

    private lateinit var replaceFragmentListener: ReplaceFragmentListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        profileActivityContext = context!!
        replaceFragmentListener = getListenerOrThrowException(ReplaceFragmentListener::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_weight, container, false)

        weight_in_kg = getTheWeightlistinKg()
        weight_in_lbs = converCmToPounds(weight_in_kg)

        weight_kg_lb__picker = view.findViewById(R.id.weight_kg_lbs_picker) as NumberPicker
        kg_lbs_picker = view.findViewById(R.id.kg_lbs_picker) as NumberPicker

        // Set divider color
        weight_kg_lb__picker.setDividerColor(
            ContextCompat.getColor(
                profileActivityContext,
                android.R.color.darker_gray
            )
        )
        weight_kg_lb__picker.setDividerColorResource(android.R.color.darker_gray)
        weight_kg_lb__picker.setDividerDistance(35)

        kg_lbs_picker.setDividerColor(ContextCompat.getColor(profileActivityContext, android.R.color.darker_gray))
        kg_lbs_picker.setDividerColorResource(android.R.color.darker_gray)
        kg_lbs_picker.setDividerDistance(35)

        // Set formatter
        weight_kg_lb__picker.setFormatter(R.string.number_picker_formatter)
        kg_lbs_picker.setFormatter(R.string.number_picker_formatter)


        // Set selected text color
        weight_kg_lb__picker.setSelectedTextColor(ContextCompat.getColor(profileActivityContext, android.R.color.black))
        weight_kg_lb__picker.setSelectedTextColorResource(R.color.colorAccent)

        kg_lbs_picker.setSelectedTextColor(ContextCompat.getColor(profileActivityContext, android.R.color.black))
        kg_lbs_picker.setSelectedTextColorResource(R.color.colorAccent)

        // Set selected text size
        weight_kg_lb__picker.setSelectedTextSize(resources.getDimension(R.dimen.selected_text_size))
        weight_kg_lb__picker.setSelectedTextSize(R.dimen.selected_text_size)

        kg_lbs_picker.setSelectedTextSize(resources.getDimension(R.dimen.ft_cm_text_size))
        kg_lbs_picker.setSelectedTextSize(R.dimen.ft_cm_text_size)

        // Set text color
        weight_kg_lb__picker.setTextColor(ContextCompat.getColor(profileActivityContext, R.color.colorAccent))
        weight_kg_lb__picker.setTextColorResource(R.color.colorAccent)

        kg_lbs_picker.setTextColor(ContextCompat.getColor(profileActivityContext, R.color.colorAccent))
        kg_lbs_picker.setTextColorResource(R.color.colorAccent)

        // Set text size
        weight_kg_lb__picker.setTextSize(resources.getDimension(R.dimen.text_size))
        weight_kg_lb__picker.setTextSize(R.dimen.text_size)

        kg_lbs_picker.setTextSize(resources.getDimension(R.dimen.ft_cm_text_size))
        kg_lbs_picker.setTextSize(R.dimen.ft_cm_text_size)

        // Set value
        weight_kg_lb__picker.setMaxValue(weight_in_kg.size)
        weight_kg_lb__picker.setMinValue(1)
        weight_kg_lb__picker.setDisplayedValues(weight_in_kg)
        weight_kg_lb__picker.setWrapSelectorWheel(false)

        kg_lbs_picker.setMaxValue(displayedValues.size)
        kg_lbs_picker.setMinValue(1)
        kg_lbs_picker.setDisplayedValues(displayedValues)
        kg_lbs_picker.setWrapSelectorWheel(false)

        // Set fading edge enabled
        weight_kg_lb__picker.setFadingEdgeEnabled(false)
        weight_kg_lb__picker.setValue(3)

        kg_lbs_picker.setFadingEdgeEnabled(false)
        kg_lbs_picker.setValue(1)

        // OnValueChangeListener
        kg_lbs_picker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker, oldVal: Int, newVal: Int) {

                if (newVal == 1) {
                    ChangeTheState("Kg")
                } else {
                    ChangeTheState("Lbs")
                }
            }
        })

        return view
    }

    private fun ChangeTheState(s: String) {

        when (s) {
            "Kg" -> {
                weight_kg_lb__picker.setMaxValue(weight_in_kg.size)
                weight_kg_lb__picker.setDisplayedValues(weight_in_kg)
                weight_kg_lb__picker.setWrapSelectorWheel(false)
                weight_kg_lb__picker.setMinimumWidth(MicroMotorUtility.getInstance().convertDpsToPixels(context, 40))

            }
            "Lbs" -> {
                weight_kg_lb__picker.setMaxValue(weight_in_lbs.size)
                weight_kg_lb__picker.setDisplayedValues(weight_in_lbs)
                weight_kg_lb__picker.setMinimumWidth(MicroMotorUtility.getInstance().convertDpsToPixels(context, 60))
                weight_kg_lb__picker.setWrapSelectorWheel(false)
            }
        }
    }

    private fun converCmToPounds(weight_in_kg: Array<String>): Array<String> {
        val theHeightListInInches = Array<String>(weight_in_kg.size, { _ -> "" })
        var i = 0
        for (weight in weight_in_kg) {
            val calculatedVal =
                MicroMotorUtility.getInstance().round((java.lang.Float.parseFloat(weight) * 2.20462).toFloat(), 2)
                    .toString().plus("")
            theHeightListInInches[i] = calculatedVal
            i++
        }
        return theHeightListInInches
    }

    fun getTheWeightlistinKg(): Array<String> {
        val theHeightlistinCm = Array<String>(MAXIMUM_WEIGHT_IN_KG - MINIMUM_WEIGHT_IN_KG, { _ -> "" })
        var counter = 0
        for (i in MINIMUM_WEIGHT_IN_KG until MAXIMUM_WEIGHT_IN_KG) {
            theHeightlistinCm[counter] = (i).toString()
            counter++
        }
        return theHeightlistinCm
    }

    private fun onButtonClicked() {
        replaceFragmentListener.showMainFragment(BirthDayFragment.newInstance())
    }

    private fun setDividerColor(picker:NumberPicker, color:Int) {
        val pickerFields = NumberPicker::class.java.getDeclaredFields()
        for (pf in pickerFields)
        {
            if (pf.getName() == "mSelectionDivider")
            {
                pf.setAccessible(true)
                try
                {
                    val colorDrawable = ColorDrawable(color)
                    pf.set(picker, colorDrawable)
                }
                catch (e:IllegalArgumentException) {
                    e.printStackTrace()
                }
                catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }
                catch (e:IllegalAccessException) {
                    e.printStackTrace()
                }
                break
            }
        }
    }
}
