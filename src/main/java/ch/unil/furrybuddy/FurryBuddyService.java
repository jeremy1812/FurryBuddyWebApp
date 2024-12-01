package ch.unil.furrybuddy;

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
import org.primefaces.shaded.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public Adopter getAdopter(UUID userID) {
        var adopter = adopterTarget
                .path(userID.toString())
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

    //filter advertisements
    public List<Advertisement> filterAdvertisements(String species, String breed, String gender, List<String> compatibility) {
        WebTarget target = serviceTarget
                .path("advertisements")
                .path("filter")
                .queryParam("species", species)
                .queryParam("breed", breed)
                .queryParam("gender", gender);

        if (compatibility != null) {
            compatibility.forEach(comp -> target.queryParam("compatibility", comp));
        }

        Response response = target.request(MediaType.APPLICATION_JSON).get();

        if (response.getStatus() == 200 && response.hasEntity()) {
            return response.readEntity(new GenericType<List<Advertisement>>() {});
        } else {
            throw new RuntimeException("Failed to filter advertisements: " + response.getStatus());
        }
    }

    //get random pet picture
    public String fetchRandomDogImage() {
        try {
            URL url = new URL("https://dog.ceo/api/breeds/image/random");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON to extract image URL
                String json = response.toString();
                String imageUrl = new JSONObject(json).getString("message");
                return imageUrl;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Fallback placeholder image
        return "https://via.placeholder.com/200x150.png?text=No+Image";
    }

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
        var adopterID = adoptionRequest.getAdopterID();
        var response = serviceTarget
                .path(adopterID.toString())
                .path("createAdoptionRequest")
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

    public AdoptionRequest cancelAdoptionRequest(AdoptionRequest adoptionRequest){
        var response = serviceTarget
                .path(adoptionRequest.getAdopterID().toString())
                .path("cancelAdoptionRequest")
                .path(adoptionRequest.getRequestID().toString())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(adoptionRequest, MediaType.APPLICATION_JSON));

        if (response.getStatus() == 200 && response.hasEntity()) {
            return response.readEntity(AdoptionRequest.class);
        }

        else {
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

    public AdoptionRequest acceptAdoptionRequest(AdoptionRequest adoptionRequest){
        var response = serviceTarget
                .path(adoptionRequest.getAdvertisement().getPetOwnerID().toString())
                .path("acceptAdoptionRequest")
                .path(adoptionRequest.getRequestID().toString())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(adoptionRequest, MediaType.APPLICATION_JSON));

        if (response.getStatus() == 200 && response.hasEntity()) {
            return response.readEntity(AdoptionRequest.class);
        }

        else {
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

    public AdoptionRequest rejectAdoptionRequest(AdoptionRequest adoptionRequest){
        var response = serviceTarget
                .path(adoptionRequest.getAdvertisement().getPetOwnerID().toString())
                .path("rejectAdoptionRequest")
                .path(adoptionRequest.getRequestID().toString())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(adoptionRequest, MediaType.APPLICATION_JSON));

        if (response.getStatus() == 200 && response.hasEntity()) {
            return response.readEntity(AdoptionRequest.class);
        }

        else {
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