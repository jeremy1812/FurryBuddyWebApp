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
public class AdvertisementBean extends Advertisement implements Serializable {
    private static final long serialVersionUID = 1L;

    // Variables for the logic service
    private Advertisement theAdvertisement;
    private boolean changed;
    private String dialogMessage;
    private List<Advertisement> allAdvertisements;
    private Advertisement selectedAdvertisement;

    // Fields for Pet
    private String petName;
    private String species;
    private String breed;
    private boolean neutered;
    private Pet.Gender gender;
    private String description;
    private String personality;
    private String color;
    private boolean compatibleWithInexperiencedOwners;
    private boolean compatibleWithKids;
    private boolean compatibleWithFamilies;
    private boolean compatibleWithOtherAnimals;
    private int age;
    private double price;
    private boolean suitableForHouse;
    private boolean vaccinated;
    private String medicalConditions;

    // Fields for Location
    private String town;
    private String postalCode;
    private String address;

    // Other fields
    private byte[] image;

    @Inject
    FurryBuddyService theService;

    // Default constructor
    public AdvertisementBean() {
        this(null, null, null, null, null, null);
    }
    public AdvertisementBean(UUID advertisementID, Pet pet, UUID petOwnerID, String description, Location location, Advertisement.Status status) {
        init();
        theAdvertisement = new Advertisement(advertisementID, pet, petOwnerID, description, location, status);
    }

    public void init() {
        theAdvertisement = null;
        changed = false;
        dialogMessage = null;
        allAdvertisements = null;
        selectedAdvertisement = null;

        //pet info
        petName = null;
        species = null;
        breed = null;
        neutered = false;
        gender = null;
        description = null;
        personality = null;
        color = null;
        compatibleWithInexperiencedOwners = false;
        compatibleWithKids = false;
        compatibleWithFamilies = false;
        compatibleWithOtherAnimals = false;
        age = 0;
        price = 0.0;
        suitableForHouse = false;
        vaccinated = false;
        medicalConditions = null;

        // location info
        town = null;
        postalCode = null;
        address = null;

        // other
        image = null;
    }

    // Getters and Setters
    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public boolean isNeutered() {
        return neutered;
    }

    public void setNeutered(boolean neutered) {
        this.neutered = neutered;
    }

    public Pet.Gender getGender() {
        return gender;
    }

    public void setGender(Pet.Gender gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isCompatibleWithInexperiencedOwners() {
        return compatibleWithInexperiencedOwners;
    }

    public void setCompatibleWithInexperiencedOwners(boolean compatibleWithInexperiencedOwners) {
        this.compatibleWithInexperiencedOwners = compatibleWithInexperiencedOwners;
    }

    public boolean isCompatibleWithKids() {
        return compatibleWithKids;
    }

    public void setCompatibleWithKids(boolean compatibleWithKids) {
        this.compatibleWithKids = compatibleWithKids;
    }

    public boolean isCompatibleWithFamilies() {
        return compatibleWithFamilies;
    }

    public void setCompatibleWithFamilies(boolean compatibleWithFamilies) {
        this.compatibleWithFamilies = compatibleWithFamilies;
    }

    public boolean isCompatibleWithOtherAnimals() {
        return compatibleWithOtherAnimals;
    }

    public void setCompatibleWithOtherAnimals(boolean compatibleWithOtherAnimals) {
        this.compatibleWithOtherAnimals = compatibleWithOtherAnimals;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSuitableForHouse() {
        return suitableForHouse;
    }

    public void setSuitableForHouse(boolean suitableForHouse) {
        this.suitableForHouse = suitableForHouse;
    }

    public boolean isVaccinated() {
        return vaccinated;
    }

    public void setVaccinated(boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    public String getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
    }

    // Getters and Setters for Location fields
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public Advertisement getSelectedAdvertisement() {
        return selectedAdvertisement;
    }

    public void setSelectedAdvertisement(Advertisement selectedAdvertisement) {
        this.selectedAdvertisement = selectedAdvertisement;
    }

    public String viewDetails(Advertisement ad) {
        this.selectedAdvertisement = ad;
        return "ViewAdvertisement.xhtml?faces-redirect=true";
    }

    // Load all advertisements
    public void loadAllAdvertisements() {
        allAdvertisements = theService.getAllAdvertisements();
    }

    public Advertisement loadSpecificAdvertisement(Advertisement advertisement) {
        return theService.getAdvertisement(advertisement.getAdvertisementID());
    }

    // Getter and Setter for allAdvertisements
    public List<Advertisement> getAllAdvertisements() {
        return allAdvertisements;
    }

    // Business logic for adding an advertisement
    public void addAdvertisement() {
        try {
            // Create Pet object
            Pet pet = new Pet(UUID.randomUUID(), petName, species, breed, neutered, gender, description, personality, color,
                    compatibleWithInexperiencedOwners, compatibleWithKids, compatibleWithFamilies, compatibleWithOtherAnimals,
                    age, price, suitableForHouse, vaccinated, medicalConditions);

            // Create Location object
            Location location = new Location(town, postalCode, address);

            // Create Advertisement object
            //TODO petowner ID logic
//            var petOwner = LoginBean.getLoggedInUser();
            theAdvertisement = new Advertisement(UUID.randomUUID(), pet, UUID.fromString("d79b117e-6cd5-44f0-8ab0-8c87ccda04f0"), description, location, Advertisement.Status.AVAILABLE);

            // Save the advertisement using the service
            var savedAdvertisement = theService.addAdvertisement(theAdvertisement);
            if (savedAdvertisement != null) {
                theAdvertisement = savedAdvertisement;
                this.replaceWith(theAdvertisement);
                changed = false;
            }

        } catch (Exception e) {
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('updateErrorDialog').show();");
        }

    }

    public void deleteAdvertisement() {
        var id = this.getAdvertisementID();
        if (id != null) {
            theService.deleteAdvertisement(id);
        }
    }

    // Method to load data (if needed)
    public void loadAddAdvertisementPage() {
        // Initialization logic (if any)
    }
}
