package com.fiyinstutorials.fhirtutorial.controller;

import com.fiyinstutorials.fhirtutorial.responseDTO.account.AccountResponse;
import com.fiyinstutorials.fhirtutorial.responseDTO.claimresponse.CRResponse;
import com.fiyinstutorials.fhirtutorial.responseDTO.patient.PatientCRResponse;
import com.fiyinstutorials.fhirtutorial.responseDTO.patient.PatientResponse;
import com.fiyinstutorials.fhirtutorial.responseDTO.paymentnotice.PaymentNoticeResponse;
import com.fiyinstutorials.fhirtutorial.service.AccountService;
import com.fiyinstutorials.fhirtutorial.service.ClaimResponseService;
import com.fiyinstutorials.fhirtutorial.service.PatientService;
import com.fiyinstutorials.fhirtutorial.service.PaymentNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hapi")
public class HapiController {

    private final PatientService patientService;
    private final AccountService accountService;
    private final PaymentNoticeService paymentNoticeService;
    private final ClaimResponseService claimResponseService;


    @Autowired
    public HapiController(PatientService patientService, AccountService accountService, ClaimResponseService claimResponseService, PaymentNoticeService paymentNoticeService) {
        this.patientService = patientService;
        this.accountService = accountService;
        this.paymentNoticeService = paymentNoticeService;
        this.claimResponseService = claimResponseService;
    }
    /**
     * Retrieves all patient resources from the fhir servers.
     *
     * @return A ResponseEntity containing a list of PatientResponse objects if found, or NO_CONTENT if no patients are found.
     */
    @GetMapping("/getAllPatients")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<PatientResponse> patients = patientService.getAllPatients();
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    /**
     * Fetches and saves account details and retrieves the list of AccountResponse objects.
     *
     * @return A ResponseEntity containing a list of AccountResponse objects.
     */
    @GetMapping("/getPatientAccounts")
    public ResponseEntity<List<AccountResponse>> fetchAndSaveAccounts() {
        List<AccountResponse> accountResponses = accountService.fetchAndSaveAccounts();
        return ResponseEntity.ok(accountResponses);
    }

    /**
     * Retrieves all ClaimResponse resource from fhir servers.
     *
     * @return A ResponseEntity containing a list of CRResponse objects if successful, or an error status if an exception occurs.
     */
    @GetMapping("/getClaimResponses")
    public ResponseEntity<List<CRResponse>> getClaimResponses() {
        try {
            List<CRResponse> claimResponses = claimResponseService.getClaimResponses();
            return ResponseEntity.ok(claimResponses);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Fetches and saves ClaimResponse objects to database.
     *
     * @return A ResponseEntity with a success message if successful, or an error message if an exception occurs.
     */
    @PostMapping("/saveClaimResponses")
    public ResponseEntity<String> saveClaimResponses() {
        try {
            claimResponseService.fetchAndSaveClaimResponses();
            return ResponseEntity.ok("Claim responses have been fetched and saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while fetching and saving claim responses.");
        }
    }

    /**
     * Retrieves ClaimResponse belonging to Patients from fhir servers
     * and retrieves all PatientClaimResponse objects.
     *
     * @return A list of PatientCRResponse objects.
     */
    @GetMapping("/getAllPatientClaimResponse")
    public List<PatientCRResponse> getAllPatientClaimResponses() {
        return claimResponseService.getAllPatientClaimResponses();
    }

    /**
     * Endpoint to get a ClaimResponse for a specific patient ID.
     *
     * @param patientId The ID of the patient
     * @return ResponseEntity containing the PatientClaimResponse object
     */
    @GetMapping("/getPatientClaimResponse/{patientId}")
    public ResponseEntity<PatientCRResponse> getClaimResponseByPatientId(@PathVariable("patientId") String patientId) {

        PatientCRResponse patientClaimResponse = claimResponseService.getClaimResponseByPatientId(patientId);

        if (patientClaimResponse != null) {
            return new ResponseEntity<>(patientClaimResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getAClaimResponse/{id}")
    public ResponseEntity<String> updateClaimResponse(@PathVariable("id") String id) {
        try {
            claimResponseService.getAClaimResponse(id);
            return ResponseEntity.ok("ClaimResponse fetched successfully.");
        } catch (Exception e) {
            log.error("Error retrieving ClaimResponse {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving ClaimResponse");
        }
    }

    @PutMapping("/updateClaimResponseOutcome/{id}")
    public ResponseEntity<String> updateClaimResponse(
            @PathVariable("id") String id,
            @RequestParam(value = "outcome", required = false) String newOutcome,
            @RequestParam(value = "paymentType", required = false) List<String> newPaymentTypes) {

        try {
            claimResponseService.updateAClaimResponse(id, newOutcome, newPaymentTypes);
            return ResponseEntity.ok("ClaimResponse updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating ClaimResponse: " + e.getMessage());
        }
    }

    @GetMapping("/getPaymentNotice")
    public List<PaymentNoticeResponse> fetchAllPaymentNotices() {
        List<PaymentNoticeResponse> paymentNoticeResponses = paymentNoticeService.getAllPaymentNotices();
        if (paymentNoticeResponses.isEmpty()) {
            System.out.println("No Payment Notices found.");
        } else {
            System.out.println("Number of Payment Notices fetched: " + paymentNoticeResponses.size());
            paymentNoticeResponses.forEach(response -> System.out.println("PaymentNotice ID: " + response.getPaymentNoticeId()));
        }
        return paymentNoticeResponses;
    }

    @PostMapping("/savePaymentNotice")
    public String savePaymentNotices() {
        try {
            paymentNoticeService.savePaymentNotices();
            return "Payment Notices have been saved successfully.";
        } catch (Exception e) {
            log.error("Error saving Payment Notices: {}", e.getMessage(), e);
            return "An error occurred while saving Payment Notices.";
        }
    }


}


//    @GetMapping("/getPatientPaymentNotice")
//    public ResponseEntity<List<PaymentNoticeResponse>> fetchAndSavePaymentNotices() {
//        List<PaymentNoticeResponse> paymentNoticeResponses = paymentNoticeService.fetchAndSavePaymentNotices();
//        return ResponseEntity.ok(paymentNoticeResponses);
//    }


//    @PutMapping("/updatePatients")
//    public ResponseEntity<List<PatientUpdateResponse>> updatePatients() {
//        List<PatientUpdateResponse> updateResponses = hapiService.updatePatients();
//        return ResponseEntity.ok(updateResponses);
//    }


