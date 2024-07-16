package com.fiyinstutorials.fhirtutorial.hapi;

import com.fiyinstutorials.fhirtutorial.responseDTO.AccountResponse;
import com.fiyinstutorials.fhirtutorial.responseDTO.PatientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hapi")
public class hapiController {

    @Autowired
    private HapiPatientService hapiPatientService;
    @Autowired
    private HapiAccountService hapiAccountService;

    @GetMapping("/getAllPatients")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<PatientResponse> patients = hapiPatientService.getAllPatients();
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/getPatientAccounts")
    public ResponseEntity<List<AccountResponse>> getPatientAccounts() {
        List<AccountResponse> accounts = hapiAccountService.getPatientAccounts();
        if (accounts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }


//    @PutMapping("/updatePatients")
//    public ResponseEntity<List<PatientUpdateResponse>> updatePatients() {
//        List<PatientUpdateResponse> updateResponses = hapiService.updatePatients();
//        return ResponseEntity.ok(updateResponses);
//    }

}
