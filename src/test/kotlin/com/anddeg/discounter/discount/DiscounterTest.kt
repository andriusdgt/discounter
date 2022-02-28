package com.anddeg.discounter.discount

import com.anddeg.discounter.transaction.Transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DiscounterTest {

    @Test
    fun `Returns nothing given no transactions`() {
        val discountedTransactions = Discounter().discount(listOf())

        assertEquals(emptyList<Transaction>(), discountedTransactions)
    }

}
