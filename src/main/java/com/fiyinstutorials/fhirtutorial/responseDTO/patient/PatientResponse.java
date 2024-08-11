package com.fiyinstutorials.fhirtutorial.responseDTO.patient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiyinstutorials.fhirtutorial.responseDTO.IdentifierDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientResponse {
    private String patientId;
    private String patientUniqueId;
    private String firstName;
    private String middleName;
    private String nickName;
    private String lastName;
    private String phone;
    private String email;
    private String gender;
    private String birthDate;
    private Boolean deceased;
    private String address;
    private String city;
    private String district;
    private String state;
    private String postalCode;
    private String country;
    private String maritalStatus;
    private String patientLanguage;
    private Boolean patientPreferredLanguage;
    private String generalPractitioner;
    private String managingOrganization;
    private List<IdentifierDTO> identifiers = new ArrayList<>();
}
