package TestClasses;

import Model.Products;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProductsTest {
  @Test
  public void constructionTest(){
    Products product = new Products(new SimpleIntegerProperty(1),  new SimpleStringProperty("TestProduct"),
            new SimpleDoubleProperty(49.99), new SimpleIntegerProperty(20), new SimpleStringProperty("Test Description"), new SimpleIntegerProperty(2));
    assertEquals(1,product.getProductID());
    assertEquals("TestProduct", product.getProductName());
    assertEquals(49.99,product.getProductPrice(),0);
    assertEquals(20,product.getProductStock());
    assertEquals("Test Description", product.getProductDescription());
    assertEquals(2, product.getProductUserID());
  }
}