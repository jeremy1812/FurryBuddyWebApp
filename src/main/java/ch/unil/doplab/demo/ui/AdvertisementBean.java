package ch.unil.doplab.demo.ui;

import ch.unil.doplab.demo.FurryBuddyService;
import ch.unil.furrybuddy.domain.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SessionScoped
@Named
public class AdvertisementBean extends Advertisement implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(AdvertisementBean.class.getName());

    // Advertisement Fields
    private Advertisement theAdvertisement;
    private boolean changed;
    private String dialogMessage;
    private List<Advertisement> allAdvertisements;
    private Advertisement selectedAdvertisement;
    private List<Advertisement> allPetOwnerAds;

    // Nested Objects
    private Pet pet;
    private Location location;

    // Image Field
    private String image;

    // Filter options
    private List<String> speciesOptions;
    private List<String> breedOptions;
    private List<String> compatibilityOptions;

    //  Selected Filters
    private String selectedSpecies;
    private String selectedBreed;
    private String selectedGender;
    private List<String> selectedCompatibility;
    private List<Advertisement> filteredAdvertisements;

    @Inject
    private FurryBuddyService theService;

    @Inject
    private LoginBean loginBean;

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
        allPetOwnerAds = null;
        pet = new Pet(); // Initialize nested Pet object
        location = new Location(); // Initialize nested Location object
        image = null;

        speciesOptions = null;
        breedOptions = null;
        compatibilityOptions = null;
        selectedSpecies = null;
        selectedBreed = null;
        selectedGender = null;
        selectedCompatibility = null;
        filteredAdvertisements = null;

        loadFilterOptions();
    }

    private void loadFilterOptions() {
        speciesOptions = List.of("Dog", "Cat", "Sheep", "Hamster", "Fish", "Other", "Bird");
        compatibilityOptions = List.of("Good with Kids", "Good with Other Pets", "House-Trained");
        breedOptions = List.of(
                // Dog breeds
                "Labrador Retriever",
                "German Shepherd",
                "Golden Retriever",
                "French Bulldog",
                "Poodle",
                "Bulldog",
                "Beagle",
                "Rottweiler",
                "Siberian Husky",
                "Dachshund",
                "Shih Tzu",

                // Cat breeds
                "Persian",
                "Maine Coon",
                "Bengal",
                "Siamese",
                "Ragdoll",
                "Scottish Fold",
                "British Shorthair",
                "Abyssinian",
                "Sphynx",
                "Russian Blue",

                // Bird breeds
                "Parakeet",
                "Cockatiel",
                "African Grey",
                "Lovebird",
                "Macaw",
                "Canary",
                "Finch",
                "Conure",
                "Cockatoo",
                "Amazon Parrot",

                // Rabbit breeds
                "Holland Lop",
                "Netherland Dwarf",
                "Flemish Giant",
                "Lionhead",
                "Mini Rex",
                "English Lop",
                "French Lop",
                "Angora Rabbit",
                "Polish Rabbit",
                "Harlequin"
                );
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
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Getters and Setters for Advertisement Fields
    public Advertisement getSelectedAdvertisement() {
        return selectedAdvertisement;
    }

    public void setSelectedAdvertisement(Advertisement selectedAdvertisement) {
        this.selectedAdvertisement = selectedAdvertisement;
    }

    public boolean isChanged(){return changed;}

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void loadAllAdvertisements() {
        allAdvertisements = theService.filterAdvertisements(selectedSpecies, selectedBreed, selectedGender, selectedCompatibility);
//        filteredAdvertisements = new ArrayList<>(theService.getAllAdvertisements());
    }

    public List<Advertisement> getAllAdvertisements() {
        return allAdvertisements;
    }

    public Advertisement loadSpecificAdvertisement(Advertisement advertisement) {
        return theService.getAdvertisement(advertisement.getAdvertisementID());
    }

    // Method to Add Advertisement
    public Advertisement addAdvertisement() {
        // Create a new advertisement object
        Advertisement advertisement = new Advertisement(
                pet,
                loginBean.getLoggedInUserId(),
                pet.getDescription(),
                location,
                Status.AVAILABLE
        );
        try {
            // Ensure the pet has an ID
            if (pet.getPetID() == null) {
                pet.setPetID(UUID.randomUUID());
                log.info("Generated new Pet ID: " + pet.getPetID());
            }

            // Send the advertisement to the service for saving
            advertisement.setImageURL(theService.fetchRandomDogImage());
            advertisement = theService.addAdvertisement(advertisement);
            log.info("Advertisement successfully created: " + advertisement);

            // Provide success feedback to the user
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Advertisement created successfully!"));

            return advertisement; // Return the created advertisement

        } catch (Exception e) {
            // Log and handle errors gracefully
            log.warning("Failed to create advertisement: " + e.getMessage());
            log.info("Advertisement details: " + advertisement);

            // Provide error feedback to the user
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('updateErrorDialog').show();");
        }

        return null;
    }

    // Method to Delete Advertisement
    public void deleteAdvertisement(Advertisement advertisement) {
        if (advertisement != null && advertisement.getAdvertisementID() != null) {
            theService.deleteAdvertisement(advertisement.getAdvertisementID(), advertisement.getPetOwnerID());
            boolean success = theService.deleteAdvertisement(advertisement.getAdvertisementID(), advertisement.getPetOwnerID());

            if (success) {
                loadAllAdvertisements(); // Refresh the list
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Advertisement deleted successfully",
                                "The advertisement was successfully removed."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Failed to delete advertisement",
                                "An error occurred while deleting the advertisement."));
            }
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

    public String editAdvertisement(Advertisement ad) {
        this.selectedAdvertisement = ad;
        return "EditAdvertisement.xhtml?faces-redirect=true";
    }

    public List<Advertisement> loadPetOwnerAdvertisements() {
        UUID userId = loginBean.getLoggedInUserId();
        if (userId != null) {
            return theService.getPetOwner(userId).getAdvertisements();
        }
        return Collections.emptyList(); // Return empty list if no user is logged in
    }

    public List<String> getSpeciesOptions() {
        return speciesOptions;
    }

    public List<String> getBreedOptions() {
        return breedOptions;
    }

    public List<String> getCompatibilityOptions() {
        return compatibilityOptions;
    }

    public String getSelectedSpecies() {
        return selectedSpecies;
    }

    public void setSelectedSpecies(String selectedSpecies) {
        this.selectedSpecies = selectedSpecies;
    }

    public String getSelectedBreed() {
        return selectedBreed;
    }

    public void setSelectedBreed(String selectedBreed) {
        this.selectedBreed = selectedBreed;
    }

    public String getSelectedGender() {
        return selectedGender;
    }

    public void setSelectedGender(String selectedGender) {
        this.selectedGender = selectedGender;
    }

    public List<String> getSelectedCompatibility() {
        return selectedCompatibility;
    }

    public void setSelectedCompatibility(List<String> selectedCompatibility) {
        this.selectedCompatibility = selectedCompatibility;
    }

    public List<Advertisement> getFilteredAdvertisements() {
        return filteredAdvertisements;
    }

    public void applyFilters() {
        try {
            this.filteredAdvertisements = theService.filterAdvertisements(selectedSpecies, selectedBreed, selectedGender, selectedCompatibility);
            log.info("Filtered advertisements retrieved: " + filteredAdvertisements.size());
            PrimeFaces.current().ajax().update("advertisementForm");
        } catch (Exception e) {
            log.severe("Failed to apply filters: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Filter Error", "Unable to filter advertisements."));
        }
    }

    // Method to handle file upload
