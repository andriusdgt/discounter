# discounter

Discounter is a CLI tool that helps calculate applicable discounts for the clothes orders made by our customers.

## Prerequisites

* JDK 11 installed on machine

## Running

Run this command to get started, it will use `input.txt` file by default, located in the same directory: 

```
./gradlew run
```

It's possible to define a different order file:

```
./gradlew run --args='input.txt'
```

You can also use absolute paths:

```
./gradlew run --args='/home/user/Desktop/input.txt'
```

All input orders must follow a common format, do refer to `input.txt` for an applicable example. A row consists of date,
shipment size and shipment provider code. The program output produces the discounted price and discount amount for input
orders. The contents and pricing of catalogue items can be adjusted in this file:
`src/main/resources/shipmentCatalogue.txt`

The shop discounter follows the rules which can be easily extended from code. Moreover, all discounts are capped
against the monthly limit a shop can reimburse partially or fully for shipment costs. This limit can be adjusted in 
this file, note that value represents Euro cents:
`src/main/resources/monthlyDiscountWallet.txt`

## Testing

To run all unit and integration tests, use this command: 

```
./gradlew test
```

## Code of conduct

* Made with Test Driven Development
* Is following SOLID design principles
* Built with GoF patterns in mind
* As software is built with Kotlin programming language it follows extensive style guide described in
  https://kotlinlang.org/docs/coding-conventions.html, when contributing we recommend to import style rules to your IDE
  until this project gets a configured linter which runs on every build or such is configured in GitHub repository   
