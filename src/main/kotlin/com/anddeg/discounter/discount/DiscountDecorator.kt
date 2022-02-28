package com.anddeg.discounter.discount

import com.anddeg.discounter.transaction.Transaction

abstract class DiscountDecorator {

    abstract fun discount(transactions: List<Transaction>): List<Transaction>

}
