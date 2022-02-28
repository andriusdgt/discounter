package com.anddeg.discounter.discount

import com.anddeg.discounter.order.Order
import com.anddeg.discounter.shipment.Shipment
import com.anddeg.discounter.shipment.ShipmentCatalogueSingleton
import com.anddeg.discounter.shipment.ShipmentSize
import com.anddeg.discounter.transaction.Transaction
import java.math.BigInteger
import java.util.Comparator.comparing

class SmallShipmentDiscountDecorator: DiscountDecorator() {

    private val cheapestProviderPrice =
        ShipmentCatalogueSingleton.value.stream().min(comparing(Shipment::price)).get().price

    override fun discount(transactions: List<Transaction>): List<Transaction> =
        transactions
            .map { t ->
                if (t.order is Order && isSmall(t.order.shipmentSize) && !isCheapestOption(t.price))
                    Transaction(t.order, t.price, t.price - cheapestProviderPrice)
                else
                    t
            }

    private fun isSmall(size: ShipmentSize) = size == ShipmentSize.S

    private fun isCheapestOption(price: BigInteger) = price <= cheapestProviderPrice

}
