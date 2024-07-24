package com.fiyinstutorials.fhirtutorial.controller;

import com.fiyinstutorials.fhirtutorial.responseDTO.AccountResponse;
import com.fiyinstutorials.fhirtutorial.responseDTO.CRResponse;
import com.fiyinstutorials.fhirtutorial.responseDTO.PatientClaimResponse;
import com.fiyinstutorials.fhirtutorial.responseDTO.PatientResponse;
import com.fiyinstutorials.fhirtutorial.service.AccountService;
import com.fiyinstutorials.fhirtutorial.service.ClaimResponseService;
import com.fiyinstutorials.fhirtutorial.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hapi")
public class HapiController {

    private final PatientService patientService;
    private final AccountService accountService;
//    private final PaymentNoticeService paymentNoticeService;
    private final ClaimResponseService claimResponseService;


    @Autowired
    public HapiController(PatientService patientService, AccountService accountService, ClaimResponseService claimResponseService) {
        this.patientService = patientService;
        this.accountService = accountService;
//        this.paymentNoticeService = paymentNoticeService;
        this.claimResponseService = claimResponseService;
    }

    @GetMapping("/getAllPatients")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<PatientResponse> patients = patientService.getAllPatients();
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/getPatientAccounts")
    public ResponseEntity<List<AccountResponse>> fetchAndSaveAccounts() {
        List<AccountResponse> accountResponses = accountService.fetchAndSaveAccounts();
        return ResponseEntity.ok(accountResponses);
    }

    @GetMapping("/getClaimResponses")
    public ResponseEntity<List<CRResponse>> getClaimResponses() {
        try {
            List<CRResponse> claimResponses = claimResponseService.getClaimResponses();
            return ResponseEntity.ok(claimResponses);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/saveClaimResponses")
    public ResponseEntity<String> saveClaimResponses() {
        try {
            claimResponseService.fetchAndSaveClaimResponses();
            return ResponseEntity.ok("Claim responses have been fetched and saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while fetching and saving claim responses.");
        }
    }

    @GetMapping("/getPatientClaimResponse")
    public List<PatientClaimResponse> getAllPatientClaimResponses() {
        return claimResponseService.getAllPatientClaimResponses();
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

}
