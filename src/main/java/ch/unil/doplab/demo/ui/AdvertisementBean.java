package ch.unil.doplab.demo.ui;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@ApplicationScoped
@Named
public class AdvertisementBean implements Serializable {
    private Long id;
    private String title;
    private String description;
    private String category;
    private Double price;
    private String contactEmail;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    // Method to load data (if needed)
    public void loadAddAdvertisementPage() {
        // Initialization logic (if any)
    }

    // Method to add advertisement
    public String addAdvertisement() {
        // Logic to save advertisement to the database
        System.out.println("Advertisement added: " + title);
        return "success"; // Redirect to success page or stay on the same page
    }
}
