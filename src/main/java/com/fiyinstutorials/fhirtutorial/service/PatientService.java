package com.fiyinstutorials.fhirtutorial.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.fiyinstutorials.fhirtutorial.repository.IdentifierRepository;
import com.fiyinstutorials.fhirtutorial.repository.PatientRepository;
import com.fiyinstutorials.fhirtutorial.responseDTO.IdentifierDTO;
import com.fiyinstutorials.fhirtutorial.responseDTO.PatientResponse;
import com.fiyinstutorials.fhirtutorial.utils.StatusCodes;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PatientService {

    @Value("${hapi.server.base-url}")
    private String hapiServerBaseUrl;

    @Value("${hapi.server.tag}")
    private String hapiServerTag;

    private final FhirContext fhirContext = FhirContext.forR4();
    private final PatientRepository patientRepository;
    private final IdentifierRepository identifierRepository;
    private final ModelMapper modelMapper;


    @Autowired
    public PatientService(PatientRepository patientRepository, IdentifierRepository identifierRepository, ModelMapper modelMapper) {
        this.patientRepository = patientRepository;
        this.identifierRepository = identifierRepository;
        this.modelMapper = modelMapper;
    }

    public List<PatientResponse> getAllPatients() {
        List<PatientResponse> patientResponses = new ArrayList<>();

        IGenericClient client = fhirContext.newRestfulGenericClient(hapiServerBaseUrl);
        int offset = 0;
        int count = 100;
        int length = 0;

        do {
            Bundle query = fetchPatientRecordFromApi(client, offset, count);

            if (query != null) {
                List<Bundle.BundleEntryComponent> bundleEntry = query.getEntry();
                length = bundleEntry.size();

                bundleEntry
                        .parallelStream()
                        .forEach(this::processPatientRecord);

                offset++;
            }

        } while (length != 0);

        return patientResponses;
    }


    private Bundle fetchPatientRecordFromApi(IGenericClient client, int offset, int count){

        return client.search()
                .forResource(Patient.class)
                .returnBundle(Bundle.class)
                .offset(offset)
                .count(count)
                .execute();
    }

    private void processPatientRecord(Bundle.BundleEntryComponent entry){

            Patient patient = (Patient) entry.getResource();
            String patientId = patient.getIdElement().getIdPart();
            if (patientRepository.existsByPatientId(patientId)) {
                log.info("Patient with ID {} already exists in the database, skipping save.", patientId);
                updatePatientRecord(entry);
                return;
            }

            PatientResponse patientResponse = new PatientResponse();
            patientResponse.setPatientId(patientId);
            patientResponse.setPatientUniqueId(hapiServerTag + "/"+StatusCodes.RESOURCE_TYPE_PATIENT+"-" + patientId);

            Optional<HumanName> officialName = patient.getName().stream()
                    .filter(name -> HumanName.NameUse.OFFICIAL.equals(name.getUse()))
                    .findFirst();
            if (officialName.isPresent()) {
                String firstName = officialName.get().getGivenAsSingleString();
                patientResponse.setFirstName(firstName);

                String lastName = officialName.get().getFamily();
                patientResponse.setLastName(lastName);
            } else {
                // Handle case where official name is not present
                if (!patient.getName().isEmpty()) {
                    HumanName firstAvailableName = patient.getName().get(0);
                    String firstName = firstAvailableName.getGivenAsSingleString();
                    String lastName = firstAvailableName.getFamily();
                    patientResponse.setFirstName(firstName);
                    patientResponse.setLastName(lastName);
                }
            }

            // Set email from telecom details if available
            for (ContactPoint telecom : patient.getTelecom()) {
                if (patientResponse.getEmail() != null && patientResponse.getPhone() != null) {
                    break;
                }
                if (patientResponse.getEmail() == null && telecom.getSystem() == ContactPoint.ContactPointSystem.EMAIL) {
                    patientResponse.setEmail(telecom.getValue());
                }
                if (patientResponse.getPhone() == null && telecom.getSystem() == ContactPoint.ContactPointSystem.PHONE) {
                    patientResponse.setPhone(telecom.getValue());
                }
            }

            // Set address details
            if (!patient.getAddress().isEmpty()) {
                Address address = patient.getAddressFirstRep();

                String fullAddress = address.getLine().stream()
                        .map(StringType::getValue)
                        .collect(Collectors.joining(", "));

                patientResponse.setAddress(fullAddress); // Assuming at least one line is present
                patientResponse.setCity(address.getCity());
                patientResponse.setDistrict(address.getDistrict());
                patientResponse.setState(address.getState());
                patientResponse.setPostalCode(address.getPostalCode());
                patientResponse.setCountry(address.getCountry());
            }

            if (patient.getGender() != null) {
                patientResponse.setGender(patient.getGender().getDisplay());
            }

            if (patient.getBirthDate() != null) {
                patientResponse.setBirthDate(Objects.toString(patient.getBirthDate()));
            }

            if (patient.getDeceased() instanceof BooleanType) {
                patientResponse.setDeceased(patient.getDeceasedBooleanType().getValue());
            }
            if (patient.getMaritalStatus() != null) {
                patientResponse.setMaritalStatus(patient.getMaritalStatus().getCodingFirstRep().getDisplay());
            }

            // Handling general practitioner if available
            if (!patient.getGeneralPractitioner().isEmpty()) {
                patientResponse.setGeneralPractitioner(patient.getGeneralPractitionerFirstRep().getDisplay());
            }

            // Handling managing organization if available
            if (patient.getManagingOrganization() != null) {
                patientResponse.setManagingOrganization(patient.getManagingOrganization().getDisplay());
            }

            // Handle identifiers
            if (patient.getIdentifier() != null && !patient.getIdentifier().isEmpty()) {
                for (org.hl7.fhir.r4.model.Identifier identifier : patient.getIdentifier()) {
                    IdentifierDTO identifierDTO = IdentifierDTO.builder()
                            .system(identifier.getSystem())
                            .value(identifier.getValue())
                            .build();
                    patientResponse.getIdentifiers().add(identifierDTO);
                }
            }

            com.fiyinstutorials.fhirtutorial.model.Patient entity = modelMapper.map(patientResponse, com.fiyinstutorials.fhirtutorial.model.Patient.class);

            patientRepository.save(entity);

            if (patientResponse.getIdentifiers() != null) {
                List<com.fiyinstutorials.fhirtutorial.model.Identifier> identifies = patientResponse
                        .getIdentifiers()
                        .stream()
                        .map(dto -> {
                            com.fiyinstutorials.fhirtutorial.model.Identifier identifier = new com.fiyinstutorials.fhirtutorial.model.Identifier();
                            identifier.setResourceType(StatusCodes.RESOURCE_TYPE_PATIENT);
                            identifier.setResourceId(patientResponse.getPatientId());
                            identifier.setIdentifierSystem(dto.getSystem());
                            identifier.setIdentifierValue(dto.getValue());
                            return identifier;
                        })
                        .collect(Collectors.toList());

                identifierRepository.saveAll(identifies);
            }

            log.info("Added patient with ID: {}", patientResponse.getPatientId());
    }

    private void updatePatientRecord(Bundle.BundleEntryComponent entry){
        String patientId = entry.getResource().getIdElement().getIdPart();
        log.info("Updating patient record: {}", patientId);
        System.out.println("Updating patient record: " + patientId);
    }


//    public List<PatientUpdateResponse> updatePatients() {
//        List<PatientUpdateResponse> updateResponses = new ArrayList<>();
//
//        // Construct the URL using UriComponentsBuilder
//        try {
//            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(hapiServerBaseUrl + "/Patient")
////                    .queryParam("_getpages", 1)
////                    .queryParam("_getpagesoffset", 1)
//                    .queryParam("_count", 100)
////                    .queryParam("_pretty", "true")
//                    .queryParam("_bundletype", "searchset");
//
//            String url = builder.toUriString();
//
//            // Retrieve the initial bundle from the FHIR server
//            Bundle bundle = restTemplate.getForObject(url, Bundle.class);
//
//            while (bundle != null) {
//                for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
//                    Patient patient = (Patient) entry.getResource();
//                    String patientId = patient.getIdElement().getIdPart();
//
//                    Optional<com.fiyinstutorials.fhirtutorial.model.Patient> optionalPatient = patientRepository.findByPatientId(patientId);
//                    if (optionalPatient.isPresent()) {
//                        com.fiyinstutorials.fhirtutorial.model.Patient existingPatient = optionalPatient.get();
//                        PatientUpdateDTO patientUpdateDTO = modelMapper.map(patient, PatientUpdateDTO.class);
//
//                        updateResponses.addAll(compareAndUpdatePatient(existingPatient, patientUpdateDTO));
//                    }
//
//                    // Check for the next page of results
//                    if (bundle != null && bundle.getLink(Bundle.LINK_NEXT) != null) {
//                        String nextPageUrl = bundle.getLink(Bundle.LINK_NEXT).getUrl();
//                        bundle = restTemplate.getForObject(nextPageUrl, Bundle.class);
//                    }
//                }
//            }
//        }catch (Exception e) {
//            log.error("Error occurred while updating patients: {}", e.getMessage(), e);
//        }
//            return updateResponses;
//    }
//
//        private List<PatientUpdateResponse> compareAndUpdatePatient(com.fiyinstutorials.fhirtutorial.model.Patient existingPatient, PatientUpdateDTO patientUpdateDTO) {
//            List<PatientUpdateResponse> updatedResponse = new ArrayList<>();
//
//            // Create PatientRequest from FHIR patient data
//            if (!Objects.equals(existingPatient.getFirstName(), patientUpdateDTO.getFirstName())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "firstName", existingPatient.getFirstName(), patientUpdateDTO.getFirstName()));
//                existingPatient.setFirstName(patientUpdateDTO.getFirstName());
//            }
//            if (!Objects.equals(existingPatient.getLastName(), patientUpdateDTO.getLastName())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "lastName", existingPatient.getLastName(), patientUpdateDTO.getLastName()));
//                existingPatient.setLastName(patientUpdateDTO.getLastName());
//            }
//            if (!Objects.equals(existingPatient.getPhone(), patientUpdateDTO.getPhone())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "phone", existingPatient.getPhone(), patientUpdateDTO.getPhone()));
//                existingPatient.setPhone(patientUpdateDTO.getPhone());
//            }
//            if (!Objects.equals(existingPatient.getEmail(), patientUpdateDTO.getEmail())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "email", existingPatient.getEmail(), patientUpdateDTO.getEmail()));
//                existingPatient.setEmail(patientUpdateDTO.getEmail());
//            }
//            if (!Objects.equals(existingPatient.getGender(), patientUpdateDTO.getGender())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "gender", existingPatient.getGender(), patientUpdateDTO.getGender()));
//                existingPatient.setGender(patientUpdateDTO.getGender());
//            }
//            if (!Objects.equals(existingPatient.getBirthDate(), patientUpdateDTO.getBirthDate())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "birthDate", existingPatient.getBirthDate(), patientUpdateDTO.getBirthDate()));
//                existingPatient.setBirthDate(patientUpdateDTO.getBirthDate());
//            }
//            if (!Objects.equals(existingPatient.getDeceased(), patientUpdateDTO.getDeceased())) {
//                String oldValue = Boolean.toString(existingPatient.getDeceased());
//                String newValue = Boolean.toString(patientUpdateDTO.getDeceased());
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "deceased", oldValue, newValue));
//                existingPatient.setDeceased(patientUpdateDTO.getDeceased());
//            }
//            if (!Objects.equals(existingPatient.getAddress(), patientUpdateDTO.getAddress())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "address", existingPatient.getAddress(), patientUpdateDTO.getAddress()));
//                existingPatient.setAddress(patientUpdateDTO.getAddress());
//            }
//            if (!Objects.equals(existingPatient.getCity(), patientUpdateDTO.getCity())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "city", existingPatient.getCity(), patientUpdateDTO.getCity()));
//                existingPatient.setCity(patientUpdateDTO.getCity());
//            }
//            if (!Objects.equals(existingPatient.getDistrict(), patientUpdateDTO.getDistrict())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "district", existingPatient.getDistrict(), patientUpdateDTO.getDistrict()));
//                existingPatient.setDistrict(patientUpdateDTO.getDistrict());
//            }
//            if (!Objects.equals(existingPatient.getState(), patientUpdateDTO.getState())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "state", existingPatient.getState(), patientUpdateDTO.getState()));
//                existingPatient.setState(patientUpdateDTO.getState());
//            }
//            if (!Objects.equals(existingPatient.getPostalCode(), patientUpdateDTO.getPostalCode())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "postalCode", existingPatient.getPostalCode(), patientUpdateDTO.getPostalCode()));
//                existingPatient.setPostalCode(patientUpdateDTO.getPostalCode());
//            }
//            if (!Objects.equals(existingPatient.getCountry(), patientUpdateDTO.getCountry())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "country", existingPatient.getCountry(), patientUpdateDTO.getCountry()));
//                existingPatient.setCountry(patientUpdateDTO.getCountry());
//            }
//            if (!Objects.equals(existingPatient.getMaritalStatus(), patientUpdateDTO.getMaritalStatus())) {
//                updatedResponse.add(createUpdateResponse(existingPatient.getPatientUniqueId(), "maritalStatus", existingPatient.getMaritalStatus(), patientUpdateDTO.getMaritalStatus()));
//                existingPatient.setMaritalStatus(patientUpdateDTO.getFirstName());
//            }
//
//            patientRepository.save(existingPatient);
//
//            return updatedResponse;
//        }
//
//    private PatientUpdateResponse createUpdateResponse(String patientUniqueId, String fieldName, String oldValue, String newValue) {
//        return PatientUpdateResponse.builder()
//                .patientId(patientUniqueId.split("-")[1]) // Extract patientId from patientUniqueId
//                .patientUniqueId(patientUniqueId)
//                .updatedFieldName(fieldName)
//                .oldValue(oldValue)
//                .newValue(newValue)
//                .build();
//    }
}
