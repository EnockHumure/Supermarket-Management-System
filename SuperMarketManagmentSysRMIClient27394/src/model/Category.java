package model;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
    
    public static final long serialVersionUID = 1L;
    
    private Long categoryId;
    private String name;
    private String description;
    private List<Product> products;

    public Category() {
    }

    public Category(Long categoryId, String name, String description, List<Product> products) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.products = products;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
