package com.anddeg.discounter.transaction

import com.anddeg.discounter.order.Order
import com.anddeg.discounter.order.Orderable
import com.anddeg.discounter.shipment.*
import java.math.BigInteger

class TransactionFactory {

    fun create(orders: List<Orderable>): List<Transaction> =
        orders.map { order ->
            val shipment =
                if (order is Order)
                    getMatchedShipment(order)
                else
                    Shipment(ShipmentSize.S, CarrierCode.LP, BigInteger.ZERO)
            Transaction(order, shipment.price)
        }

    private fun getMatchedShipment(o: Order) =
        ShipmentCatalogueSingleton.value.find { s ->
            o.shipmentSize == s.size && o.carrierCode == s.carrierCode
        }!!

}
