package com.anddeg.discounter.discount

import com.anddeg.discounter.transaction.Transaction

class Discounter {

    fun discount(transactions: List<Transaction>) =
        CappedDiscountDecorator(
            LargeShipmentDiscountDecorator(
                SmallShipmentDiscountDecorator()
            )
        ).discount(transactions)

}
