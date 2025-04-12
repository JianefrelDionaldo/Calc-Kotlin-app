package com.example.calculator

//import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class CalculatorViewModel: ViewModel() {

    private val _equationText = MutableLiveData("")
    val equationText: LiveData<String> = _equationText

    private val _resultText = MutableLiveData("0")
    val resultText: LiveData<String> = _resultText

    private var lastEvaluated = false

    fun onButtonClick(btn: String) {
//        Log.i("Clicked Button", btn)

        when (btn) {
            "AC" -> {
                _equationText.value = ""
                _resultText.value = "0"
                lastEvaluated = false
            }

            "C" -> {
                _equationText.value = _equationText.value?.dropLast(1)
                lastEvaluated = false
                evaluatePossible()
            }

            "=" -> {
                val result = _resultText.value ?: "0"
                _equationText.value = result
                lastEvaluated = true
            }

            else -> {
                if (lastEvaluated && btn in "123456789.") {
                    _equationText.value = btn
                    lastEvaluated = false
                } else {
                    _equationText.value += btn
                    lastEvaluated = false
                }
                evaluatePossible()
            }
        }

    }

    private fun evaluatePossible() {
        try {
            val equation = _equationText.value ?: return
            if(equation.isNotBlank() && equation.last().isDigit()) {
                _resultText.value = calculateResult(equation)
            }
        } catch (e: Exception) {
            _resultText.value = "Error"
        }
    }

    private fun calculateResult(equation: String) : String {
        val context : Context = Context.enter()
        context.optimizationLevel = -1
        val scriptable: Scriptable = context.initStandardObjects()
        return try {
            var result = context.evaluateString(scriptable, equation, "JavaScript", 1, null).toString()
            if(result.endsWith("0")) {
                result = result.removeSuffix(".0")
            }
            result
        } catch (e: Exception) {
            "Error"
        } finally {
            Context.exit()
        }
    }

}

