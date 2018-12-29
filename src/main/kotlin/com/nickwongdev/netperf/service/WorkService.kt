package com.nickwongdev.netperf.service

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPOutputStream
import kotlin.random.Random

class WorkService {

	private val uuidArray: Array<String> = Array(128) { UUID.randomUUID().toString() }

	/**
	 * Coroutine that launches a specified number of workSimulation coroutines.
	 *
	 * Once per specified number of coroutines, this method:
	 * - calculates a random amount of iterations of work (within specified bounds)
	 * - calculates a random amount of wait time (within specified bounds)
	 * - launches a coroutine with those parameters
	 *
	 * @param numCoroutines How many times to iterate on the work
	 * @param calcIterMin Minimum times to run algorithm per work
	 * @param calcIterMax Maximum times to run algorithm per work
	 * @param delayMin Minimum millis to wait between iterations of work
	 * @param delayMax Maximum millis to wait between iterations of work
	 */
	suspend fun work(numCoroutines: Int, calcIterMin: Int, calcIterMax: Int, delayMin: Long, delayMax: Long): Int = coroutineScope {

		val calcRange = calcIterMax - calcIterMin
		val delayRange = delayMax - delayMin
		val count = AtomicInteger(0)

		val jobs = List(numCoroutines) {
			launch {
				val curIter = if (calcRange > 1) Random.nextInt(calcIterMin, calcIterMax) else calcIterMin
				val curDelay = if (delayRange > 1) delayMin + Random.nextLong(delayMin, delayMax) else delayMin
				workSimulation(curDelay, curIter) { count.incrementAndGet() }
			}
		}
		jobs.forEach { it.join() }
		count.get()
	}

	/**
	 * This coroutine:
	 * - Waits a specified amount of non-blocking time
	 * - Runs a specified number of iterations of wasteCpuAndMemory
	 *
	 * @param wait How long to wait
	 * @param calcIter number of times to iterate
	 * @param action Action to perform when work is completed
	 */
	private suspend fun workSimulation(wait: Long, calcIter: Int, action: suspend () -> Unit) = coroutineScope {

		delay(wait)

		runBlocking {
			repeat(calcIter) {
				launch {
					if (!wasteCpuAndMemory()) throw RuntimeException("Work did not complete successfully!")
					action() // Tell "action" that work has been done
				}
			}
		}
	}

	/**
	 * Simple blocking method for wasting CPU and Memory Resources that never leaks exceptions.
	 */
	private fun wasteCpuAndMemory(): Boolean {

		try {
			var data = ""
			repeat(10) {
				data += uuidArray[Random.nextInt(0, 127)]
			}

			val dataString = data.toLowerCase()
			val zip1 = zipString(dataString)
			val zip2 = zipString(dataString)

			return zip1.hashCode() == zip2.hashCode()
		} catch (t: Throwable) {
		}
		return false
	}

	private fun zipString(input: String): String {
		val bos = ByteArrayOutputStream()
		GZIPOutputStream(bos).bufferedWriter(UTF_8).use { it.write(input) }
		return String(bos.toByteArray())
	}
}