//    public void handleImageUpload(FileUploadEvent event) {
//        try {
//            this.image = event.getFile().getContent(); // Read the uploaded file into the byte array
//            FacesContext.getCurrentInstance().addMessage(null,
//                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Image Uploaded", "File: " + event.getFile().getFileName()));
//        } catch (Exception e) {
//            FacesContext.getCurrentInstance().addMessage(null,
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload Failed", e.getMessage()));
//        }
//    }

    // Vérifie si des modifications ont été faites
    public void checkIfChanged() {
        if (selectedAdvertisement.getAdvertisementID() != null) {
            Advertisement advertisement = theService.getAdvertisement(selectedAdvertisement.getAdvertisementID());
            boolean petChanged = !this.pet.equals(advertisement.getPet());
            boolean locationChanged = !this.location.equals(advertisement.getLocation());
            this.changed = petChanged || locationChanged;
        }
    }

    //modifier une annonce
    public void updateAdvertisement() {
        try {
            Advertisement advertisement = theService.getAdvertisement(selectedAdvertisement.getAdvertisementID());
            if (advertisement != null) {
                advertisement.setPet(this.pet);
                advertisement.setDescription(this.pet.getDescription());
                this.changed = false;
                this.dialogMessage = "Advertisement updated successfully.";
            }
        } catch (Exception e) {
            this.dialogMessage = "Error updating advertisement: " + e.getMessage();
        }
    }
}
