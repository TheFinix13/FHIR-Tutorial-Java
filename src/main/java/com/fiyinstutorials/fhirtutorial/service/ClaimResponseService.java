package com.fiyinstutorials.fhirtutorial.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.fiyinstutorials.fhirtutorial.repository.ClaimResponseRepository;
import com.fiyinstutorials.fhirtutorial.responseDTO.*;
import com.fiyinstutorials.fhirtutorial.utils.StatusCodes;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClaimResponseService {
    @Value("${hapi.server.base-url}")
    private String hapiServerBaseUrl;

    @Value("${hapi.server.tag}")
    private String hapiServerTag;

    private final FhirContext fhirContext = FhirContext.forR4();
    ;
    //    private final PatientRepository patientRepository;
    private final ClaimResponseRepository claimResponseRepository;
    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;

    @Autowired
    public ClaimResponseService(RestTemplate restTemplate, ClaimResponseRepository claimResponseRepository, ModelMapper modelMapper) {
        this.restTemplate = restTemplate;
//        this.patientRepository = patientRepository;
        this.claimResponseRepository = claimResponseRepository;
        this.modelMapper = modelMapper;
    }

    public String fetchClaimResponses() {
        // Build the URL for the FHIR server query
        String url = UriComponentsBuilder.fromHttpUrl(hapiServerBaseUrl)
                .pathSegment("ClaimResponse")
                .queryParam("_count", "100")
                .queryParam("_include", "*")
                .queryParam("_include", "ClaimResponse:patient")
                .queryParam("_include", "ClaimResponse:request")
                .queryParam("_include", "ClaimResponse:requestor")
                .queryParam("_pretty", "true")
                .toUriString();

        // Make the HTTP GET request

        // Return the response as a String (or you could parse it into a more structured format)
        return restTemplate.getForObject(url, String.class);
    }

    public List<CRResponse> getClaimResponses() {
        // Add a logging interceptor to log requests and responses
        IGenericClient client = fhirContext.newRestfulGenericClient(hapiServerBaseUrl);

        IClientInterceptor loggingInterceptor = new LoggingInterceptor();
        client.registerInterceptor(loggingInterceptor);

        List<CRResponse> allResponses = new ArrayList<>();
        String nextPageUrl = null;

        try {
            do {
                Bundle bundle;
                if (nextPageUrl == null) {
                    // First request
                    bundle = client
                            .search()
                            .forResource(ClaimResponse.class)
                            .count(100)
                            .include(ClaimResponse.INCLUDE_PATIENT)
                            .returnBundle(Bundle.class)
                            .execute();
                } else {
                    bundle = client
                            .loadPage()
                            .byUrl(nextPageUrl)
                            .andReturnBundle(Bundle.class)
                            .execute();
                }

                // Map FHIR ClaimResponse to ResponseDTOs
                List<CRResponse> responses = bundle
                        .getEntry()
                        .stream()
                        .filter(entry -> entry.getResource() instanceof ClaimResponse) // Filter for ClaimResponse only
                        .map(entry -> {
                            ClaimResponse claimResponse = (ClaimResponse) entry.getResource();
                            return mapToCRResponse(claimResponse);
                        })
                        .collect(Collectors.toList());

                // Add the current page's responses to the list
                allResponses.addAll(responses);

                // Get the next page URL, if available
                nextPageUrl = bundle.getLink(Bundle.LINK_NEXT) != null ? bundle.getLink(Bundle.LINK_NEXT).getUrl() : null;

            } while (nextPageUrl != null);

            // Log the list of CRResponse DTOs
            allResponses.forEach(responseDto -> log.info("ClaimResponse DTO: {}", responseDto));

            return allResponses;

        } catch (InvalidRequestException e) {
            log.error("Invalid request error: ", e);
            throw e;
        } catch (Exception e) {
            log.error("Error fetching ClaimResponses: ", e);
            throw e;
        }
    }

    private CRResponse mapToCRResponse(ClaimResponse claimResponse) {
        CRResponse responseDto = new CRResponse();

        // Set basic fields
        String claimResponseId = claimResponse.getIdElement().getIdPart();
        responseDto.setEHRCategoryTag(hapiServerTag);
        responseDto.setClaimResponseId(claimResponseId);
        responseDto.setClaimResponseUniqueId(hapiServerTag + "-" + claimResponseId);

        if (claimResponse.hasStatus()) {
            String customStatus = mapStatus(claimResponse.getStatus().toCode());
            responseDto.setStatus(customStatus);
        }
        if (claimResponse.hasUse()) {
            String customUse = mapUse(claimResponse.getUse().toCode());
            responseDto.setUse(customUse);
        }
        if (claimResponse.hasCreated()) {
            responseDto.setCreated(claimResponse.getCreated());
        }
        if (claimResponse.hasPatient() && claimResponse.getPatient().hasReference()) {
            responseDto.setPatientReference(claimResponse.getPatient().getReference());
        }
        if (claimResponse.hasInsurer() && claimResponse.getInsurer().hasIdentifier()) {
            responseDto.setInsurerIdentifierSystem(claimResponse.getInsurer().getIdentifier().getSystem());
            responseDto.setInsurerIdentifierValue(claimResponse.getInsurer().getIdentifier().getValue());
        }
        if (claimResponse.hasRequestor() && claimResponse.getRequestor().hasReference()) {
            responseDto.setRequestorReference(claimResponse.getRequestor().getReference());
        }
        if (claimResponse.hasRequest() && claimResponse.getRequest().hasReference()) {
            responseDto.setRequestReference(claimResponse.getRequest().getReference());
        }
        if (claimResponse.hasOutcome()) {
            String customOutcome = mapOutcome(claimResponse.getOutcome().toCode());
            responseDto.setOutcome(customOutcome);
        }
        if (claimResponse.hasDisposition()) {
            responseDto.setDisposition(claimResponse.getDisposition());
        }

        // Map payee type
        if (claimResponse.hasPayeeType() && claimResponse.getPayeeType().hasCoding()) {
            responseDto.setPayeeType(claimResponse.getPayeeType().getCoding().stream()
                    .map(coding -> {
                        CodingDTO codingDTO = new CodingDTO();
                        codingDTO.setSystem(coding.getSystem());
                        codingDTO.setCode(mapPayeeType(coding.getCode()));
                        return codingDTO;
                    })
                    .collect(Collectors.toList()));
        }

        // Map items
        if (claimResponse.hasItem()) {
            responseDto.setItem(claimResponse.getItem().stream()
                    .map(item -> {
                        ClaimResponseItemDTO itemDTO = new ClaimResponseItemDTO();
                        itemDTO.setItemSequence(item.getItemSequence());

                        // Map adjudications
                        List<ClaimResponseAdjudicationDTO> adjudicationDTOs = item.getAdjudication().stream()
                                .map(adjudication -> {
                                    ClaimResponseAdjudicationDTO adjudicationDTO = new ClaimResponseAdjudicationDTO();
                                    adjudicationDTO.setAdjudicationCategoryCode(mapAdjudicationCategory(adjudication.getCategory().getCodingFirstRep().getCode()));
                                    adjudicationDTO.setAdjudicationAmountValue(adjudication.getAmount().getValue().doubleValue());
                                    adjudicationDTO.setAdjudicationAmountCurrency(adjudication.getAmount().getCurrency());
                                    adjudicationDTO.setAdjudicationReasonCode(adjudication.getReason().getCodingFirstRep().getCode());
                                    adjudicationDTO.setAdjudicationReasonSystem(adjudication.getReason().getCodingFirstRep().getSystem());
                                    adjudicationDTO.setAdjudicationReasonDisplay(adjudication.getReason().getCodingFirstRep().getDisplay());
                                    return adjudicationDTO;
                                })
                                .collect(Collectors.toList());
                        itemDTO.setAdjudication(adjudicationDTOs);

                        return itemDTO;
                    })
                    .collect(Collectors.toList()));
        }

        // Map totals
        if (claimResponse.hasTotal()) {
            responseDto.setTotal(claimResponse.getTotal().stream()
                    .map(total -> {
                        ClaimResponseAdjudicationDTO totalDTO = new ClaimResponseAdjudicationDTO();
                        totalDTO.setAdjudicationCategoryCode(mapAdjudicationCategory(total.getCategory().getCodingFirstRep().getCode()));
                        totalDTO.setAdjudicationAmountValue(total.getAmount().getValue().doubleValue());
                        totalDTO.setAdjudicationAmountCurrency(total.getAmount().getCurrency());
                        return totalDTO;
                    })
                    .collect(Collectors.toList()));
        }

        // Map payment details
        if (claimResponse.hasPayment()) {
            responseDto.setPaymentType(claimResponse.getPayment().getType().getCoding().stream()
                    .map(coding -> {
                        CodingDTO codingDTO = new CodingDTO();
                        codingDTO.setSystem(coding.getSystem());
                        codingDTO.setCode(mapPaymentType(coding.getCode()));
                        return codingDTO;
                    })
                    .collect(Collectors.toList()));
            responseDto.setPaymentDate(claimResponse.getPayment().getDate());
            if (claimResponse.getPayment().hasAmount()) {
                responseDto.setPaymentAmountValue(claimResponse.getPayment().getAmount().getValue().doubleValue());
                responseDto.setPaymentAmountCurrency(claimResponse.getPayment().getAmount().getCurrency());
            }
            if (claimResponse.getPayment().hasAdjustment()) {
                responseDto.setPaymentAdjustmentCurrency(claimResponse.getPayment().getAdjustment().getCurrency());
                responseDto.setPaymentAdjustmentValue(claimResponse.getPayment().getAdjustment().getValue().doubleValue());
            }
            if (claimResponse.getPayment().hasAdjustmentReason()) {
                responseDto.setPaymentAdjustmentReason(claimResponse.getPayment().getAdjustmentReason().getCodingFirstRep().getCode());
            }
            if (claimResponse.getPayment().hasIdentifier()) {
                responseDto.setPaymentIdentifierSystem(claimResponse.getPayment().getIdentifier().getSystem());
                responseDto.setPaymentIdentifierValue(claimResponse.getPayment().getIdentifier().getValue());
            }
        }

        return responseDto;

    }

    public void fetchAndSaveClaimResponses() {
        int pageCount = 100; // Adjust batch size as needed

        Bundle bundle = fetchInitialClaimResponses(pageCount);
        if (bundle == null) {
            log.error("Failed to fetch initial ClaimResponses.");
            return;
        }

        while (bundle != null && !bundle.getEntry().isEmpty()) {
            // Process the fetched ClaimResponses and save them
            List<CRResponse> crResponses = bundle.getEntry().stream()
                    .filter(entry -> entry.getResource() instanceof ClaimResponse)
                    .map(entry -> {
                        ClaimResponse claimResponse = (ClaimResponse) entry.getResource();
                        return mapToCRResponse(claimResponse);
                    })
                    .collect(Collectors.toList());

            if (crResponses.isEmpty()) {
                log.info("No new ClaimResponses found on this page.");
            }

            // Convert CRResponse DTOs to entity objects
            List<com.fiyinstutorials.fhirtutorial.model.ClaimResponse> claimResponseEntities = crResponses.stream()
                    .map(dto -> modelMapper.map(dto, com.fiyinstutorials.fhirtutorial.model.ClaimResponse.class))
                    .collect(Collectors.toList());

            // Save entities to the database
            claimResponseEntities.forEach(entity -> {
                if (claimResponseRepository.existsByClaimResponseId(entity.getClaimResponseId())) {
                    log.info("Skipping existing ClaimResponse with ID: {}", entity.getClaimResponseId());
                } else {
                    claimResponseRepository.save(entity);
                    log.info("Saved new ClaimResponse with ID: {}", entity.getClaimResponseId());
                }
            });

            // Fetch the next page
            bundle = fetchNextPage(bundle);
        }
    }

    private Bundle fetchInitialClaimResponses(int pageCount) {
        IGenericClient client = fhirContext.newRestfulGenericClient(hapiServerBaseUrl);
        try {
            IQuery<Bundle> query = client.search()
                    .forResource(ClaimResponse.class)
                    .count(pageCount)
                    .include(ClaimResponse.INCLUDE_PATIENT)
                    .returnBundle(Bundle.class);

            return query.execute();
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching initial ClaimResponses: {}", e.getMessage(), e);
        }
        return null;
    }

    private Bundle fetchNextPage(Bundle currentBundle) {
        if (currentBundle == null) {
            return null;
        }
        IGenericClient client = fhirContext.newRestfulGenericClient(hapiServerBaseUrl);
        try {
            return client.loadPage().next(currentBundle).execute();
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching next ClaimResponses page: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Fetches all ClaimResponses and maps them to PatientClaimResponse objects.
     *
     * @return List of PatientClaimResponse objects
     */
    public List<PatientClaimResponse> getAllPatientClaimResponses() {
        // Fetch all ClaimResponses
        List<ClaimResponse> claimResponses = fetchAllClaimResponses();

        // Map and return list of PatientClaimResponse
        return claimResponses.stream()
                .map(this::mapToPatientClaimResponse)
                .collect(Collectors.toList());
    }
    /**
     * Fetches all ClaimResponses from the FHIR server.
     *
     * @return List of ClaimResponse objects
     */
    private List<ClaimResponse> fetchAllClaimResponses() {
        IGenericClient client = fhirContext.newRestfulGenericClient(hapiServerBaseUrl);
        Bundle bundle = client.search()
                .forResource(ClaimResponse.class)
                .returnBundle(Bundle.class)
                .execute();

        return bundle.getEntry().stream()
                .map(entry -> (ClaimResponse) entry.getResource())
                .collect(Collectors.toList());
    }
    /**
     * Fetches a Patient resource using a patient reference string.
     *
     * @param patientReference The patient reference string (e.g., "Patient/1")
     * @return Patient object
     */
    private Patient fetchPatientIdByReference(String patientReference) {
        IGenericClient client = fhirContext.newRestfulGenericClient(hapiServerBaseUrl);
        // Extract the patient ID from the reference string
        String[] referenceParts = patientReference.split("/");
        if (referenceParts.length != 2 || !referenceParts[0].equals("Patient")) {
            log.error("Invalid patient reference: {}", patientReference);
            return null;
        }
        String patientId = referenceParts[1];
        try {
            return client.read()
                    .resource(Patient.class)
                    .withId(patientId)
                    .execute();
        }catch (Exception e) {
            log.error("Error fetching Patient with ID {}: {}", patientId, e.getMessage());
            return null;
        }
    }
    /**
     * Maps a ClaimResponse object to a PatientClaimResponse object.
     *
     * @param claimResponse The ClaimResponse object
     * @return PatientClaimResponse object
     */
    private PatientClaimResponse mapToPatientClaimResponse(ClaimResponse claimResponse) {
        // Fetch Patient details using patient reference from ClaimResponse
        String patientReference = claimResponse.getPatient().getReference();
        Patient patient = fetchPatientIdByReference(patientReference);

        PatientClaimResponse patientClaimResponse = new PatientClaimResponse();
        // Map Patient details
        if (patient != null) {
            mapPatientDetails(patient, patientClaimResponse);
        }
        // Map ClaimResponse Data
        mapClaimResponseDetails(claimResponse, patientClaimResponse);

        return patientClaimResponse;

    }
    /**
     * Maps Patient details to a PatientClaimResponse object.
     *
     * @param patient The Patient object
     * @param patientClaimResponse The PatientClaimResponse object to populate
     */
    private void mapPatientDetails(Patient patient, PatientClaimResponse patientClaimResponse){
        if (patient.hasId()) {
            String patientServerId = patient.getIdElement().getIdPart();
            patientClaimResponse.setPatientUniqueId(hapiServerTag + "-Patient-" + patientServerId);

        }
        if (patient.hasName()) {
            HumanName firstAvailableName = patient.getName().get(0);
            String firstName = firstAvailableName.getGivenAsSingleString();
            String lastName = firstAvailableName.getFamily();
            patientClaimResponse.setFirstName(firstName);
            patientClaimResponse.setLastName(lastName);
        }
        if (patient.hasTelecom()) {
            // Initialize variables to hold phone and email values
            String phone = null;
            String email = null;

            for (ContactPoint telecom : patient.getTelecom()) {
                // Check the system type and assign value accordingly
                if (telecom.getSystem() == ContactPoint.ContactPointSystem.PHONE) {
                    phone = telecom.getValue();
                } else if (telecom.getSystem() == ContactPoint.ContactPointSystem.EMAIL) {
                    email = telecom.getValue();
                }
            }
            // Set phone and email values to the patientClaimResponse
            patientClaimResponse.setPhone(phone);
            patientClaimResponse.setEmail(email);
        }
        if (patient.hasGender()) {
            patientClaimResponse.setGender(patient.getGender().toCode());
        }
        if (patient.hasBirthDate()) {
            patientClaimResponse.setBirthDate(patient.getBirthDateElement().getValueAsString());
        }
        if (patient.hasGeneralPractitioner()) {
            patientClaimResponse.setGeneralPractitioner(patient.getGeneralPractitionerFirstRep().getReference());
        }
        if (patient.hasManagingOrganization()) {
            patientClaimResponse.setManagingOrganization(patient.getManagingOrganization().getReference());
        }
        if (patient.hasAddress()) {
            Address address = patient.getAddressFirstRep();

            String fullAddress = address.getLine().stream()
                    .map(StringType::getValue)
                    .collect(Collectors.joining(", "));

            patientClaimResponse.setAddress(fullAddress); // Assuming at least one line is present
            patientClaimResponse.setCity(address.getCity());
            patientClaimResponse.setDistrict(address.getDistrict());
            patientClaimResponse.setState(address.getState());
            patientClaimResponse.setPostalCode(address.getPostalCode());
            patientClaimResponse.setCountry(address.getCountry());
        }
    }
    /**
     * Maps ClaimResponse details to a PatientClaimResponse object.
     *
     * @param claimResponse The ClaimResponse object
     * @param patientClaimResponse The PatientClaimResponse object to populate
     */
    private void mapClaimResponseDetails(ClaimResponse claimResponse, PatientClaimResponse patientClaimResponse) {
        if (claimResponse != null) {
            if (claimResponse.hasId()) {
                String claimResponseServerId = claimResponse.getIdElement().getIdPart();
                patientClaimResponse.setClaimResponseUniqueId(hapiServerTag + "-ClaimResponse-" + claimResponseServerId);
            }
            if (claimResponse.hasStatus()) {
                patientClaimResponse.setClaimResponseStatus(claimResponse.getStatus().toCode());
            }
            if (claimResponse.hasUse()) {
                patientClaimResponse.setClaimResponseUse(claimResponse.getUse().toCode());
            }
            if (claimResponse.hasOutcome()) {
                patientClaimResponse.setClaimResponseOutcome(claimResponse.getOutcome().toCode());
            }
            if (claimResponse.hasDisposition()) {
                patientClaimResponse.setClaimResponseDisposition(claimResponse.getDisposition());
            }
            if (claimResponse.hasPatient() && claimResponse.getPatient().hasReference()) {
                patientClaimResponse.setPatientReference(claimResponse.getPatient().getReference());
            }
            // Map items
            if (claimResponse.hasItem()) {
                patientClaimResponse.setItem(claimResponse.getItem().stream()
                        .map(item -> {
                            ClaimResponseItemDTO itemDTO = new ClaimResponseItemDTO();
                            itemDTO.setItemSequence(item.getItemSequence());

                            // Map adjudications
                            List<ClaimResponseAdjudicationDTO> adjudicationDTOs = item.getAdjudication().stream()
                                    .map(adjudication -> {
                                        ClaimResponseAdjudicationDTO adjudicationDTO = new ClaimResponseAdjudicationDTO();
                                        adjudicationDTO.setAdjudicationCategoryCode(mapAdjudicationCategory(adjudication.getCategory().getCodingFirstRep().getCode()));
                                        adjudicationDTO.setAdjudicationAmountValue(adjudication.getAmount().getValue().doubleValue());
                                        adjudicationDTO.setAdjudicationAmountCurrency(adjudication.getAmount().getCurrency());
                                        adjudicationDTO.setAdjudicationReasonCode(adjudication.getReason().getCodingFirstRep().getCode());
                                        adjudicationDTO.setAdjudicationReasonSystem(adjudication.getReason().getCodingFirstRep().getSystem());
                                        adjudicationDTO.setAdjudicationReasonDisplay(adjudication.getReason().getCodingFirstRep().getDisplay());
                                        return adjudicationDTO;
                                    })
                                    .collect(Collectors.toList());
                            itemDTO.setAdjudication(adjudicationDTOs);

                            return itemDTO;
                        })
                        .collect(Collectors.toList()));
            }
            // Map totals
            if (claimResponse.hasTotal()) {
                patientClaimResponse.setTotal(claimResponse.getTotal().stream()
                        .map(total -> {
                            ClaimResponseAdjudicationDTO totalDTO = new ClaimResponseAdjudicationDTO();
                            totalDTO.setAdjudicationCategoryCode(mapAdjudicationCategory(total.getCategory().getCodingFirstRep().getCode()));
                            totalDTO.setAdjudicationAmountValue(total.getAmount().getValue().doubleValue());
                            totalDTO.setAdjudicationAmountCurrency(total.getAmount().getCurrency());
                            return totalDTO;
                        })
                        .collect(Collectors.toList()));
            }
            // Map payment details
            if (claimResponse.hasPayment()) {
                patientClaimResponse.setPaymentType(claimResponse.getPayment().getType().getCoding().stream()
                        .map(coding -> {
                            CodingDTO codingDTO = new CodingDTO();
                            codingDTO.setSystem(coding.getSystem());
                            codingDTO.setCode(mapPaymentType(coding.getCode()));
                            return codingDTO;
                        })
                        .collect(Collectors.toList()));
                patientClaimResponse.setPaymentDate(claimResponse.getPayment().getDate());

                if (claimResponse.getPayment().hasAmount()) {
                    patientClaimResponse.setPaymentAmountValue(claimResponse.getPayment().getAmount().getValue().doubleValue());
                    patientClaimResponse.setPaymentAmountCurrency(claimResponse.getPayment().getAmount().getCurrency());
                }
            }
        }
    }

    private String mapStatus(String fhirStatusCode) {
        switch (fhirStatusCode) {
            case "active":
                return StatusCodes.CLAIM_RESPONSE_STATUS_CODE_ACTIVE;
            case "cancelled":
                return StatusCodes.CLAIM_RESPONSE_STATUS_CODE_CANCELLED;
            case "draft":
                return StatusCodes.CLAIM_RESPONSE_STATUS_CODE_DRAFT;
            case "entered-in-error":
                return StatusCodes.CLAIM_RESPONSE_STATUS_CODE_ERROR;
            default:
                return "Unknown";
        }
    }
    private String mapUse(String fhirUseCode) {
        switch (fhirUseCode) {
            case "claim":
                return StatusCodes.CLAIM_RESPONSE_USE_CLAIM;
            case "preauthorization":
                return StatusCodes.CLAIM_RESPONSE_USE_PREAUTHORIZATION;
            case "predetermination":
                return StatusCodes.CLAIM_RESPONSE_USE_PREDETERMINATION;
            default:
                return "Unknown";
        }
    }
    private String mapOutcome(String fhirOutcomeCode) {
        switch (fhirOutcomeCode) {
            case "queued":
                return StatusCodes.CLAIM_RESPONSE_OUTCOME_QUEUED;
            case "complete":
                return StatusCodes.CLAIM_RESPONSE_OUTCOME_COMPLETE;
            case "error":
                return StatusCodes.CLAIM_RESPONSE_OUTCOME_ERROR;
            case "partial":
                return StatusCodes.CLAIM_RESPONSE_OUTCOME_PARTIAL;
            default:
                return "Unknown";
        }
    }
    private String mapPayeeType(String fhirPayeeTypeCode) {
        switch (fhirPayeeTypeCode) {
            case "subscriber":
                return StatusCodes.CLAIM_RESPONSE_PAYEE_TYPE_SUBSCRIBER;
            case "provider":
                return StatusCodes.CLAIM_RESPONSE_PAYEE_TYPE_PROVIDER;
            case "beneficiary":
                return StatusCodes.CLAIM_RESPONSE_PAYEE_TYPE_BENEFICIARY;
            case "other":
                return StatusCodes.CLAIM_RESPONSE_PAYEE_TYPE_OTHER;
            default:
                return "Unknown";
        }
    }
    private String mapAdjudicationCategory(String fhirAdjudicationCategoryCode) {
        switch (fhirAdjudicationCategoryCode) {
            case "submitted":
                return StatusCodes.ADJUDICATION_CATEGORY_SUBMITTED_AMOUNT;
            case "copay":
                return StatusCodes.ADJUDICATION_CATEGORY_COPAY;
            case "eligible":
                return StatusCodes.ADJUDICATION_CATEGORY_ELIGIBLE_AMOUNT;
            case "deductible":
                return StatusCodes.ADJUDICATION_CATEGORY_DEDUCTIBLE;
            case "unallocdeduct":
                return StatusCodes.ADJUDICATION_CATEGORY_UNALLOCATED_DEDUCTIBLE;
            case "eligpercent":
                return StatusCodes.ADJUDICATION_CATEGORY_ELIGIBLE_PERCENTAGE;
            case "tax":
                return StatusCodes.ADJUDICATION_CATEGORY_TAX;
            case "benefit":
                return StatusCodes.ADJUDICATION_CATEGORY_BENEFIT_AMOUNT;
            default:
                return "Unknown";
        }
    }

    private String mapPaymentType(String fhirPaymentTypeCode) {
        switch (fhirPaymentTypeCode) {
            case "complete":
                return StatusCodes.PAYMENT_TYPE_COMPLETE;
            case "partial":
                return StatusCodes.PAYMENT_TYPE_PARTIAL;
            default:
                return "Unknown";
        }
    }



}
