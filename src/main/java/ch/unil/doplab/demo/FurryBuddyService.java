package ch.unil.doplab.demo;

import ch.unil.furrybuddy.domain.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class FurryBuddyService {
    private static final String BASE_URL = "http://localhost:8080/FurryBuddyService-1.0-SNAPSHOT/api";
    private WebTarget petOwnerTarget;
    private WebTarget adopterTarget;
    private WebTarget advertisementTarget;
    private WebTarget adoptionRequestTarget;
    private WebTarget serviceTarget;

    @PostConstruct
    public void init() {
        System.out.println("FurryBuddyService created: " + this.hashCode());
        Client client = ClientBuilder.newClient();
        petOwnerTarget = client.target(BASE_URL).path("petOwners");
        adopterTarget = client.target(BASE_URL).path("adopters");
        advertisementTarget = client.target(BASE_URL).path("advertisements");
        adoptionRequestTarget = client.target(BASE_URL).path("adoptionRequests");
        serviceTarget = client.target(BASE_URL).path("service");
    }

    /*
     * Service operations
     */

    public void resetService() {
        String response = serviceTarget
                .path("reset")
                .request()
                .get(String.class);
    }

    public UUID authenticate(String email, String password, String role) {
        var response = serviceTarget
                .path("authenticate")
                .path(email)
                .path(password)
                .path(role)
                .request(MediaType.APPLICATION_JSON)
                .get(UUID.class);
        return response;
    }

    /*
     PetOwner operations
     */

    public PetOwner getPetOwner(UUID id) {
        var petowner = petOwnerTarget
                .path(id.toString())
                .request()
                .get(PetOwner.class);
        return petowner;
    }

    public PetOwner addPetOwner(PetOwner petOwner) throws Exception {
        petOwner.setUserID(null);  // Ensure ID is not set to avoid server-side UUID errors
        Response response = petOwnerTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(petOwner, MediaType.APPLICATION_JSON));

        if (response.getStatus() == 200 && response.hasEntity()) {
            return response.readEntity(PetOwner.class);
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            try {
                Class<?> exceptionClass = Class.forName(description.getType());
                Constructor<?> constructor = exceptionClass.getConstructor(String.class);
                Exception exception = (Exception) constructor.newInstance(description.getMessage());
                throw exception;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean setPetOwner(PetOwner petOwner) throws Exception {
        var response = petOwnerTarget
                .path(petOwner.getUserID().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(petOwner, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getStatus() == 200;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
//            throw Utils.buildException(description); //TODO
            try {
                Class<?> exceptionClass = Class.forName(description.getType());
                Constructor<?> constructor = exceptionClass.getConstructor(String.class);
                Exception exception = (Exception) constructor.newInstance(description.getMessage());
                throw exception;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<PetOwner> getAllPetOwners() {
        var petOwners = petOwnerTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<PetOwner>>() {
                });
        return petOwners;
    }

    public boolean deletePetOwner(UUID petOwnerID) {
        var response = petOwnerTarget
                .path(petOwnerID.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == 200;
    }

    /*
     Adopter operations
     */

    public Adopter getAdopter(String id) {
        var adopter = adopterTarget
                .path(id.toString())
                .request()
                .get(Adopter.class);
        return adopter;
    }

    public Adopter addAdopter(Adopter adopter) throws Exception {
        adopter.setUserID(null);  // Ensure ID is not set to avoid server-side UUID errors
        Response response = adopterTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(adopter, MediaType.APPLICATION_JSON));

        if (response.getStatus() == 200 && response.hasEntity()) {
            return response.readEntity(Adopter.class);
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            try {
                Class<?> exceptionClass = Class.forName(description.getType());
                Constructor<?> constructor = exceptionClass.getConstructor(String.class);
                Exception exception = (Exception) constructor.newInstance(description.getMessage());
                throw exception;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean setAdopter(Adopter adopter) throws Exception {
        var response = adopterTarget
                .path(adopter.getUserID().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(adopter, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getStatus() == 200;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
//            throw Utils.buildException(description); //TODO
            try {
                Class<?> exceptionClass = Class.forName(description.getType());
                Constructor<?> constructor = exceptionClass.getConstructor(String.class);
                Exception exception = (Exception) constructor.newInstance(description.getMessage());
                throw exception;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Adopter> getAllAdopters() {
        var adopters = adopterTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Adopter>>() {
                });
        return adopters;
    }

    public boolean deleteAdopter(UUID adopterID) {
        var response = adopterTarget
                .path(adopterID.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == 200;
    }

    /*
    Advertisement operations
     */
    public Advertisement getAdvertisement(UUID id) {
        var advertisement = advertisementTarget
                .path(id.toString())
                .request()
                .get(Advertisement.class);
        return advertisement;
    }

    public boolean setAdvertisement(Advertisement advertisement) throws Exception {
        var response = advertisementTarget
                .path(advertisement.getAdvertisementID().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(advertisement, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getStatus() == 200;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
//            throw Utils.buildException(description); //TODO
            try {
                Class<?> exceptionClass = Class.forName(description.getType());
                Constructor<?> constructor = exceptionClass.getConstructor(String.class);
                Exception exception = (Exception) constructor.newInstance(description.getMessage());
                throw exception;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Advertisement> getAllAdvertisements() {
        var advertisements = advertisementTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Advertisement>>() {
                });
        return advertisements;
    }

    public Advertisement addAdvertisement(Advertisement advertisement) throws Exception {
        advertisement.setAdvertisementID(null);// To make sure the id is not set and avoid bug related to ill-formed UUID on server side
        var petownerID = advertisement.getPetOwnerID();

        var response = serviceTarget
                .path(petownerID.toString())
                .path("createAdvertisement")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(advertisement, MediaType.APPLICATION_JSON));

        if (response.getStatus() == 200 && response.hasEntity()) {
            var newlyCreatedAdvertisement = response.readEntity(Advertisement.class);
            advertisement.setAdvertisementID(newlyCreatedAdvertisement.getAdvertisementID());
            return newlyCreatedAdvertisement;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            // TODO Utils class in domain
            // Utils.buildException(description)
            try {
                Class<?> exceptionClass = Class.forName(description.getType());
                Constructor<?> constructor = exceptionClass.getConstructor(String.class);
                Exception exception = (Exception) constructor.newInstance(description.getMessage());
                throw exception;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean deleteAdvertisement(UUID advertisementID, UUID petOwnerID) {
        var response = serviceTarget
                .path(petOwnerID.toString())
                .path("deleteAdvertisement")
                .path(advertisementID.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == 200;
    }

//    public List<Advertisement> getAdvertisementsByPetOwner(UUID petOwnerID) {
//        return advertisementTarget
//                .path("byOwner")
//                .path(petOwnerID.toString())
//                .request(MediaType.APPLICATION_JSON)
//                .get(new GenericType<List<Advertisement>>() {});
//    }


    /*
    Adoption request operations
     */
    public AdoptionRequest getAdoptionRequest(UUID requestID) {
        var adoptionRequest = adoptionRequestTarget
                .path(requestID.toString())
                .request()
                .get(AdoptionRequest.class);
        return adoptionRequest;
    }

    public boolean setAdoptionRequest(AdoptionRequest adoptionRequest) throws Exception {
        var response = advertisementTarget
                .path(adoptionRequest.getRequestID().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(adoptionRequest, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getStatus() == 200;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
//            throw Utils.buildException(description); //TODO
            try {
                Class<?> exceptionClass = Class.forName(description.getType());
                Constructor<?> constructor = exceptionClass.getConstructor(String.class);
                Exception exception = (Exception) constructor.newInstance(description.getMessage());
                throw exception;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<AdoptionRequest> getAllAdoptionRequests() {
        var adoptionRequests = adoptionRequestTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<AdoptionRequest>>() {
                });
        return adoptionRequests;
    }

    public AdoptionRequest addAdoptionRequest(AdoptionRequest adoptionRequest) throws Exception {
        adoptionRequest.setRequestID(null);  // To make sure the id is not set and avoid bug related to ill-formed UUID on server side
        var response = adoptionRequestTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(adoptionRequest, MediaType.APPLICATION_JSON));

        if (response.getStatus() == 200 && response.hasEntity()) {
            var newlyCreatedAdoptionRequest = response.readEntity(AdoptionRequest.class);
            adoptionRequest.setRequestID(newlyCreatedAdoptionRequest.getRequestID());
            return newlyCreatedAdoptionRequest;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            // TODO Utils class in domain
            // Utils.buildException(description)
            try {
                Class<?> exceptionClass = Class.forName(description.getType());
                Constructor<?> constructor = exceptionClass.getConstructor(String.class);
                Exception exception = (Exception) constructor.newInstance(description.getMessage());
                throw exception;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean deleteAdoptionRequest(UUID requestID) {
        var response = adoptionRequestTarget
                .path(requestID.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == 200;
    }

    public List<AdoptionRequest> getRequestsForAdvertisements(UUID petOwnerID) {
        return adoptionRequestTarget
                .path("requestsForOwner")
                .path(petOwnerID.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<AdoptionRequest>>() {});
    }
    public List<AdoptionRequest> getRequestsForAdopter(String adopterName) {
        return adoptionRequestTarget
                .path("byAdopter")
                .path(adopterName)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<AdoptionRequest>>() {});
    }

    public boolean updateRequestStatus(AdoptionRequest adoptionRequest) throws Exception {
        var response = adoptionRequestTarget
                .path(adoptionRequest.getRequestID().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(adoptionRequest, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return true;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            try {
                Class<?> exceptionClass = Class.forName(description.getType());
                Constructor<?> constructor = exceptionClass.getConstructor(String.class);
                Exception exception = (Exception) constructor.newInstance(description.getMessage());
                throw exception;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }



}