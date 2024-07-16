SMART APP
=========

## [Introduction](http://hl7.org/fhir/smart-app-launch/)
- SMART on FHIR is a set of open specifications to integrate apps with Electronic Health Records (EHRs), portals, Health Information Exchanges (HIEs), and other Health IT systems.
- SMART on FHIR apps are launched from within the EHR context and can access patient data from the EHR.
- based on the FHIR standard and OAuth2.0 protocol.
- maintained by the HL7 organization.


[SMART Project](https://smarthealthit.org/)
- SMART Health IT is an open, standards-based technology platform that enables innovators to create apps that seamlessly and securely run across the healthcare system.
- this website contains the launch, tester and environments for development of SMART on FHIR apps.
- It acts as a standardized layer that protects health information rather than pulling from the EHR server directly
- and acts as a standard way to create an application that can log into any FHIR without worrying about protocols.

[Overview](http://hl7.org/fhir/smart-app-launch/toc.html)
- This implementation guide describes a set of foundational patterns based on OAuth 2.0 for client applications to authorize, authenticate, and integrate with FHIR-based data systems.

[App Launch Framework](http://hl7.org/fhir/smart-app-launch/app-launch.html)
- The SMART App Launch Framework connects third-party applications to Electronic Health Record data, allowing apps to launch from inside or outside the user interface of an EHR system.
- The framework supports apps for use by clinicians, 
  - patients,
  - and others via a PHR, Patient Portal, 
  - or any FHIR system where a user can launch an app. 

- It provides a reliable, secure authorization protocol for a variety of app architectures, 
  - including apps that run on an end-user’s device as well as apps that run on a secure server.

Profiling Scope
==============================

- This profile on OAuth 2.0 is intended to be used by developers of apps that need to access user identity information or other FHIR resources by requesting authorization from OAuth 2.0-compliant authorization servers.
- This profile provides a mechanism to delegate an entity’s permissions (e.g., a user’s permissions) to a 3rd-party app. 

## Within this profile we differentiate between the two types of apps defined in the OAuth 2.0 specification: 
    - confidential
    - and public. 
- The differentiation is based upon whether the execution environment within which the app runs enables the app to protect secrets. 
- Pure client-side apps (for example, HTML5/JS browser-based apps, iOS mobile apps, or Windows desktop apps) can provide adequate security, but they may be unable to “keep a secret” in the OAuth2 sense. 
- In other words, any “secret” key, code, or string that is statically embedded in the app can potentially be extracted by an end-user or attacker. 
- Hence, security for these apps cannot depend on secrets embedded at install-time.

- To determine the appropriate app type, first answer the question “is your app able to protect a secret?”
    If “Yes”, use a confidential app
    Example: App runs on a trusted server with only server-side access to the secret

  - If “No”, use a public app 
  - Example: App is an HTML5 or JS in-browser app (including single-page applications) that would expose the secret in user space

SMART AUTHORIZATION AND ACCESS
==============================
- An app can launch from within an existing session in an EHR, Patient Portal, or other FHIR system. 
- Alternatively, it can launch as a standalone app.

- In an EHR launch, 
  - (Actor): You are a medical provider logged in to an EHR system, using application A, and you want to use a different application B for something else as an extra functionality,
  - The provider doesnt have to go through the authorization process from the start because they are logged into an EHR session.
  
- In a standalone launch, when the app launches from outside an EHR session, the app can request context from the EHR authorization server. 
  - The context will then be determined during the authorization process.

STEPS FOR SMART APP LAUNCH
==========================
1. Register App with EHR (one-time step)
2. Launch App: (Standalone Launch or EHR Launch)
3. Retrieve (.well-known/smart-configuration)
4. Obtain authorization code
5. Obtain access token
6. Access FHIR API
7. Refresh access token


