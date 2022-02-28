# discounter

Discounter is a CLI tool that helps calculate applicable discounts for the clothes orders made by customers.

## Prerequisites

* JDK 11 installed on machine

### Running

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

To run all unit and integration tests, use this command:

```
./gradlew test
```

### Input and output

Refer to `input.txt` for an applicable example. An order row consists of:
1. date
2. shipment size
3. shipment provider code

Output produces same rows each additionally containing:
1. the discounted price
2. discount amount for input orders

The contents and pricing of catalogue items can be adjusted in this file (price in euro cents):
`src/main/resources/shipmentCatalogue.txt`

### Discount calculation

The discounter follows the rules which can be easily extended from code. List of rules applied for shipments:
1. All S shipments should always match the lowest S package price among the providers
2. The third L shipment via LP should be free, but only once a calendar month
3. Accumulated discounts cannot exceed 10€ in a calendar month. If there are not enough funds to fully cover a discount 
this calendar month, it should be covered partially. Limit can be adjusted in `src/main/resources/monthlyDiscountWallet.txt`
file

## Code of conduct

* Made with Test Driven Development
* Is following SOLID design principles
* Built with GoF patterns in mind
* As software is built with Kotlin programming language it follows extensive style guide described in
  https://kotlinlang.org/docs/coding-conventions.html, when contributing we recommend to import style rules to your IDE
  until this project gets a configured linter which runs on every build or such is configured in GitHub repository   
