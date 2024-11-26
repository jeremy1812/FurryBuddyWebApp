package ch.unil.doplab.demo.ui;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
public class PetOwnerBean implements Serializable {
    private String uuid;
    private String name;
    private List<String> pets;

    // Load data for the pet owner
    public void loadPetOwnerData() {
        // Load the pet owner's data using the UUID
        // Example: Query service or database for petOwner's details and pets
    }

    // Getter and Setter for UUID
    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    // Getter and Setter for other fields as needed
}
