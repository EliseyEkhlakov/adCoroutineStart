package ru.adorly.adcoroutinestart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.adorly.adcoroutinestart.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonLoad.setOnClickListener {
//            lifecycleScope.launch {
//                loadData()
//            }
            loadWithoutCoroutine()
        }
    }

    private suspend fun loadData() {
        Log.d("MainActivity", "Load start: $this")
        binding.progress.isVisible = true
        binding.buttonLoad.isEnabled = false
        val city = loadCity()

        binding.tvLocation.text = city
        val temp = loadTemperature(city)

        binding.tvTemperature.text = temp.toString()
        binding.progress.isVisible = false
        binding.buttonLoad.isEnabled = true
        Log.d("MainActivity", "Load finish: $this")
    }

    private fun loadWithoutCoroutine(step: Int = 0, obj: Any? = null) {
        when (step) {
            0 -> {
                Log.d("MainActivity", "Load start: $this")
                binding.progress.isVisible = true
                binding.buttonLoad.isEnabled = false
                val city = loadCityWithoutCoroutine {
                    loadWithoutCoroutine(1, it)
                }
            }
            1 -> {
                val city = obj as String
                binding.tvLocation.text = city
                loadTemperatureWithoutCoroutine(city) {
                    loadWithoutCoroutine(2, it)
                }
            }
            2 -> {
                val temp = obj as Int
                binding.tvTemperature.text = temp.toString()
                binding.progress.isVisible = false
                binding.buttonLoad.isEnabled = true
                Log.d("MainActivity", "Load finish: $this")
            }
        }
    }

    private fun loadCityWithoutCoroutine(callback: (String) -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            callback.invoke("Moscow")
        }, 5000)
    }

    private suspend fun loadCity(): String {
        delay(5000)
        return "Moscow"
    }

    private fun loadTemperatureWithoutCoroutine(city: String, callback: (Int) -> Unit) {
        Toast.makeText(
            this,
            getString(R.string.loading_temperature_toast, city),
            Toast.LENGTH_SHORT
        ).show()
        Handler(Looper.getMainLooper()).postDelayed({
            callback.invoke(17)
        }, 5000)
    }

    private suspend fun loadTemperature(city: String): Int {
        Toast.makeText(
            this,
            getString(R.string.loading_temperature_toast, city),
            Toast.LENGTH_SHORT
        ).show()
        delay(5000)
        return 17
    }
}
