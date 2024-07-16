package com.fiyinstutorials.fhirtutorial.responseDTO;

import com.fiyinstutorials.fhirtutorial.model.Identifier;
import com.fiyinstutorials.fhirtutorial.model.Reference;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvoiceResponse {
    private String EHRCategoryTag;
    private String invoiceId;
    private String invoiceUniqueId;
    private String invoiceStatus;
    private String cancelledReason;
    private String type;
    private String subjectReference;
    private String recipientReference;
    private LocalDateTime creation;
    private LocalDate periodDate;
    private LocalDateTime periodPeriodStart;
    private LocalDateTime periodPeriodEnd;
    private Identifier issuerReference;
    private Reference accountReference;
    private int lineItemSequence;
    private String lineItemChargeItemReference;
    private String lineItemPriceComponentType;
    private String lineItemPriceComponentFactor;
    private String lineItemPriceComponentCurrency;
    private Double lineItemPriceComponentValue;
    private String totalNetCurrency;
    private Double totalNetValue;
    private String totalGrossCurrency;
    private Double totalGrossValue;
    private String paymentTerms;
    private List<IdentifierDTO> identifiers = new ArrayList<>();
}
