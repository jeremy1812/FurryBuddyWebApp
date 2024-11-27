package ch.unil.doplab.demo.ui;
import ch.unil.furrybuddy.domain.PetOwner;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@SessionScoped
@Named
public class PetOwnerBean extends PetOwner implements Serializable {
    private UUID uuid;
    private String name;
    private List<String> pets;

    // Load data for the pet owner
    public void loadPetOwnerData() {
        // Load the pet owner's data using the UUID
        // Example: Query service or database for petOwner's details and pets
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
