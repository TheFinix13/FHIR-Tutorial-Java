package com.fiyinstutorials.fhirtutorial.utils;

public class StatusCodes {
    public static final String ACCOUNT_STATUS_ACTIVE = "active"; //This account is active and may be used.
    public static final String ACCOUNT_STATUS_INACTIVE = "inactive"; //This account is inactive and should not be used to track financial information.
    public static final String ACCOUNT_STATUS_ERROR = "entered-in-error"; //This instance should not have been part of this patient's medical record.
    public static final String ACCOUNT_STATUS_ON_HOLD = "on-hold"; //This account is on hold.
    public static final String ACCOUNT_STATUS_UNKNOWN = "unknown"; //The account status is unknown.

    //(account.status is active)
    public static final String BILLING_STATUS_CODE_OPEN = "Open"; //The account is open for charging transactions
    public static final String BILLING_STATUS_CODE_CARE_COMPLETED_NOT_BILLED = "CareComplete/Not Billed"; //The account is still active and may have charges recorded against it (only for events in the servicePeriod),
    public static final String BILLING_STATUS_CODE_BILLING = "Billing"; //Indicates that all transactions are recorded and the finance system can perform the billing process, including preparing insurance claims, scrubbing charges, invoicing etc. During this time any new charges will not be included in the current billing run/cycle.
   //(account.status is in-active)
    public static final String BILLING_STATUS_CODE_CLOSED_BAD_DEBT = "Closed-Bad Debt"; //The balance of this debt has not been able to be recovered, and the organization has decided not to pursue debt recovery.
    public static final String BILLING_STATUS_CODE_CLOSED_VOIDED = "Closed-Voided"; //The account was not created in error, however the organization has decided that it will not be charging any transactions associated.
    public static final String BILLING_STATUS_CODE_CLOSED_COMPLETED = "Closed-Completed"; //The account is closed and all charges are processed and accounted for.
    public static final String BILLING_STATUS_CODE_CLOSED_COMBINED = "Closed-Combined"; //This account has been merged into another account, all charged have been migrated. This account should no longer be used, and will not be billed.

    public static final String CLAIM_PRIORITY_CODE_STAT = "Immediate";
    public static final String CLAIM_PRIORITY_CODE_NORMAL = "Normal";
    public static final String CLAIM_PRIORITY_CODE_DEFERRED = "Deferred";

    public static final String CLAIM_PAYEE_CODE_SUBSCRIBER = "Subscriber";
    public static final String CLAIM_PAYEE_CODE_PROVIDER = "Provider";
    public static final String CLAIM_PAYEE_CODE_BENEFICIARY = "Beneficiary";
    public static final String CLAIM_PAYEE_CODE_OTHER = "other";

    public static final String PAYMENT_NOTICE_CODE_ACTIVE = "Active";
    public static final String PAYMENT_NOTICE_CODE_CANCELLED = "Cancelled";
    public static final String PAYMENT_NOTICE_CODE_DRAFT = "Draft";
    public static final String PAYMENT_NOTICE_CODE_ERROR = "Entered in Error";

    public static final String PAYMENT_STATUS_CODE_PAID = "Paid";//The payment has been sent physically or electronically.
    public static final String PAYMENT_STATUS_CODE_CLEARED = "Cleared";//The payment has been received by the payee.

    public static final String CHARGE_ITEM_STATUS_CODE_PLANNED = "Planned";//The charge item has been entered, but the charged service is not yet complete, so it shall not be billed yet
    public static final String CHARGE_ITEM_STATUS_CODE_BILLABLE = "Billable";//The charge item is ready for billing.
    public static final String CHARGE_ITEM_STATUS_CODE_NOT_BILLABLE = "Not Billable";//The charge item has been determined to be not billable
    public static final String CHARGE_ITEM_STATUS_CODE_ABORTED = "Aborted";//The processing of the charge was aborted.
    public static final String CHARGE_ITEM_STATUS_CODE_BILLED = "Billed";//The charge item has been billed.
    public static final String CHARGE_ITEM_STATUS_CODE_ERROR = "Entered in Error";//The charge item has been entered in error and should not be processed for billing
    public static final String CHARGE_ITEM_STATUS_CODE_UNKNOWN = "Unknown";//The authoring system does not know which of the status values currently applies for this charge item

    public static final String TOTAL_PRICE_COMPONENT_TYPE_BASE = "base price"; //The amount is the base price used for calculating the total price before applying surcharges, discount or taxes.
    public static final String TOTAL_PRICE_COMPONENT_TYPE_SURCHARGE = "Surcharge"; //The amount that increases the cost of the item.
    public static final String TOTAL_PRICE_COMPONENT_TYPE_DISCOUNT = "Discount"; //The amount that reduces the cost of the item.
    public static final String TOTAL_PRICE_COMPONENT_TYPE_TAX = "Tax"; //The amount that is levied by a government on the item.
    public static final String TOTAL_PRICE_COMPONENT_TYPE_INFORMATIONAL = "Informational"; //The amount is of informational character, it has not been applied in the calculation of the total price.

