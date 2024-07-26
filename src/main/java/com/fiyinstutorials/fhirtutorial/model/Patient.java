package com.fiyinstutorials.fhirtutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "patient_id")
    private Long id;

    @Column(name = "patient_server_id")
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

    @CreationTimestamp
    @ApiModelProperty(hidden = true)
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @ApiModelProperty(hidden = true)
    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Identifier> identifiers;

}


