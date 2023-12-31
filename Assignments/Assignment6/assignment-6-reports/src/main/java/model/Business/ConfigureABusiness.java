/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.Business;

import java.util.List;
import java.util.Random;

import com.github.javafaker.Faker;

import model.Business.Business;
import model.CustomerManagement.CustomerDirectory;
import model.CustomerManagement.CustomerProfile;
import model.MarketingManagement.MarketingPersonDirectory;
import model.MarketingManagement.MarketingPersonProfile;
import model.OrderManagement.MasterOrderList;
import model.OrderManagement.Order;
import model.OrderManagement.OrderItem;
import model.Personnel.EmployeeDirectory;
import model.Personnel.EmployeeProfile;
import model.Personnel.Person;
import model.Personnel.PersonDirectory;
import model.ProductManagement.Product;
import model.ProductManagement.ProductCatalog;
import model.SalesManagement.SalesPersonDirectory;
import model.SalesManagement.SalesPersonProfile;
import model.Supplier.Supplier;
import model.Supplier.SupplierDirectory;
import model.UserAccountManagement.UserAccount;
import model.UserAccountManagement.UserAccountDirectory;

/**
 *
 * @author kal bugrara
 */
public class ConfigureABusiness {

  static int upperPriceLimit = 50;
  static int lowerPriceLimit = 10;
  static int range = 5;
  static int productMaxQuantity = 5;

  public static Business createABusinessAndLoadALotOfData(String name, int supplierCount, int productCount,
      int customerCount, int orderCount, int itemCount) {
    Business business = new Business(name);

    // Add Suppliers +
    loadSuppliers(business, supplierCount);

    // Add Products +
    // loadProducts(business, productCount);
    newLoadProducts(business, productCount);

    // Add Customers
    loadCustomers(business, customerCount);

    // Add Order
    // loadOrders(business, orderCount, itemCount);
    newLoadOrders(business, orderCount, itemCount);

    return business;
  }

  public static void loadSuppliers(Business b, int supplierCount) {
    Faker faker = new Faker();

    SupplierDirectory supplierDirectory = b.getSupplierDirectory();
    for (int index = 1; index <= supplierCount; index++) {
      supplierDirectory.newSupplier(faker.company().name());
    }
  }

  static void loadProducts(Business b, int productCount) {
    SupplierDirectory supplierDirectory = b.getSupplierDirectory();

    for (Supplier supplier : supplierDirectory.getSuplierList()) {

      int randomProductNumber = getRandom(1, productCount);
      ProductCatalog productCatalog = supplier.getProductCatalog();

      for (int index = 1; index <= randomProductNumber; index++) {

        String productName = "Product #" + index + " from " + supplier.getName();
        int randomFloor = getRandom(lowerPriceLimit, lowerPriceLimit + range);
        int randomCeiling = getRandom(upperPriceLimit - range, upperPriceLimit);
        int randomTarget = getRandom(randomFloor, randomCeiling);

        productCatalog.newProduct(productName, randomFloor, randomCeiling,
            randomTarget);
      }
    }
  }

  // Update loadProducts method
  // Pick first 30 Suppliers and add 50 Products to each
  static void newLoadProducts(Business b, int productCount) {
    SupplierDirectory supplierDirectory = b.getSupplierDirectory();
    int numberOfSuppliersToProcess = Math.min(30,
        supplierDirectory.getSuplierList().size());

    for (int i = 0; i < numberOfSuppliersToProcess; i++) {
      Supplier supplier = supplierDirectory.getSuplierList().get(i);
      int productNumber = 50;
      ProductCatalog productCatalog = supplier.getProductCatalog();

      for (int index = 1; index <= productNumber; index++) {
        Faker faker = new Faker();

        String productName = "Product " + faker.commerce().productName() + " from " +
            supplier.getName();
        int randomFloor = getRandom(lowerPriceLimit, lowerPriceLimit + range);
        int randomCeiling = getRandom(upperPriceLimit - range, upperPriceLimit);
        int randomTarget = getRandom(randomFloor, randomCeiling);
        productCatalog.newProduct(productName, randomFloor, randomCeiling,
            randomTarget);
      }
    }
  }

