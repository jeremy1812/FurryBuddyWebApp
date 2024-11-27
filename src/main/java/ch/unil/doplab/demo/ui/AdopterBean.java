package ch.unil.doplab.demo.ui;

import ch.unil.furrybuddy.domain.Adopter;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@SessionScoped
@Named
public class AdopterBean extends Adopter implements Serializable {
    private UUID uuid;
    private String name;
    private List<String> adoptionApplications;

    // Load data for the adopter
    public void loadAdopterData() {
        // Load the adopter's data using the UUID
        // Example: Query service or database for adopter's details and applications
    }

    // Getter and Setter for UUID
    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    // Getter and Setter for other fields as needed
}
