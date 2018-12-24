package com.nickwongdev.netperf.service

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.zip.GZIPOutputStream
import kotlin.random.Random

class WorkService {

    private val uuidArray: Array<String> = Array(128) { UUID.randomUUID().toString() }

    /**
     * Async coroutine that does a set of valueless work
     *
     * @param iter How many times to iterate on the work
     * @param calcIterMin Minimum times to run algorithm per work
     * @param calcIterMax Maximum times to run algorithm per work
     * @param waitMin Minimum usec to wait between iterations of work
     * @param waitMax Maximum usec to wait between iterations of work
     */
    suspend fun work(iter: Int, calcIterMin: Int, calcIterMax: Int, waitMin: Long, waitMax: Long): Int = coroutineScope {

        val usecDiff = waitMax - waitMin
        val calcIterDiff = calcIterMax - calcIterMin
        val asyncValList: MutableList<Deferred<Int>> = mutableListOf()
        repeat(iter) {
            val curIter = if (calcIterDiff > 1) Random.nextInt(1, calcIterDiff) else calcIterMin
            val curWait = if (usecDiff > 1) waitMin + Random.nextLong(0, usecDiff) else waitMin
            asyncValList.add(async { workCoroutine(it, curWait, curIter) })
        }

        var count = 0
        asyncValList.forEach { item -> count += item.await() }
        count
    }

    /**
     * Just some basic String manipulation and byte calculation work to waste CPU and Memory resources
     */
    private suspend fun workCoroutine(coroutineId: Int, wait: Long, calcIter: Int): Int = coroutineScope {

        delay(wait)

        val asyncValList: MutableList<Deferred<Int>> = mutableListOf()
        repeat(calcIter) {
            asyncValList.add(async { calcCoroutine(coroutineId, it) })
        }

        var count = 0
        asyncValList.forEach { item -> count += item.await() }
        count
    }

    private suspend fun calcCoroutine(coroutineId: Int, calcId: Int): Int = coroutineScope {

        // println("Starting work for coroutine $coroutineId and calc $calcId")

        var data = ""
        repeat(10) {
            data += uuidArray[Random.nextInt(0, 127)]
        }

        val dataString = data.toLowerCase()
        val zip1 = zipString(dataString)
        val zip2 = zipString(dataString)

        if (zip1.hashCode() != zip2.hashCode()) throw RuntimeException("Zip HashCode does not match!")
        1
    }

    private fun zipString(input: String): String {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).bufferedWriter(UTF_8).use { it.write(input) }
        return String(bos.toByteArray())
    }
}