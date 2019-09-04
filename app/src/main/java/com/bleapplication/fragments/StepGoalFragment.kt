package com.bleapplication.fragments

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
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
import getListenerOrThrowException

class StepGoalFragment : Fragment() {
    lateinit var goal_picker: NumberPicker
    internal lateinit var profileActivityContext: Context
    internal lateinit var steps: Array<String?>
    internal var MINIMUM_STEPS = 1000
    internal var MAXIMUM_STEPS = 14000
    private var initialSteps = ""

    companion object {
        fun newInstance() = StepGoalFragment()
    }

    private lateinit var replaceFragmentListener: ReplaceFragmentListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        profileActivityContext = context!!
        replaceFragmentListener = getListenerOrThrowException(ReplaceFragmentListener::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_step_goal, container, false)

        goal_picker = view.findViewById(R.id.goal_step_picker) as NumberPicker
        steps = getTheStepslist()

        // Set divider color
        goal_picker.setDividerColor(ContextCompat.getColor(profileActivityContext,android.R.color.darker_gray))
        goal_picker.setDividerColorResource(android.R.color.darker_gray)
        goal_picker.setDividerDistance(35)

        // Set formatter
        goal_picker.setFormatter(R.string.number_picker_formatter)

        // Set selected text color
        goal_picker.setSelectedTextColor(ContextCompat.getColor(profileActivityContext,android.R.color.black))
        goal_picker.setSelectedTextColorResource(R.color.colorAccent)
        // Set selected text size
        goal_picker.setSelectedTextSize(resources.getDimension(R.dimen.selected_text_size))
        goal_picker.setSelectedTextSize(R.dimen.selected_text_size)

        // Set text color
        goal_picker.setTextColor(ContextCompat.getColor(profileActivityContext, android.R.color.darker_gray))
        goal_picker.setTextColorResource(android.R.color.darker_gray)

        // Set text size
        goal_picker.setTextSize(resources.getDimension(R.dimen.text_size))
        goal_picker.setTextSize(R.dimen.text_size)


        // Set value
        goal_picker.setMaxValue(steps.size)
        goal_picker.setMinValue(1)
        goal_picker.setDisplayedValues(steps)
        goal_picker.setWrapSelectorWheel(false)

        // Set fading edge enabled
        goal_picker.setFadingEdgeEnabled(true)
        goal_picker.setValue(3)

        goal_picker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker, oldVal: Int, newVal: Int) {
                initialSteps = newVal.toString()
            }
        })

        val btn_next = view.findViewById(R.id.goal_btn_next) as ImageButton
        btn_next.setOnClickListener { view ->
            onButtonClicked()
        }
        return view
    }

    fun getTheStepslist(): Array<String?> {
        val lenghofarray = (MAXIMUM_STEPS - MINIMUM_STEPS) / 1000
        var theHeightlistinCm: Array<String?> = arrayOfNulls(lenghofarray)

        var i = MINIMUM_STEPS
        var counter = 0
        do {
            theHeightlistinCm[counter] = i.toString()
            i = i + 1000
            counter++
        } while (i < MAXIMUM_STEPS)

        return theHeightlistinCm
    }

    private fun onButtonClicked() {
        replaceFragmentListener.showMainFragment(NameFragment.newInstance())
    }
}
