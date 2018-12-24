package com.nickwongdev.netperf.service

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


internal class WorkServiceTest {

    private val workService = WorkService()

    @Test
    fun work() = runBlocking {
        val job = GlobalScope.launch {
            val numInvoke = workService.work(4, 10, 20, 100, 300)
            println("Work resulted in $numInvoke")
            delay(1000)
            Assertions.assertTrue(numInvoke > 0)
        }
        job.join()
    }
}