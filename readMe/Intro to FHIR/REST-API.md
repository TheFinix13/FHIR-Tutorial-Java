# [Interactions(Rest API)](http://hl7.org/fhir/http.html)
- FHIR resources can be accessed through RESTful APIs.
- The FHIR standard defines 3 sets of interactions that can be performed on resources.
  - Whole system interactions
  - Type Level interactions
  - Instance Level interactions

# Whole System Interactions
- ## At the base URL of the server you'll have operations like 
  - [CAPABILITIES:](https://hl7.org/fhir/http.html#capabilities) : getting server capabilities or support
  - [SEARCH:](https://hl7.org/fhir/http.html#search): search across resources on the server
  - [BATCH/TRANSACTIONS](https://hl7.org/fhir/http.html#transaction): multiple updates or creates in the server
  - [HISTORY](https://hl7.org/fhir/http.html#history) 
  - [DELETE](https://hl7.org/fhir/http.html#cdelete) operations, 

- These define interactions for the entire system.

# Type Level Interactions
- ## based on resources like patient records, imaging studies, and care plans, specifying interactions at a resource type level.
  - [CREATE](https://hl7.org/fhir/http.html#create): to create a new resource when no id is provided
  - [SEARCH](https://hl7.org/fhir/http.html#search): to search for resources of a specific type when non criteria is provided
  - [DELETE](https://hl7.org/fhir/http.html#cdelete): to delete across a particular resource
  - [HISTORY](https://hl7.org/fhir/http.html#history): to retrieve the change history of a resource

# Instance Level Interactions
- ## involve specific resource instances with IDs, allowing actions like reading, updating, and deleting individual records within the system.
  - [READ](https://hl7.org/fhir/http.html#read): to read a specific resource instance by id
  - [VREAD](https://hl7.org/fhir/http.html#vread): to read a specific version of a resource instance
  - [UPDATE](https://hl7.org/fhir/http.html#update): to update a specific resource instance by id
  - [PATCH](https://hl7.org/fhir/http.html#patch): to update a specific resource instance by posting changes
  - [DELETE](https://hl7.org/fhir/http.html#delete): to delete a specific resource instance
  - [HISTORY](https://hl7.org/fhir/http.html#history): to retrieve the change history of a resource instance

# Style Guide of Constructing the URL
VERB [base]/[type]/[id] {?_format=[mime-type]} 
  - [base](https://hl7.org/fhir/http.html#root): The Service Base URL
    type: The name of a resource type (e.g. "Patient")
    id: The Logical Id of a resource
    vid: The Version Id of a resource
    [mime-type](https://hl7.org/fhir/http.html#mime-type): application/fhir+xml or application/fhir+json in the Accept headers
    parameters: URL parameters as defined for the particular interaction 
    Content surrounded by {} is optional

# HTTP headers
- ## This specification makes use of several HTTP headers to change the processing or format of requests or results.
  - Accept: (request)	
    - Content-negotiation for MIME Type (Content Types and Encodings)
    - and FHIR Version (FHIR Version Parameter)
    - and General Parameters (_format).
  
  - ETag: (response) 
    - The value from .meta.versionId as a "weak" ETag, 
    - prefixed with W/ and enclosed in quotes (e.g., W/"3141").
  
  - If-Match: (request)
    - ETag-based matching for conditional requests, (Conditional Read, Conditional Update, Conditional Patch)
    - Managing Resource Contention, 
    - and Support for Versions.

  - If-Modified-Since (request)	
    - Date-based matching for conditional read requests, (Conditional Read)
    
  - If-None-Exist (request)
    - HL7 defined extension header to prevent the creation of duplicate resources, (Conditional Create)
  
  - If-None-Match (request)	
    - ETag-based matching for conditional requests, (Conditional Read and Conditional Update.)

  - Last-Modified (response)	
    - The value from .meta.lastUpdated, which is a FHIR instant, converted to the proper format.
    
  - Prefer (request)	
    - Request various behaviors specific to a single request,
  
  - Location (response)	
    - Used in the response to a CREATE/UPSERT Request to indicate where the resource can be found after being processed.
    
  - Content-Location (response)
    - Used in the Async pattern to indicate where the response to the request can be found.

## General parameters
- ## These are params you can add to the request to give you the specific responses you desire
**(_format)** - this Overrides the HTTP content negotiation (Accept Headers)
  - This specifies alternative response formats by their MIME-types. 
  - This parameter allows a client to override the accept header value when it is unable to set it correctly due to internal limitations (e.g. XSLT usage). 
  - For the _format parameter, the codes json, application/json and application/fhir+json SHALL be interpreted to mean the JSON format,

**Implementation Notes:**
- If you provide a generic mime type in the Accept header (application/json), the server SHOULD respond with the mime type, using the JSON formats described in this specification as the best representation for the named mime type (though see the note on the [Binary resource](https://hl7.org/fhir/binary.html#rest)). 
- the _format parameter does not override the Content-Type header for the type of the body of a POST request. 
- If neither the accept header nor the _format parameter are specified, the MIME-type of the content returned by the server is undefined and may vary


**(_pretty)** - Ask for a pretty printed response for human convenience
- Clients that wish to request for pretty-printed resources in JSON can use the _pretty parameter:
- Example request:
  - GET [base]/Patient/example?_pretty=true
  

(**_summary**) - Ask for a predefined short form of the resource in response
- Example request:
  - GET [base]/ValueSet?_summary=true
  
- The _summary parameter requests the server to return a subset of the resource. It can contain one of the following values:
  - (true) - Return a limited subset of elements from the resource.
    - This subset SHOULD consist solely of all supported elements that are marked as "summary" in the base definition of the resource(s) 
    - (see [ElementDefinition.isSummary](https://hl7.org/fhir/elementdefinition-definitions.html#ElementDefinition.isSummary))
  
  - (text) - Return only the text, id, meta, and top-level mandatory elements 
    - these mandatory elements are included to ensure that the payload is valid FHIR.
  
  - (data) - Remove the text element
  - (count)	_Search only_: just return a count of the matching resources, without returning the actual matches
  - (false) - Return all parts of the resource(s)

 - The intent of the _summary parameter is to reduce the total processing load on server, client, and resources between them such as the network. 
 - It is most useful for resources that are large, particularly ones that include images or elements that may repeat many times. 
 - The summary form allows a client to quickly retrieve a large set of resources, and let a user pick the appropriate one. 
 - The summary for an element is defined to allow a user to quickly sort and filter the resources.

(**_elements**) - Ask for a particular set of elements to be returned
- Indicates that the resource(s) in the response should only include the enumerated elements (plus any mandatory or modifier elements).
- Example Request:
  - GET [base]/Patient?_elements=identify,active,link

# Preferred returns
- There are different types of returns that a server can provide. 
  - Prefer: return=minimal --- return no body
    Prefer: return=representation --- return the full resource
    Prefer: return=OperationOutcome --- asks the server to return an [OperationOutcome](https://hl7.org/fhir/operationoutcome.html) resource containing hints and warnings about the operation rather than the full resource.
