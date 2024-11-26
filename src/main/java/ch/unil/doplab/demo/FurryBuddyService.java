package ch.unil.doplab.demo;

import ch.unil.furrybuddy.domain.Advertisement;
import ch.unil.furrybuddy.domain.ExceptionDescription;
import ch.unil.furrybuddy.domain.PetOwner;
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

    /*
     * PetOwner operations
     */

    public PetOwner getPetOwner(String id) {
        var petowner = petOwnerTarget
                .path(id.toString())
                .request()
                .get(PetOwner.class);
        return petowner;
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
        advertisement.setAdvertisementID(null);  // To make sure the id is not set and avoid bug related to ill-formed UUID on server side
        var response = advertisementTarget
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

    public boolean deleteAdvertisement(UUID id) {
        var response = advertisementTarget
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == 200;
    }


}