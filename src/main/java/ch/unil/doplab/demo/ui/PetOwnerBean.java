package ch.unil.doplab.demo.ui;

import ch.unil.doplab.demo.FurryBuddyService;
import ch.unil.furrybuddy.domain.Advertisement;
import ch.unil.furrybuddy.domain.AdoptionRequest;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@SessionScoped
@Named
public class PetOwnerBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID uuid;
    private String name;
    private List<Advertisement> myAdvertisements;
    private List<AdoptionRequest> receivedRequests;

    @Inject
    private FurryBuddyService theService;

    // Méthode pour charger les données du propriétaire
    public void loadPetOwnerData() {
        // Utiliser le UUID pour charger les informations
        System.out.println("Chargement des données pour le propriétaire : " + uuid);
    }

    // Charger les publicités postées
    public void loadMyAdvertisements() {
        if (uuid != null) {
            myAdvertisements = theService.getAdvertisementsByPetOwner(uuid);
        }
    }

    // Charger les demandes reçues pour ses publicités
    public void loadReceivedRequests() {
        if (uuid != null) {
            receivedRequests = theService.getRequestsForAdvertisements(uuid);
        }
    }

    // Getters et Setters
    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Advertisement> getMyAdvertisements() {
        return myAdvertisements;
    }

    public void setMyAdvertisements(List<Advertisement> myAdvertisements) {
        this.myAdvertisements = myAdvertisements;
    }

    public List<AdoptionRequest> getReceivedRequests() {
        return receivedRequests;
    }

    public void setReceivedRequests(List<AdoptionRequest> receivedRequests) {
        this.receivedRequests = receivedRequests;
    }
}


