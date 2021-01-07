package com.dangproject.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


private const val STATE_PENDING_OPERATION = "PendingOperation"
private const val STATE_OPERAND1 = "Operand1"
private const val STATE_OPERAND1_STORE = "Operand1_Stored"
private const val STATE_DONE_A_CALCULATION = "DoneACalculation"

class MainActivity : AppCompatActivity() {
    // Variable to hold the operands and type of calculation
    private var operand1: Double? = null
    private var doneACalculation: Boolean = false
    private var pendingOperation = "="
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listener = View.OnClickListener { v ->
            val b = v as Button     // attach this listener to buttons: ***
            newNumber.append(b.text)
        }
        button0.setOnClickListener(listener)
        button1.setOnClickListener(listener)
        button2.setOnClickListener(listener)
        button3.setOnClickListener(listener)
        button4.setOnClickListener(listener)
        button5.setOnClickListener(listener)
        button6.setOnClickListener(listener)
        button7.setOnClickListener(listener)
        button8.setOnClickListener(listener)
        button9.setOnClickListener(listener)
        buttonDot.setOnClickListener(listener)

        val opListener = View.OnClickListener { v ->
            val op = (v as Button).text.toString()
            try {
                val value = newNumber.text.toString().toDouble()
                performOperation(value, op)
            } catch (e: NumberFormatException) {
                newNumber.setText("")
            }
            pendingOperation = op
            if (op != "="){
                operation.text = pendingOperation
                doneACalculation = false
            } else {
                operation.text = ""
                doneACalculation = true
            }
        }
        buttonEqual.setOnClickListener(opListener)
        buttonDivide.setOnClickListener(opListener)
        buttonMultiply.setOnClickListener(opListener)
        buttonMinus.setOnClickListener(opListener)
        buttonPlus.setOnClickListener(opListener)

        buttonNeg.setOnClickListener  { _ ->
            val value = newNumber.text.toString()
            if (value.isEmpty()){
                newNumber.setText("-")
            } else {
                try {
                    var doubleValue = value.toDouble()
                    doubleValue *= -1
                    newNumber.setText(getStringFromRoundedDouble(doubleValue))
                } catch (e: NumberFormatException){
                    // newNumber was "-", ".', or empty
                    newNumber.setText("")
                }
            }
        }
        buttonClear.setOnClickListener  { _ ->
            val value = newNumber.text.toString()
            if (value.isEmpty()){
                operation.text = ""
                result.setText("")
                doneACalculation = false
                operand1 = null
                pendingOperation = "="
            } else {
                newNumber.setText("")

            }
        }


    }

    private fun performOperation(value: Double, operation: String) {
        if (operand1 == null || doneACalculation) {
            operand1 = value
        } else {
            if (pendingOperation == "=") {
                pendingOperation = operation
            }

            when (pendingOperation) {
                "=" -> operand1 = value
                "/" -> operand1 = if (value == 0.0) {
                    Double.NaN // handle attemp to divide by zero
                } else {
                    operand1!! / value
                }
                "*" -> operand1 = operand1!! * value
                "-" -> operand1 = operand1!! - value
                "+" -> operand1 = operand1!! + value
            }
        }
        // if user input an Integer, it should show an Integer
//        var checkInt : Int = operand1!!.toInt()
//        var checkZero: Double = operand1!! - checkInt
//        if (checkZero == 0.0){
//            result.setText(checkInt.toString())
//        } else {
//            result.setText(operand1.toString())
//        }
        result.setText(getStringFromRoundedDouble(operand1!!))
        newNumber.setText("")

    }
    private fun getStringFromRoundedDouble(tNumber : Double) : String {
        var checkInt : Int = tNumber.toInt()
        var checkZero: Double = tNumber - checkInt
        return if (checkZero == 0.0) {
            checkInt.toString()
        } else {
            tNumber.toString()
        }
    }

    fun makeString(list: List<String>,joiner: String = "") : String {

        if (list.isEmpty()) return ""
        return list.reduce { r, s -> r + joiner + s }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (operand1 != null){
            outState.putDouble(STATE_OPERAND1, operand1!!)
            outState.putBoolean(STATE_OPERAND1_STORE,true)
        }
        outState.putString(STATE_PENDING_OPERATION,pendingOperation)
        outState.putBoolean(STATE_DONE_A_CALCULATION, doneACalculation)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        operand1 = if (savedInstanceState.getBoolean(STATE_OPERAND1_STORE,false)){
            savedInstanceState.getDouble(STATE_OPERAND1)
        } else {
            null
        }

        pendingOperation = savedInstanceState.getString(STATE_PENDING_OPERATION).toString()
        operation.text = pendingOperation
        doneACalculation = savedInstanceState.getBoolean(STATE_DONE_A_CALCULATION, false)
    }
}