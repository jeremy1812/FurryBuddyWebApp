package ch.unil.doplab.demo.ui;

import ch.unil.doplab.demo.FurryBuddyService;
import ch.unil.furrybuddy.domain.Advertisement;
import ch.unil.furrybuddy.domain.Location;
import ch.unil.furrybuddy.domain.Pet;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SessionScoped
@Named
public class AdvertisementBean extends Advertisement implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private byte[] image;

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

    public void loadAllAdvertisements() {
        allAdvertisements = theService.getAllAdvertisements();
        filteredAdvertisements = new ArrayList<>(allAdvertisements);
    }

    public List<Advertisement> getAllAdvertisements() {
        return allAdvertisements;
    }

    public Advertisement loadSpecificAdvertisement(Advertisement advertisement) {
        return theService.getAdvertisement(advertisement.getAdvertisementID());
    }

    // Method to Add Advertisement
    public void addAdvertisement() {
        if (!loginBean.isPetOwner()) {
            throw new IllegalArgumentException("User is not a pet owner.");
        }

        UUID userId = loginBean.getLoggedInUserId();
        if (userId == null) {
            throw new IllegalArgumentException("User ID is null. Please log in.");
        }

        try {
            // Create Advertisement
            theAdvertisement = new Advertisement(
                    pet,
                    userId, // Pet owner ID
                    pet.getDescription(),
                    location,
                    Advertisement.Status.AVAILABLE
            );

            // Save Advertisement
            var savedAdvertisement = theService.addAdvertisement(theAdvertisement);
            if (savedAdvertisement != null) {
                this.theAdvertisement = savedAdvertisement;
                changed = false;

                // Success message
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Advertisement added successfully",
                                "Your advertisement is now live."));
            }
        } catch (Exception e) {
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('updateErrorDialog').show();");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Failed to add advertisement",
                            e.getMessage()));
        }
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
        filteredAdvertisements = allAdvertisements.stream()
                .filter(ad -> (selectedSpecies == null || selectedSpecies.isEmpty() || selectedSpecies.equals(ad.getPet().getSpecies())))
                .filter(ad -> (selectedBreed == null || selectedBreed.isEmpty() || selectedBreed.equals(ad.getPet().getBreed())))
                .filter(ad -> (selectedGender == null || selectedGender.isEmpty() || matchesGender(ad.getPet().getGender())))
                .filter(ad -> matchesCompatibilityFlags(ad.getPet()))
                .collect(Collectors.toList());

        PrimeFaces.current().ajax().update("advertisementForm");

    }

    private boolean matchesCompatibilityFlags(Pet pet) {
        if (selectedCompatibility == null || selectedCompatibility.isEmpty()) {
            return true; // No compatibility filter applied
        }

        boolean match = true;
        if (selectedCompatibility.contains("Good with Kids")) {
            match &= pet.isCompatibleWithKids();
        }
        if (selectedCompatibility.contains("Good with Other Animals")) {
            match &= pet.isCompatibleWithOtherAnimals();
        }
        if (selectedCompatibility.contains("Suitable for Inexperienced Owners")) {
            match &= pet.isCompatibleWithInexperiencedOwners();
        }
        if (selectedCompatibility.contains("Suitable for Families")) {
            match &= pet.isCompatibleWithFamilies();
        }
        if (selectedCompatibility.contains("Suitable for House")) {
            match &= pet.isSuitableForHouse();
        }
        return match;
    }

    // Helper method for gender matching
    private boolean matchesGender(Pet.Gender gender) {
        if (selectedGender == null || selectedGender.isEmpty()) {
            return true; // No gender filter applied
        }
        return gender != null && gender.name().equalsIgnoreCase(selectedGender);
    }
}
