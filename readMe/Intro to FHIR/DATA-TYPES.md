# [Data Types](http://hl7.org/fhir/datatypes.html)
- Core FHIR data types are used to define the specifications and content of resources.
- There are 4 different types of data types:
    - Primitive Data Types:  start with a lowercase letter and represent the simplest data types.
    - General-Purpose Data Types: start with an uppercase letter and represent more complex data types.
    - Special Data Types
    - Metadata Data Types

- Each data type has a specific structure and purpose, some being inherited models

# [Extensions](https://hl7.org/fhir/extensibility.html)
- Note: Every Element has an Extension element consisting of a (URL, Value) pair that can be used to add additional information to the element 
- This is outside the extension methods already provided for each resource for more flexibility in data representation.

# [Serialization](https://hl7.org/fhir/json.html)
- FHIR resources can be serialized in either XML, JSON, or RDF format.
  - FHIR serialization formats follow a specific structure and rules to ensure data consistency and interoperability.
    {
    "resourceType" : "[Resource Type]",
    "property1" : "<[primitive]>",  --- primitive datatype; the value of the property will be as described for the stated type
    "property2" : { [Datatype] },  --- complex datatype; the value of the property will be a JSON object with the structure as described for the stated type
    "property3" : {                --- object property that contains additional properties (e.g. propertyA;)
         "propertyA" : { CodeableConcept }, ---- example of an object property that has a binding to a value set
    },
    "property4" : [{                ---- array property that contains items which are objects themselves.
        "propertyB" : { Reference(ResourceType) } ---- example of an object property in an array that contains accountReferences to other resources
    }]
    }
  - 
- The MIME-type for this format is **"application/fhir+json."** to place in the header of the request or response.