# STEPS IN CHARGING A PATIENT IN A HEALTHCARE SYSTEM

1. Patient Interaction:
A Patient is linked to an Account which tracks their financial transactions within the healthcare system.

2. Coverage:
Coverage is connected to the Patient and relates to their health insurance details.
It defines the benefits and accountCoverage plans for the patient.
Coverage can have accountReferences to Insurance Plan and Contract, specifying the terms of the accountCoverage.

3. Service Provision:
A healthcare service or item is provided to the patient, which generates a ChargeItem.

4. Claim Creation:
The ChargeItem is linked to a Claim, which is a request for payment for the services provided to the patient.
The Claim is sent to the Coverage provider (e.g., an insurance company) for reimbursement.

5. Claim Processing:
The Claim is processed, resulting in a Claim Response which details the adjudication results, 
such as what amount will be paid by the insurer and what amount remains the patient's responsibility.
An Explanation of Benefit (EOB) document is generated, explaining the claim adjudication results and any payments made.

6. Payment Notice:
A Payment Notice is issued to inform the healthcare provider or patient about the payment outcome based on the Claim Response.

7. Payment Reconciliation:
Payments are tracked and reconciled through Payment Reconciliation to ensure that all financial transactions match the records.


# Flow Process for Charging a Patient

1. Account Setup:
When a patient registers, an Account is created in the system.

2. Coverage Assignment:
The patient's insurance details are entered, linking Coverage to their Account.

3. Service Rendering:
The patient receives a medical service, generating a ChargeItem.

4. Claim Generation:
The ChargeItem is used to create a Claim. 
The Claim is submitted to the insurance provider specified in the Coverage.

5. Claim Adjudication:
The insurance provider processes the Claim and sends back a Claim Response. 
This response indicates the amount covered by the insurance and the remaining amount the patient needs to pay.
An Explanation of Benefit document is generated for transparency.

6. Billing:
Based on the Claim Response, the patient is billed for the remaining amount. 
The Account is updated to reflect this charge.

7. Payment Processing:
Payments from the patient and the insurance provider are tracked using Payment Notice and Payment Reconciliation to ensure that all transactions are correctly recorded and reconciled.

This flow ensures that the patient is charged correctly, insurance claims are processed efficiently, and financial records are accurately maintained within the healthcare system.


# Step-by-Step Developer Process

1. Retrieve All Patients:
Fetch all patients from the FHIR server.
Endpoint: /Patient

2. Retrieve Correlating Accounts:
For each patient, retrieve their associated accounts.
Endpoint: /Account?subject=Patient/{patientId}

3. Send Payment Notices: (TEXT2PAY FEATURE)
Create and send Payment Notices to inform patients of their payment responsibilities.
Endpoint: /PaymentNotice
The notice should include details about the invoice and payment instructions.

4. Process Patient Payments:
Record payments made by patients towards their invoices.
Endpoint: /PaymentNotice
Update the status of the payment notice and link it to the corresponding invoice and account.

5. Reconcile Payments:
Reconcile the payments to ensure they match the billed amount.
Endpoint: /PaymentReconciliation
Update the status of the invoice and account to reflect the payment received.