    public static final String INVOICE_STATUS_DRAFT = "draft"; //the invoice has been prepared but not yet finalized.
    public static final String INVOICE_STATUS_ISSUED = "issued"; //the invoice has been finalized and sent to the recipient.
    public static final String INVOICE_STATUS_BALANCED = "balanced"; //the invoice has been balanced / completely paid
    public static final String INVOICE_STATUS_CANCELLED = "cancelled"; //the invoice was cancelled.
    public static final String INVOICE_STATUS_ERROR = "entered_in_error"; //the invoice was determined as entered in error before it was issued.

    public static final String RESOURCE_TYPE_PATIENT = "PATIENT RESOURCE";
    public static final String RESOURCE_TYPE_ACCOUNT = "ACCOUNT RESOURCE";
    public static final String RESOURCE_TYPE_CHARGE_ITEM = "CHARGE-ITEM RESOURCE";
    public static final String RESOURCE_TYPE_COVERAGE = "COVERAGE RESOURCE";
    public static final String RESOURCE_TYPE_PAYMENT_NOTICE = "PAYMENT-NOTICE RESOURCE";
    public static final String RESOURCE_TYPE_INVOICE = "INVOICE RESOURCE";
    public static final String RESOURCE_TYPE_CLAIM = "CLAIM RESOURCE";

    public static final String CLAIM_RESPONSE_STATUS_CODE_ACTIVE = "Active";  //The instance is currently in-force.
    public static final String CLAIM_RESPONSE_STATUS_CODE_CANCELLED = "Cancelled";  //The instance is withdrawn, rescinded or reversed.
    public static final String CLAIM_RESPONSE_STATUS_CODE_DRAFT = "Draft";  //A new instance the contents of which is not complete.
    public static final String CLAIM_RESPONSE_STATUS_CODE_ERROR = "Entered in Error";  //The instance was entered in error

  public static final String CLAIM_RESPONSE_USE_CLAIM = "Claim";  // The treatment is complete and this represents a Claim for the services.
  public static final String CLAIM_RESPONSE_USE_PREAUTHORIZATION = "Preauthorization";  // The treatment is proposed and this represents a Pre-authorization for the services.
  public static final String CLAIM_RESPONSE_USE_PREDETERMINATION = "Predetermination";  // The treatment is proposed and this represents a Pre-determination for the services.

    public static final String CLAIM_RESPONSE_OUTCOME_QUEUED = "Queued";  // The Claim/Pre-authorization/Pre-determination has been received but processing has not begun.
    public static final String CLAIM_RESPONSE_OUTCOME_COMPLETE = "Complete";  // The processing has completed without errors.
    public static final String CLAIM_RESPONSE_OUTCOME_ERROR = "Error";  // One or more errors have been detected in the Claim.
    public static final String CLAIM_RESPONSE_OUTCOME_PARTIAL = "Partial";  // No errors have been detected in the Claim and some of the adjudication has been performed.

    public static final String CLAIM_RESPONSE_PAYEE_TYPE_SUBSCRIBER = "Subscriber";  // The subscriber (policy holder) will be reimbursed.
    public static final String CLAIM_RESPONSE_PAYEE_TYPE_PROVIDER = "Provider";  // Any benefit payable will be paid to the provider (Assignment of Benefit).
    public static final String CLAIM_RESPONSE_PAYEE_TYPE_BENEFICIARY = "Beneficiary";  // The beneficiary (patient) will be reimbursed.
    public static final String CLAIM_RESPONSE_PAYEE_TYPE_OTHER = "Other";  // Any benefit payable will be paid to a third party such as a guarantor.

    public static final String ADJUDICATION_CATEGORY_SUBMITTED_AMOUNT = "submitted";  // The total submitted amount for the claim or group or line item.
    public static final String ADJUDICATION_CATEGORY_COPAY = "copay";  // Patient Co-Payment
    public static final String ADJUDICATION_CATEGORY_ELIGIBLE_AMOUNT = "eligible";  // Amount of the change which is considered for adjudication.
    public static final String ADJUDICATION_CATEGORY_DEDUCTIBLE = "deductible";  // Amount deducted from the eligible amount prior to adjudication.
    public static final String ADJUDICATION_CATEGORY_UNALLOCATED_DEDUCTIBLE = "unallocdeduct";  // The amount of deductible which could not be allocated to other line items.
    public static final String ADJUDICATION_CATEGORY_ELIGIBLE_PERCENTAGE = "eligpercent";  // Eligible Percentage.
    public static final String ADJUDICATION_CATEGORY_TAX = "tax";  // The amount of tax.
    public static final String ADJUDICATION_CATEGORY_BENEFIT_AMOUNT = "benefit";  // Amount payable under the coverage.

    public static final String PAYMENT_TYPE_COMPLETE = "Complete";  // Complete (final) payment of the benefit under the Claim less any adjustments.
    public static final String PAYMENT_TYPE_PARTIAL = "Partial";  // Partial payment of the benefit under the Claim less any adjustments.





}
