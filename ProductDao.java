package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import bean.Brands;
import bean.Product;

public class ProductDao {
    private static final String URL = "jdbc:mysql://192.168.18.245:3306/pythondb_test";
    private static final String USER = "pythondb_test";
    private static final String PASSWORD = "PYt3StDB75S";
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";

    public ProductDao() {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT p.*, b.brandName FROM product p JOIN brands b ON p.brand_id = b.brand_id";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
                Product product = new Product();
                product.setProduct_id(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setBrand_name(rs.getString("brandName"));  // Get the brand name
                product.setPrice(rs.getInt("price"));
                product.setColor(rs.getString("color"));
                product.setSpecification(rs.getString("specification"));
                product.setImage_url(rs.getString("image_url"));
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

    public List<String> getAllBrandNames() {
        List<String> brandNames = new ArrayList<>();
        String sql = "SELECT brandName FROM brands";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
                brandNames.add(rs.getString("brandName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brandNames;
    }
    
    public List<Brands> getAllBrands() {
        List<Brands> brands = new ArrayList<>();
        String sql = "SELECT brand_id, brandName FROM brands";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
                Brands brand = new Brands();
                brand.setId(rs.getInt("brand_id"));
                brand.setBrand_name(rs.getString("brandName"));
                brands.add(brand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brands;
    }


    // Method to retrieve a product by its ID
    public Product getProductById(int productId) {
        Product product = null;
        String sql = "SELECT p.product_id, p.name, p.brand_id, p.price, p.color, p.specification, p.image_url, b.brandName " +
                     "FROM product p " +
                     "JOIN brands b ON p.brand_id = b.brand_id " +
                     "WHERE p.product_id = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    product = new Product();
                    product.setProduct_id(rs.getInt("product_id"));
                    product.setName(rs.getString("name"));
                    product.setBrand_name(rs.getString("brandName"));  // Get the brand name
                    product.setPrice(rs.getInt("price"));
                    product.setColor(rs.getString("color"));
                    product.setSpecification(rs.getString("specification"));
                    product.setImage_url(rs.getString("image_url"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }


    public boolean updateProduct(Product product) {
        String sql = "UPDATE product SET name = ?, brand_id = ?, price = ?, color = ?, specification = ?, image_url = ? WHERE product_id = ?";
        
        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getBrand_id());
            stmt.setInt(3, product.getPrice());
            stmt.setString(4, product.getColor());
            stmt.setString(5, product.getSpecification());
            stmt.setString(6, product.getImage_url());
            stmt.setInt(7, product.getProduct_id());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Map<Integer, Product> getProductsByIds(Set<Integer> productIds) throws SQLException {
        Map<Integer, Product> products = new HashMap<>();
        if (productIds.isEmpty()) {
            return products;
        }

        String ids = productIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String query = "SELECT * FROM product WHERE product_id IN (" + ids + ")";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("name");
                int price = rs.getInt("price");
                Product product = new Product(id, name, price);
                products.put(id, product);
            }
        }

        return products;
    }
}
