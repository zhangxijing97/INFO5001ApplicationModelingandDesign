/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.Supplier;

import java.util.ArrayList;
import java.util.Random;

import model.CustomerManagement.CustomerDirectory;
import model.ProductManagement.Product;
import model.ProductManagement.ProductSummary;
import model.ProductManagement.ProductsReport;

/**
 *
 * @author kal bugrara
 */
public class SupplierDirectory {

    ArrayList<Supplier> suppliers;

    public SupplierDirectory() {
        suppliers = new ArrayList();
    }

    public Supplier newSupplier(String n) {
        Supplier supplier = new Supplier(n);
        suppliers.add(supplier);
        return supplier;

    }

    public Supplier findSupplier(String id) {

        for (Supplier supplier : suppliers) {

            if (supplier.getName().equals(id))
                return supplier;
        }
        return null;
    }

    public ArrayList<Supplier> getSuplierList() {
        return suppliers;
    }

    public Supplier pickRandomSupplier() {
        if (suppliers.size() == 0)
            return null;
        Random r = new Random();
        int randomIndex = r.nextInt(suppliers.size());
        return suppliers.get(randomIndex);
    }

    public Supplier pickRandomSupplierFromRange() {
        if (suppliers.size() < 30) {
            // Handle the case where there are less than 30 suppliers
            return null;
        }

        Random r = new Random();
        int randomIndex = r.nextInt(30); // Picks a random index between 0 and 9 (inclusive)
        return suppliers.get(randomIndex);
    }

    public void printShortInfo() {
        System.out.println("Checking what's inside the supplier directory.");
        System.out.println("There are " + suppliers.size() + " suppliers.");
        for (Supplier s : suppliers) {
            s.printShortInfo();
        }
    }

    public SuppliersReport generateSuppliersReport() {
        SuppliersReport suppliersreport = new SuppliersReport();

        for (Supplier s : suppliers) {

            SupplierSummary ss = new SupplierSummary(s);
            suppliersreport.addSupplierSummary(ss);
        }
        return suppliersreport;
    }

}