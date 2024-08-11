package com.fiyinstutorials.fhirtutorial.responseDTO.patient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiyinstutorials.fhirtutorial.responseDTO.CodingDTO;
import com.fiyinstutorials.fhirtutorial.responseDTO.claimresponse.CRResponseAdjudicationDTO;
import com.fiyinstutorials.fhirtutorial.responseDTO.claimresponse.CRResponseItemDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientCRResponse {
    //Patient Text2Pay Info
    private String patientUniqueId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String gender;
    private String birthDate;
    private String generalPractitioner;
    private String managingOrganization;

    //Patient Fiserv Info
    private String address;
    private String city;
    private String district;
    private String state;
    private String postalCode;
    private String country;

    //ClaimResponse Info
    private String patientReference;
    private String claimResponseUniqueId;
    private String claimResponseStatus;
    private String claimResponseUse;
    private String claimResponseOutcome;
    private String claimResponseDisposition;
    private List<CRResponseItemDTO> item = new ArrayList<>();
    private List<CRResponseAdjudicationDTO> total = new ArrayList<>();
    private List<CodingDTO> paymentType = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date paymentDate;
    private String paymentAmountCurrency;
    private Double paymentAmountValue;

}
