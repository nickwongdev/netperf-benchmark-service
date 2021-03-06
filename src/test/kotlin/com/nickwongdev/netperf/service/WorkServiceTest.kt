package com.nickwongdev.netperf.service

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

/**
 * There's some "runBlocking" stuff in here cause JUint doesn't understand Async code
 */
internal class WorkServiceTest {

	private val workService = WorkService()

	@Test
	fun work() {
		var amountOfWork = 0
		val timeTaken = measureTimeMillis {
			runBlocking {
				amountOfWork = workService.work(4, 10, 20, 1000, 2000)
			}
		}

		println("Amount of Work: $amountOfWork in $timeTaken millis")
		assert(timeTaken > 1000)
		assertTrue(amountOfWork >= 20)
	}
}