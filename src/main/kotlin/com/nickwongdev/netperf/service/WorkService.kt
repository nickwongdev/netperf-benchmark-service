package com.nickwongdev.netperf.service

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPOutputStream
import kotlin.random.Random

/**
 * A service that generates a lot of coroutines and wastes CPU and Memory resources
 */
class WorkService {

	/* Generate some random strings */
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
	 * @param iterMin Minimum times to run algorithm per work
	 * @param iterMax Maximum times to run algorithm per work
	 * @param delayMin Minimum millis to wait between iterations of work
	 * @param delayMax Maximum millis to wait between iterations of work
	 */
	suspend fun work(numCoroutines: Int, iterMin: Int, iterMax: Int, delayMin: Long, delayMax: Long): Int = coroutineScope {

		val count = AtomicInteger(0)

		// Launches children coroutines and joins so if they fail, they all abort
		val jobs = List(numCoroutines) {
			launch {
				val curIter = if (iterMax > iterMin) Random.nextInt(iterMin, iterMax) else iterMin
				val curDelay = if (delayMax > delayMin) Random.nextLong(delayMin, delayMax) else delayMin
				workSimulation(curDelay, curIter) { count.incrementAndGet() }
			}
		}

		jobs.forEach { it.join() }
		count.get()
	}

	/**
	 * This coroutine:
	 * - Waits a specified amount of non-blocking time
	 * - Runs a specified number of wasteCpuAndMemory coroutines concurrently
	 *
	 * @param wait How long to wait
	 * @param calcIter number of times to iterate
	 * @param action Action to perform when work is completed
	 */
	private suspend fun workSimulation(wait: Long, calcIter: Int, action: suspend () -> Unit) = coroutineScope {

		// Wait before starting work if delay was specified
		if (wait > 0) delay(wait)

		// Run work in sequence
		repeat(calcIter) {
			wasteCpuAndMemory()
			action()
		}
	}

	/**
	 * Simple blocking method for wasting CPU and Memory Resources
	 *
	 * @throws Exception When work was unable to be verified
	 */
	private fun wasteCpuAndMemory() {

		// Generate a block of data from the random strings
		var data = ""
		repeat(10) {
			data += uuidArray[Random.nextInt(0, 127)]
		}

		// Waste CPU and Memory by zipping the data block
		val dataString = data.toLowerCase()
		val zip1 = zipString(dataString)
		val zip2 = zipString(dataString)

		// Verify we aren't crazy
		if (zip1.hashCode() != zip2.hashCode()) throw Exception("Work could not be verified!")
	}

	private fun zipString(input: String): String {
		val bos = ByteArrayOutputStream()
		GZIPOutputStream(bos).bufferedWriter(UTF_8).use { it.write(input) }
		return String(bos.toByteArray())
	}
}