  static int getRandom(int lower, int upper) {
    Random r = new Random();

    // nextInt(n) will return a number from zero to 'n'. Therefore e.g. if I want
    // numbers from 10 to 15
    // I will have result = 10 + nextInt(5)
    int randomInt = lower + r.nextInt(upper - lower);
    return randomInt;
  }

  static void loadCustomers(Business b, int customerCount) {
    CustomerDirectory customerDirectory = b.getCustomerDirectory();
    PersonDirectory personDirectory = b.getPersonDirectory();

    Faker faker = new Faker();

    for (int index = 1; index <= customerCount; index++) {
      Person newPerson = personDirectory.newPerson(faker.name().fullName());
      customerDirectory.newCustomerProfile(newPerson);
    }
  }

  static void loadOrders(Business b, int orderCount, int itemCount) {

    // reach out to masterOrderList
    MasterOrderList mol = b.getMasterOrderList();

    // pick a random customer (reach to customer directory)
    CustomerDirectory cd = b.getCustomerDirectory();
    SupplierDirectory sd = b.getSupplierDirectory();

    for (int index = 0; index < orderCount; index++) {

      CustomerProfile randomCustomer = cd.pickRandomCustomer();
      if (randomCustomer == null) {
        System.out.println("Cannot generate orders. No customers in the customer directory.");
        return;
      }

      // create an order for that customer
      Order randomOrder = mol.newOrder(randomCustomer);

      // add order items
      // -- pick a supplier first (randomly)
      // -- pick a product (randomly)
      // -- actual price, quantity

      int randomItemCount = getRandom(1, itemCount);
      for (int itemIndex = 0; itemIndex < randomItemCount; itemIndex++) {

        Supplier randomSupplier = sd.pickRandomSupplier();
        if (randomSupplier == null) {
          System.out.println("Cannot generate orders. No supplier in the supplier directory.");
          return;
        }
        ProductCatalog pc = randomSupplier.getProductCatalog();
        Product randomProduct = pc.pickRandomProduct();
        if (randomProduct == null) {
          System.out.println("Cannot generate orders. No products in the product catalog.");
          return;
        }

        int randomPrice = getRandom(randomProduct.getFloorPrice(), randomProduct.getCeilingPrice());
        int randomQuantity = getRandom(1, productMaxQuantity);

        OrderItem oi = randomOrder.newOrderItem(randomProduct, randomPrice, randomQuantity, randomCustomer);
      }
    }
    // Make sure order items are connected to the order

  }

  static void newLoadOrders(Business b, int maxOrders, int maxItems) {
    MasterOrderList mol = b.getMasterOrderList();
    CustomerDirectory cd = b.getCustomerDirectory();
    SupplierDirectory sd = b.getSupplierDirectory();
    Random rand = new Random();

    // List<CustomerProfile> customers = cd.getCustomers();

    // Get the list of customers
    List<CustomerProfile> customers = cd.getCustomers();

    // Process all customers
    int customersToProcess = customers.size();

    for (int index = 0; index < customersToProcess; index++) {
      CustomerProfile customer = customers.get(index);

      int numOrders = rand.nextInt(maxOrders) + 1; // Random number of orders (between 1 and maxOrders)
      for (int i = 0; i < numOrders; i++) {
        Order randomOrder = mol.newOrder(customer);
        int numItems = rand.nextInt(maxItems) + 1; // Random number of items (between 1 and maxItems)
        for (int j = 0; j < numItems; j++) {
          Supplier randomSupplier = sd.pickRandomSupplierFromRange();
          if (randomSupplier == null) {
            System.out.println("Cannot generate orders. No supplier in the supplier directory.");
            return;
          }
          ProductCatalog pc = randomSupplier.getProductCatalog();
          Product randomProduct = pc.pickRandomProduct();
          if (randomProduct == null) {
            System.out.println("Cannot generate orders. No products in the product catalog.");
            return;
          }

          int randomPrice = getRandom(randomProduct.getFloorPrice(), randomProduct.getCeilingPrice());
          int randomQuantity = getRandom(1, productMaxQuantity);

          OrderItem oi = randomOrder.newOrderItem(randomProduct, randomPrice, randomQuantity, customer);
        }
      }
    }
    // Make sure order items are connected to the order
  }

}
