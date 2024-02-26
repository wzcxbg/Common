package com.sliver.common.helper

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class ResultLauncher(private val resultCaller: ActivityResultCaller) {
    private var contract: ActivityResultContract<*, *>? = null
    private var callback: ActivityResultCallback<Any>? = null

    //当ResultLauncher处于全局变量时，fragment或activity重建时会无法接收到数据
    private val launcher = resultCaller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val result = contract?.parseResult(it.resultCode, it.data)
        if (result != null) {
            callback?.onActivityResult(result)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <I, O> launch(contract: ActivityResultContract<I, O>, input: I, callback: ActivityResultCallback<O>) {
        val intent: Intent
        if (resultCaller is ComponentActivity) {
            intent = contract.createIntent(resultCaller, input)
        } else if (resultCaller is Fragment) {
            intent = contract.createIntent(resultCaller.requireContext(), input)
        } else {
            throw IllegalArgumentException("resultCaller must be androidx.activity.ComponentActivity or androidx.fragment.app.Fragment")
        }
        launcher.launch(intent)
        this.contract = contract
        this.callback = callback as ActivityResultCallback<Any>
    }
}