package ch.unil.furrybuddy.ui;

import ch.unil.furrybuddy.domain.*;
import ch.unil.furrybuddy.FurryBuddyService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

@SessionScoped
@Named
public class AdoptionRequestBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(AdvertisementBean.class.getName());

    // Champs pour les détails d'une demande
    private UUID advertisementID;
    private Advertisement selectedAdvertisement;
    private String adopterName;
    private String petName;
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

    public AdoptionRequestBean(){
        init();
    }

    @PostConstruct
    public void init(){
        if (loginBean == null) {
            log.severe("LoginBean is null in AdoptionRequestBean!");
        } else {
            log.info("LoginBean successfully injected with UUID: " + loginBean.getLoggedInUserId());
        }

//        loadMyRequests();
    }


    // Getters et Setters
    public List<AdoptionRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<AdoptionRequest> requests) {
        this.requests = requests;
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

    // Chargement des requêtes pour un adoptant
    public List<AdoptionRequest> loadMyRequests() {

        if (loginBean == null || loginBean.getLoggedInUserId() == null) {
            log.warning("LoginBean or logged-in user ID is null!");
            return Collections.emptyList(); // Return an empty list to avoid breaking the page
        }

        // Initialize the requests list to avoid null
        if (requests == null) {
            requests = new ArrayList<>();
        }

        if (loginBean.getLoggedInUserId() != null) {
            try {
                // Load adoption requests for the logged-in user
                requests = service.getAdopter(loginBean.getLoggedInUserId()).getAdoptionRequests();
            } catch (Exception e) {
                System.err.println("Error loading requests: " + e.getMessage());
                // Log the error and ensure the requests list is not null
                requests = new ArrayList<>();
            }
        }

        return requests;
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
            log.severe("Cancelled adoption request: " + request.getAdopterID());
            // Update the list of requests in the bean to reflect the change
//            loadMyRequests();

            // Display success message
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Request Cancelled",
                            "Adoption request has been successfully cancelled."));
        } catch (Exception e) {
            log.severe("ERROR TOTO " + e.getMessage());
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

    //soumettre une demande
    public AdoptionRequest submitRequest(UUID advertisementID) {
                AdoptionRequest adoptionRequest = new AdoptionRequest(
                        loginBean.getLoggedInUserId(),
                        service.getAdvertisement(advertisementID),
                        AdoptionRequest.Status.PENDING,
                        requestMessage
                );
//                adoptionRequest.setMessage(requestMessage);

                try {
                    adoptionRequest = service.addAdoptionRequest(adoptionRequest);
                    log.info("Adoption Request Successfully created " + adoptionRequest.toString());

                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO,
                                    "Request Submitted",
                                    "Your adoption request has been successfully submitted."));
                    return adoptionRequest;
                } catch (Exception e) {
                    log.warning("Failed to create new adoption request: " + e.getMessage());
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Request Failed",
                                    "Could not submit the request. Please try again later."));
            return null;
        }
    }

    //obtenir le nom de l'adopter
    public String getAdopterNameDetails(AdoptionRequest adoptionRequest){
        if (adoptionRequest != null) {
            Adopter adopter = service.getAdopter(adoptionRequest.getAdopterID());
            return adopter.getFirstName();
        }
        return null;
    }

    //accepter une request
    public void acceptAdoptionRequest(AdoptionRequest adoptionRequest) {
        if (adoptionRequest == null || adoptionRequest.getAdopterID() == null || adoptionRequest.getRequestID() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Invalid request",
                            "Cannot cancel adoption request: Missing required data."));
            return;
        }

        try {
            service.acceptAdoptionRequest(adoptionRequest);
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

    //decliner une request
    public void rejectAdoptionRequest(AdoptionRequest adoptionRequest) {
        if (adoptionRequest == null || adoptionRequest.getAdopterID() == null || adoptionRequest.getRequestID() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Invalid request",
                            "Cannot cancel adoption request: Missing required data."));
            return;
        }

        try {
            service.rejectAdoptionRequest(adoptionRequest);
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
}
