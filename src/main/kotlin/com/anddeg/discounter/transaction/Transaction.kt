package com.anddeg.discounter.transaction

import com.anddeg.discounter.order.Orderable
import java.math.BigInteger

data class Transaction(
    val order: Orderable,
    val price: BigInteger = BigInteger.ZERO,
    val discount: BigInteger = BigInteger.ZERO
)
