package ch.unil.doplab.demo.ui;

import ch.unil.doplab.demo.FurryBuddyService;
import ch.unil.furrybuddy.domain.Advertisement;
import ch.unil.furrybuddy.domain.Location;
import ch.unil.furrybuddy.domain.Pet;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@SessionScoped
@Named
public class AdvertisementBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // Advertisement Fields
    private Advertisement theAdvertisement;
    private boolean changed;
    private String dialogMessage;
    private List<Advertisement> allAdvertisements;
    private Advertisement selectedAdvertisement;

    // Nested Objects
    private Pet pet;
    private Location location;

    // Image Field
    private byte[] image;

    @Inject
    private FurryBuddyService theService;

    // Constructor
    public AdvertisementBean() {
        init();
    }

    public void init() {
        theAdvertisement = null;
        changed = false;
        dialogMessage = null;
        allAdvertisements = null;
        selectedAdvertisement = null;
        pet = new Pet(); // Initialize nested Pet object
        location = new Location(); // Initialize nested Location object
        image = null;
    }

    // Getters and Setters for Pet
    public Pet getPet() {
        if (this.pet == null) {
            this.pet = new Pet(); // Lazy initialization
        }
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    // Getters and Setters for Location
    public Location getLocation() {
        if (this.location == null) {
            this.location = new Location(); // Lazy initialization
        }
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // Getters and Setters for Image
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    // Getters and Setters for Advertisement Fields
    public Advertisement getSelectedAdvertisement() {
        return selectedAdvertisement;
    }

    public void setSelectedAdvertisement(Advertisement selectedAdvertisement) {
        this.selectedAdvertisement = selectedAdvertisement;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public List<Advertisement> getAllAdvertisements() {
        return allAdvertisements;
    }

    public void loadAllAdvertisements() {
        allAdvertisements = theService.getAllAdvertisements();
    }

    public Advertisement loadSpecificAdvertisement(Advertisement advertisement) {
        return theService.getAdvertisement(advertisement.getAdvertisementID());
    }

    // Method to Add Advertisement
    public void addAdvertisement() {
        try {
            // Validate Required Fields
            if (pet.getName() == null || pet.getName().isEmpty()) {
                throw new IllegalArgumentException("Pet name is required.");
            }
            if (location.getTown() == null || location.getTown().isEmpty()) {
                throw new IllegalArgumentException("Town is required.");
            }

            // Create Advertisement
            theAdvertisement = new Advertisement(
                    UUID.randomUUID(),
                    pet,
                    UUID.randomUUID(), // Placeholder for pet owner ID
                    pet.getDescription(),
                    location,
                    Advertisement.Status.AVAILABLE
            );

            // Save Advertisement
            var savedAdvertisement = theService.addAdvertisement(theAdvertisement);
            if (savedAdvertisement != null) {
                this.theAdvertisement = savedAdvertisement;
                changed = false;
            }
        } catch (Exception e) {
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('updateErrorDialog').show();");
        }
    }

    // Method to Delete Advertisement
    public void deleteAdvertisement() {
        if (theAdvertisement != null && theAdvertisement.getAdvertisementID() != null) {
            theService.deleteAdvertisement(theAdvertisement.getAdvertisementID());
        }
    }

    // Method to Load Add Advertisement Page
    public void loadAddAdvertisementPage() {
        init();
    }

    public String viewDetails(Advertisement ad) {
        this.selectedAdvertisement = ad;
        return "ViewAdvertisement.xhtml?faces-redirect=true";
    }
}
