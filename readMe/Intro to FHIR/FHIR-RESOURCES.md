
## FHIR Resources
[Patient Resource](https://hl7.org/fhir/patient.html)
1. Resource Content
   When checking any resource e.g. the patient resource, You'll notice a few headers such as:
- The Patient Administrator/[Workgroup](http://www.hl7.org/Special/committees/pafm/index.cfm): This is the group responsible for the resource.
- The maturity level
- The security Category
- and The [compartments](https://hl7.org/fhir/compartmentdefinition-patient.html): to organize and bundle resources logically for comprehensive views and efficient use.

2. Resource References
- For developers, This displays other resources that the patient resource can:
    - ##### either implement
    - ##### be referenced by or associated with.
    - ##### and extensional accountReferences.
These extensions provide additional data elements or relationships that can be attached

3. Resource Structure
- The structure of the resource is displayed in a tree-like structure, showing:
    - the field names
    - the field cardinalities - most are optional to emphasize real-world data variability in the health care domain.
        - (e.g. _0..1_ - 0 or 1,_0..*_ - 0 or more, _1..1_ - exactly 1, _1..*_ - 1 or more)

    - the field data types.
    - the field descriptions.

4. Resource Search Params
- This section lists the search parameters that can be used to query the resource explaining how to request specific data from a server to avoid complexity in data retrieval processes.
- (e.g. Rather than going into the address field of the patient resource, you can simply query (_patient.address.city, patient.address.state, patient.address.postalcode_) to get the address details.)
- search parameters either return "String, token, accountReference, date" etc. data types.

Click here to view a Patient example in either XML or JSON format:
# [Patient Example](https://hl7.org/fhir/patient-example.html)

5. Resource Profiles & Extensions
- Beyond the core standard, there is a format for defining additional fields needed for developers
- (E.g. _patient.birthplace,_ is a field that is not part of the core standard but can be added as an extension through the standard extension mechanism.)
- This section lists the profiles and extensions that can be used to extend the resource.


