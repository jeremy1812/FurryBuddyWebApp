package ch.unil.doplab.demo.ui;

import ch.unil.furrybuddy.domain.AdoptionRequest;
import ch.unil.doplab.demo.FurryBuddyService;
import ch.unil.furrybuddy.domain.Advertisement;
import ch.unil.furrybuddy.domain.PetOwner;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.component.log.Log;

import java.io.Serializable;
import java.util.*;

//TODO revoir les champs qui n'existent pas
@Named
@SessionScoped
public class AdoptionRequestBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // Champs pour les détails d'une demande
    private UUID advertisementID;
    private Advertisement selectedAdvertisement;
    private String adopterName;
    private String petName;
    private String petType;
    private Date requestDate;
    private String petOwnerName;
    private String status;
    private String requestMessage;

    private List<AdoptionRequest> requests; // Liste des demandes d'adoption
    private AdoptionRequest selectedRequest; // Requête sélectionnée

    @Inject
    private FurryBuddyService service; // Service pour interagir avec le backend

    @Inject
    private LoginBean loginBean;

    @Named
    @Inject
    private AdvertisementBean advertisementBean;

    //TODO init method


    // Getters et Setters
    public List<AdoptionRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<AdoptionRequest> requests) {
        this.requests = requests;
    }

    public AdoptionRequest getSelectedRequest() {
        return selectedRequest;
    }

    public void setSelectedRequest(AdoptionRequest selectedRequest) {
        this.selectedRequest = selectedRequest;
    }

    public String getAdopterName() {
        return adopterName;
    }

    public void setAdopterName(String adopterName) {
        this.adopterName = adopterName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetOwnerName(){return petOwnerName;}
    public void setPetOwnerName(String petOwnerName){this.petOwnerName = petOwnerName;}

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public UUID getAdvertisementID() {
        return advertisementID;
    }

    public void setAdvertisementID(UUID advertisementID) {
        this.advertisementID = advertisementID;
    }

    public Advertisement getSelectedAdvertisement() {
        return selectedAdvertisement;
    }

    public void setSelectedAdvertisement(Advertisement selectedAdvertisement) {
        this.selectedAdvertisement = selectedAdvertisement;
    }

    public void loadAdvertisement(){
        if (advertisementID != null) {
            selectedAdvertisement = service.getAdvertisement(advertisementID);
        }
    }
    public void loadAdvertisementFromQuery() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String adID = params.get("selectedAdvertisementID");
        if (adID != null) {
            selectedAdvertisement = service.getAdvertisement(UUID.fromString(adID));
        }
    }
    // Chargement des requêtes pour un adoptant
    public List<AdoptionRequest> loadMyRequests() {
        UUID userId = loginBean.getLoggedInUserId();
        if (userId != null) {
            requests = service.getAdopter(userId).getAdoptionRequests();
        }
        return Collections.emptyList();
    }

    // Annuler une demande
    public void cancelAdoptionRequest(AdoptionRequest request) {
        if (request == null || request.getAdopterID() == null || request.getRequestID() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Invalid request",
                            "Cannot cancel adoption request: Missing required data."));
            return;
        }

        try {
            service.cancelAdoptionRequest(request);
            // Update the list of requests in the bean to reflect the change
            loadMyRequests();

            // Display success message
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Request Cancelled",
                            "Adoption request has been successfully cancelled."));
        } catch (Exception e) {
            // Display error message if the service call fails
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Cancellation Failed",
                            "An error occurred while cancelling the adoption request: " + e.getMessage()));
        }
    }


    // Voir les détails d'une demande
    public String viewRequestDetails(AdoptionRequest request) {
        this.selectedRequest = request;
        return "ViewAdvertisement.xhtml?faces-redirect=true";
    }

    // Modifier une demande
    public String modifyRequest(AdoptionRequest request) {
        this.selectedRequest = request;
        return "ModifyRequest.xhtml?faces-redirect=true";
    }

    public String submitRequest() {
        try {
            AdoptionRequest adoptionRequest = new AdoptionRequest(loginBean.getLoggedInUserId(), selectedAdvertisement, AdoptionRequest.Status.PENDING);
//            adoptionRequest.setAdvertisement(selectedAdvertisement);
//            adoptionRequest.setAdopterID(loginBean.getLoggedInUserId()); // Fetch logged-in user's ID
//            adoptionRequest.setMessage(requestMessage); TODO

            service.addAdoptionRequest(adoptionRequest);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Request Submitted",
                            "Your adoption request has been successfully submitted."));
            return "MyRequests?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Request Failed",
                            "Could not submit the request. " + e.getMessage()));
            return null;
        }
    }
}
