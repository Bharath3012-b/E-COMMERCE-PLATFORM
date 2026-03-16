import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

class Product {
    int id;
    String name;
    double price;

    Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return id + " - " + name + " (₹" + price + ")";
    }
}

class ECommercePlatform {
    List<Product> products = new ArrayList<>();
    List<Product> cart = new ArrayList<>();

    void addProduct(int id, String name, double price) {
        products.add(new Product(id, name, price));
    }

    List<Product> getProducts() {
        return products;
    }

    void addToCart(int productId) throws Exception {
        Product product = products.stream()
            .filter(p -> p.id == productId)
            .findFirst()
            .orElseThrow(() -> new Exception("Product with ID " + productId + " not found."));
        cart.add(product);
    }

    List<Product> getCart() {
        return cart;
    }

    double calculateTotal() {
        return cart.stream().mapToDouble(p -> p.price).sum();
    }

    String checkDiscount() {
        boolean hasLaptop = cart.stream().anyMatch(p -> p.name.equalsIgnoreCase("Laptop"));
        boolean hasSmallProduct = cart.stream().anyMatch(p -> p.name.equalsIgnoreCase("Headphones") || p.name.equalsIgnoreCase("Mouse"));

        if (hasLaptop && hasSmallProduct) {
            return "You get a 20% discount for buying a Laptop with one small product.";
        }
        return null;
    }

    double applyDiscount(double total) {
        boolean hasLaptop = cart.stream().anyMatch(p -> p.name.equalsIgnoreCase("Laptop"));
        boolean hasSmallProduct = cart.stream().anyMatch(p -> p.name.equalsIgnoreCase("Headphones") || p.name.equalsIgnoreCase("Mouse"));

        if (hasLaptop && hasSmallProduct) {
            return total * 0.8;
        }
        return total;
    }

    void clearCart() {
        cart.clear();
    }
}

public class Main {
    public static void main(String[] args) {
        ECommercePlatform platform = new ECommercePlatform();

        // Adding products
        platform.addProduct(1, "Laptop", 65999.0);
        platform.addProduct(2, "Smartphone", 39999.0);
        platform.addProduct(3, "Headphones", 2499.0);
        platform.addProduct(4, "Tablet", 24999.0);
        platform.addProduct(5, "Smartwatch", 15999.0);
        platform.addProduct(6, "Gaming Console", 32999.0);
        platform.addProduct(7, "Bluetooth Speaker", 3999.0);
        platform.addProduct(8, "Monitor", 12499.0);
        platform.addProduct(9, "Keyboard", 3299.0);
        platform.addProduct(10, "Mouse", 1599.0);

        SwingUtilities.invokeLater(() -> new Main().initializeUI(platform));
    }

    private void initializeUI(ECommercePlatform platform) {
        JFrame frame = new JFrame("E-Commerce Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        // Product list panel
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBorder(BorderFactory.createTitledBorder("Products"));

        DefaultListModel<String> productListModel = new DefaultListModel<>();
        JList<String> productList = new JList<>(productListModel);
        platform.getProducts().forEach(p -> productListModel.addElement(p.toString()));

        JScrollPane productScrollPane = new JScrollPane(productList);
        productPanel.add(productScrollPane, BorderLayout.CENTER);

        JButton addToCartButton = new JButton("Add to Cart");
        productPanel.add(addToCartButton, BorderLayout.SOUTH);

        // Cart panel
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Cart"));

        DefaultListModel<String> cartListModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartListModel);
        JScrollPane cartScrollPane = new JScrollPane(cartList);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);

        JButton checkoutButton = new JButton("Checkout");
        cartPanel.add(checkoutButton, BorderLayout.SOUTH);

        // Add panels to frame
        frame.add(productPanel, BorderLayout.WEST);
        frame.add(cartPanel, BorderLayout.EAST);

        // Event handlers
        addToCartButton.addActionListener(e -> {
            String selected = productList.getSelectedValue();
            if (selected != null) {
                int productId = Integer.parseInt(selected.split(" - ")[0]);
                try {
                    platform.addToCart(productId);
                    cartListModel.addElement(selected);
                    JOptionPane.showMessageDialog(frame, "Added to cart!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a product to add!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        checkoutButton.addActionListener(e -> {
            if (cartListModel.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Your cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double total = platform.calculateTotal();
            String discountMessage = platform.checkDiscount();
            if (discountMessage != null) {
                JOptionPane.showMessageDialog(frame, discountMessage, "Discount", JOptionPane.INFORMATION_MESSAGE);
                total = platform.applyDiscount(total);
            }

            int result = JOptionPane.showConfirmDialog(frame, "Total: ₹" + total + "\nProceed to checkout?", "Checkout", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(frame, "Order placed successfully!\nThank you for shopping with us!", "Success", JOptionPane.INFORMATION_MESSAGE);
                platform.clearCart();
                cartListModel.clear();
            }
        });

        frame.setVisible(true);
    }
}
