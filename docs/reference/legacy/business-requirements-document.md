# Business Requirements Document (BRD)

## Academic Qualification Verification Platform (AQVP)

**Document Version:** 1.0
**System:** Academic Qualification Verification Platform (AQVP)
**Document Type:** Business Requirements Document

---

## 1. Introduction

The **Academic Qualification Verification Platform (AQVP)** is a centralized digital platform designed to reduce qualification fraud and simplify the process of issuing, storing, and verifying academic qualifications.

The platform will bring together two major groups of organizations:

1. **Organizations that need to verify qualifications**, such as employers, banks, government departments, NGOs and private companies.
2. **Organizations that issue qualifications**, such as universities, colleges, examination boards and other accredited educational institutions.

The system will therefore provide both **qualification verification** and **secure certificate generation** capabilities.

---

# 2. Business Problem

Organizations frequently receive certificates, diplomas, degrees and other academic qualifications from job applicants and employees but may have difficulty determining whether those qualifications are genuine.

Traditional verification may require contacting the issuing institution manually, which can be slow and difficult to manage.

At the same time, educational institutions need a secure mechanism for generating qualifications that can subsequently be verified electronically.

AQVP addresses these challenges by creating a trusted ecosystem in which:

**Issuing institutions generate secure qualifications → qualifications contain QR codes and security identifiers → employers or other organizations scan/upload qualifications → AQVP verifies them against authoritative records.**

---

# 3. Business Objectives

The system shall:

* provide a centralized mechanism for verifying academic qualifications;
* reduce the use of fraudulent or altered certificates;
* allow authorized educational institutions to generate secure digital and printable qualifications;
* enable verification through **QR-code scanning**;
* support verification through **uploading a certificate**;
* provide unique security identifiers for issued qualifications;
* allow institutions to import graduate/student qualification data in bulk using CSV files;
* maintain an authoritative record of qualifications issued through the platform;
* provide strong access control between issuing institutions and verification organizations;
* maintain an audit trail of important system activities.

---

# 4. High-Level System Structure

The AQVP will consist of **two major functional modules**:

| Module                                            | Primary Purpose                                         | Main Users                        |
| ------------------------------------------------- | ------------------------------------------------------- | --------------------------------- |
| **Module 1 – Verification Platform (VP)**         | Verify whether a qualification is authentic             | Primarily Read-Only organizations |
| **Module 2 – Certificate Generation Module (CG)** | Create, register and generate verifiable qualifications | Read & Write organizations        |

The two modules shall operate as part of the same platform and share the qualification verification database.

Conceptually:

**Certificate Generation Module**

Institution → Graduate Data → Qualification Record → Security Hash → QR Code → Certificate

↓

**Qualification Database**

↓

**Verification Platform**

Certificate/QR → Verification → Qualification Record → Verification Result

---

# 5. User and Administration Structure

The platform shall have the following hierarchical user structure:

```text
                    SUPER ADMIN
                         │
                         ▼
                    SYSTEM ADMIN
                         │
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
      IT ADMIN       HR MANAGER      DIRECTOR
          │              │              │
          └──── Client Organization ────┘
```

There are therefore two administrative levels.

### 5.1 Platform-Level Users

#### Super Admin

The **Super Admin** is the highest-level administrator of AQVP.

The Super Admin shall be responsible for:

* managing the entire platform;
* creating/managing System Admin accounts;
* managing global system configuration;
* approving or overseeing client organizations;
* assigning or changing organization access rights;
* managing security policies;
* viewing platform-wide audit logs;
* suspending organizations where necessary;
* monitoring overall platform operations.

#### System Admin

The **System Admin** shall perform day-to-day administration of AQVP under the authority of the Super Admin.

Responsibilities shall include:

* onboarding organizations;
* verifying registration information;
* creating or approving organization accounts;
* assigning organization types;
* supporting client administrators;
* monitoring system activity;
* managing client accounts;
* reviewing operational audit logs.

---

# 6. Client Organization Users

Each organization registered on AQVP shall have **a maximum of three active users**.

These shall be:

### 6.1 IT Administrator

Responsible primarily for the organization's technical administration.

The IT Admin may:

* manage the organization's AQVP configuration;
* manage its users;
* assist with account activation;
* manage technical settings;
* support system integration;
* view relevant technical and audit information.

### 6.2 HR Manager

The HR Manager will primarily perform operational qualification verification.

Depending on the organization's access rights, the HR Manager may:

* search qualifications;
* scan QR codes;
* upload certificates for verification;
* submit verification requests;
* view verification results;
* generate verification reports.

For educational institutions, additional certificate-management permissions may be assigned.

### 6.3 Director

The Director provides senior-level organizational oversight.

The Director may:

* view organizational activities;
* view verification reports;
* review qualification activities;
* review audit information;
* authorize sensitive actions where required.

### Business Rule

A client organization may operate with fewer than three users, but:

> **No client organization shall have more than three active client-user accounts at any given time.**

---

# 7. Organization Access Categories

AQVP shall support two major client access categories.

## 7.1 Type 1 – Read-Only Organizations

These organizations use AQVP primarily to **verify qualifications**.

Examples include:

* TM Supermarket;
* Delta;
* CBZ;
* other employers;
* banks;
* NGOs;
* government departments;
* recruitment organizations.

These organizations generally **do not issue academic qualifications**.

They shall therefore have access primarily to **Module 1 – Verification Platform**.

### Read-Only organizations can:

* scan certificate QR codes;
* upload certificates for verification;
* search permitted qualification information;
* submit verification requests;
* view verification results;
* generate permitted verification reports.

### They cannot:

* create qualifications;
* alter qualification records;
* generate official certificates;
* generate qualification QR codes;
* change graduate academic information;
* revoke qualifications.

---

# 8. Type 2 – Read & Write Organizations

Read & Write organizations are primarily recognized **educational institutions and qualification-awarding bodies**.

Examples include:

* University of Zimbabwe (UZ);
* Midlands State University (MSU);
* ZIMSEC;
* colleges;
* universities;
* examination boards;
* authorized professional certification bodies.

These organizations shall have access to:

**Module 1 – Verification Platform**

and

**Module 2 – Certificate Generation Module.**

They can therefore verify qualifications as well as create and maintain qualification records that they are authorized to issue.

---

# 9. Module 1 – Verification Platform (VP)

The **Verification Platform** is responsible for determining whether a presented qualification corresponds with an authoritative qualification record contained within AQVP.

It will primarily serve Read-Only organizations, although Read & Write institutions may also use it.

---

## 9.1 Verification Method 1 – QR Code Scanning

The user shall be able to scan the QR code appearing on a certificate.

The system shall:

1. capture the QR code;
2. decode its information/security reference;
3. identify the corresponding qualification record;
4. validate the security information;
5. retrieve the authoritative qualification information;
6. determine the verification status;
7. display the verification result.

The verification should not depend solely on the information printed inside the QR code. The QR information should be checked against the authoritative server-side qualification record.

---

# 10. Verification Method 2 – Certificate Upload

The platform shall also allow a user to:

**Upload the complete certificate for verification.**

Supported formats may include:

* PDF;
* JPG;
* JPEG;
* PNG.

The system should attempt to extract or identify relevant information from the uploaded certificate, such as:

* QR code;
* issuing institution;
* recipient;
* qualification;
* certificate/reference number;
* graduation year;
* other available identifiers.

Where a QR code is detected in the uploaded certificate, the system shall process it using the QR verification mechanism.

This allows verification even when the person conducting the verification is working from an electronic copy of the certificate.

---

# 11. Verification Result

The Verification Platform should return a clear result such as:

### VERIFIED

The certificate details correspond with a valid qualification record.

### NOT VERIFIED

No corresponding authoritative record can be established.

### DETAILS MISMATCH

A qualification record exists, but one or more supplied certificate details differ from the authoritative record.

### REVOKED/WITHDRAWN

The qualification previously existed but has subsequently been revoked or withdrawn by the issuing institution.

### REQUIRES REVIEW

The system cannot automatically establish the validity of the certificate and manual investigation is required.

---

# 12. Module 2 – Certificate Generation Module (CG)

The **Certificate Generation Module** shall be available to authorized **Read & Write institutions**.

It will allow institutions to create qualification records and generate certificates that can subsequently be verified through the Verification Platform.

The module shall contain four important components:

1. **Qualification/Certificate Template**
2. **Qualification Record Management**
3. **Security Code and QR Code Generator**
4. **CSV Bulk Data Import**

---

# 13. Certificate Template

Authorized institutions shall be able to create or configure templates for qualifications such as:

* degrees;
* diplomas;
* certificates;
* professional qualifications;
* examination certificates.

A certificate template shall support, at minimum:

| Field                     | Description                                                |
| ------------------------- | ---------------------------------------------------------- |
| Institution               | Name of the issuing institution                            |
| Institution Logo          | Official logo of the institution                           |
| Recipient Name            | Name of the qualification holder                           |
| Programme                 | Degree/diploma/certificate programme                       |
| Qualification             | Qualification awarded                                      |
| Degree/Class              | Classification or grade where applicable                   |
| Year                      | Year of award/graduation                                   |
| Certificate/Record Number | Unique qualification reference                             |
| QR Code                   | Secure verification QR code                                |
| Security Reference        | Unique system-generated security identifier where required |

Additional institutional information may be added to the template.

---

# 14. Qualification Record

Before or during certificate generation, AQVP shall create an authoritative electronic qualification record.

A typical record should contain:

```text
Issuing Institution
        +
Recipient
        +
Programme
        +
Qualification
        +
Degree/Class
        +
Year of Award
        +
Certificate/Record Number
        +
Unique Security Identifier
        =
VERIFIABLE QUALIFICATION RECORD
```

The authoritative database record becomes the primary source against which future verification requests are evaluated.

---

# 15. Unique Security Code

Each generated qualification shall receive a **cryptographically secure unique identifier**.

The initial requirement is for a **64-bit random value**.

However, for production security, I recommend using **at least 128 bits of cryptographically secure randomness**, or a standard **UUIDv4/UUIDv7 plus a cryptographic signature/hash**, rather than relying on only a 64-bit random code.

A 64-bit identifier provides about 18.4 quintillion possible values, but collision and security requirements become more important as the database grows. The identifier should therefore be generated using a cryptographically secure random number generator rather than an ordinary programming-language random function.

The identifier must be unique to the qualification record.

---

# 16. QR Code Generation

The Certificate Generation Module shall automatically generate a QR code associated with every issued qualification.

Conceptually, the QR information relates to:

```text
Institution
     +
Recipient
     +
Programme
     +
Qualification/Class
     +
Year
     +
Certificate/Record ID
     +
Security Identifier
     ↓
Secure QR Payload
     ↓
QR CODE
```

The generated QR code shall then be inserted automatically into the appropriate position on the certificate template.

### Important Security Requirement

Sensitive biodata should **not simply be stored as readable plaintext inside the QR code**.

A stronger production design is for the QR code to contain something similar to:

```text
Qualification Record ID
+
Random Security Token
+
Cryptographic Signature
+
Verification URL/Reference
```

The Verification Platform can then securely retrieve the authoritative biodata from AQVP.

This reduces exposure of personal information and makes manipulation more difficult.

---

# 17. CSV Bulk Import

Read & Write institutions shall be able to import qualification information from a **CSV file**.

This is particularly important where universities or examination boards need to register hundreds or thousands of graduates.

For example:

| Student ID | Recipient Name | Programme           | Qualification | Class | Graduation Year |
| ---------- | -------------- | ------------------- | ------------- | ----- | --------------- |
| MSU001     | Student A      | Information Systems | BCom Honours  | 2.1   | 2026            |
| MSU002     | Student B      | Computer Science    | BSc Honours   | First | 2026            |
| MSU003     | Student C      | Accounting          | BCom Honours  | 2.1   | 2026            |

After uploading the CSV, AQVP shall:

1. validate the file structure;
2. validate required fields;
3. detect invalid or duplicate records;
4. present errors for correction;
5. allow the authorized user to confirm the import;
6. create qualification records;
7. generate unique security identifiers;
8. generate the corresponding QR codes;
9. populate the certificate templates;
10. generate certificates ready for authorized issuance.

---

# 18. End-to-End Certificate Generation Process

The complete workflow should therefore be:

```text
Educational Institution
          │
          ▼
Upload CSV / Enter Graduate Manually
          │
          ▼
Validate Graduate & Qualification Data
          │
          ▼
Create Qualification Record
          │
          ▼
Generate Unique Security Identifier
          │
          ▼
Generate Secure QR Code
          │
          ▼
Merge Data into Certificate Template
          │
          ▼
Generate Certificate
          │
          ▼
Register Certificate as Issued
          │
          ▼
Qualification becomes available
for future verification
```

---

# 19. End-to-End Verification Process

When the certificate is later presented to an employer:

```text
Certificate Presented
        │
        ▼
 ┌──────────────┐
 │ Verification │
 │    Method    │
 └──────┬───────┘
        │
   ┌────┴─────┐
   ▼          ▼
Scan QR    Upload
Code        Certificate
   │          │
   └────┬─────┘
        ▼
Extract Verification Reference
        │
        ▼
Locate Qualification Record
        │
        ▼
Validate Security Information
        │
        ▼
Compare Certificate Details
with Authoritative Record
        │
        ▼
Verification Result
```

This creates the fundamental AQVP lifecycle:

> **Issue → Secure → Register → Present → Scan/Upload → Verify**

---

# 20. Access-Control Matrix

| Function                     | Read-Only | Read & Write | System Admin | Super Admin |
| ---------------------------- | :-------: | :----------: | :----------: | :---------: |
| Scan QR                      |     ✓     |       ✓      |       ✓      |      ✓      |
| Upload certificate           |     ✓     |       ✓      |       ✓      |      ✓      |
| Verify qualification         |     ✓     |       ✓      |       ✓      |      ✓      |
| View verification result     |     ✓     |       ✓      |       ✓      |      ✓      |
| Import CSV                   |     ✗     |       ✓      |    Support   |      ✓      |
| Create qualification         |     ✗     |       ✓      |    Support   |      ✓      |
| Generate security identifier |     ✗     |       ✓      |    Support   |      ✓      |
| Generate QR                  |     ✗     |       ✓      |    Support   |      ✓      |
| Generate certificate         |     ✗     |       ✓      |    Support   |      ✓      |
| Correct qualification        |     ✗     |      ✓*      |    Support   |      ✓      |
| Revoke qualification         |     ✗     |      ✓*      |    Support   |      ✓      |
| Manage organization users    |  Limited  |    Limited   |       ✓      |      ✓      |
| Change client access class   |     ✗     |       ✗      |  Authorized  |      ✓      |
| Platform configuration       |     ✗     |       ✗      |    Limited   |      ✓      |

*Only for qualifications belonging to and legitimately issued by that institution, subject to role permissions.

---

# 21. Critical Business Rules

The following rules shall apply across AQVP:

1. Every client organization shall operate within its own tenant.
2. A client shall have a maximum of **three active users: IT Admin, HR Manager and Director**.
3. Every client shall be classified as either **Read-Only** or **Read & Write**.
4. Read-Only organizations shall not create or modify authoritative qualification records.
5. Read & Write status shall only be granted to appropriately verified qualification-issuing organizations.
6. An issuing institution shall only modify qualifications belonging to its authorized issuing scope.
7. Every qualification shall have a unique system record/reference.
8. Every AQVP-generated qualification shall have a unique security identifier.
9. Every generated QR code shall be linked to the authoritative qualification record.
10. CSV imports shall be validated before qualification records are committed.
11. Duplicate qualification/security identifiers shall not be permitted.
12. Every qualification creation, correction, revocation and verification event shall be auditable.
13. Qualification records should not normally be permanently deleted; revocation or superseding should preserve the history.
14. Deactivated users shall immediately lose system access.
15. Users shall not access another client's private administrative information.

---

# 22. Audit Trail Requirements

AQVP shall maintain an audit trail recording significant activities.

For each event the system should capture:

* user;
* organization;
* user role;
* date and time;
* action performed;
* qualification affected;
* previous values where applicable;
* new values;
* verification request;
* IP/device information where appropriate.

Particularly important events include:

**Certificate created → QR generated → Certificate issued → Record modified → Certificate revoked → Certificate verified.**

---

# 23. Core Data Entities

The system will require at least the following entities:

### Organization

Represents employers, universities, examination boards and other registered clients.

### User

Represents Super Admin, System Admin, IT Admin, HR Manager and Director accounts.

### Student/Qualification Holder

Represents the individual to whom a qualification was awarded.

### Qualification

Contains programme, qualification type, classification, year and related academic information.

### Certificate

Represents the issued certificate and its associated template.

### Security Identifier

Stores the unique cryptographically generated identifier associated with a qualification.

### QR Record

Associates the QR verification mechanism with a qualification.

### Verification Request

Records an attempt by an organization to verify a qualification.

### Verification Result

Records the outcome of the verification.

### Audit Event

Records security-sensitive and business-critical activity.

---

# 24. Security Requirements

Because AQVP will hold personally identifiable and academic information, security shall be a core business requirement.

The system should provide:

* Role-Based Access Control (RBAC);
* tenant isolation;
* secure authentication;
* multi-factor authentication for privileged accounts;
* encryption in transit using HTTPS/TLS;
* encryption of sensitive information at rest;
* cryptographically secure token generation;
* digitally signed QR verification data;
* protection against QR manipulation;
* account lockout/rate limiting;
* immutable or tamper-resistant audit logs;
* secure backup and recovery;
* controlled certificate revocation;
* minimum necessary disclosure of personal information.

---

# 25. Important Design Principle: The QR Code Is Not the Certificate Database

The QR code should **identify and authenticate a qualification**, but the platform database should remain the authoritative source.

For example, if someone modifies the printed certificate from:

**BSc Computer Science – Second Class**

to

**BSc Computer Science – First Class**

the QR code may still point to the authoritative database record.

When scanned, AQVP could display:

> **VERIFIED QUALIFICATION**
> Recipient: [Name]
> Institution: [University]
> Programme: BSc Computer Science
> Classification: Second Class
> Year: 2026

The employer can immediately see that the printed information does not correspond with the authoritative record.

This is one of the central anti-fraud mechanisms of AQVP.

---

# 26. Proposed System Boundary

The resulting platform can therefore be summarized as:

```text
           ACADEMIC QUALIFICATION
          VERIFICATION PLATFORM
                    (AQVP)

     ┌─────────────────────────────┐
     │     PLATFORM MANAGEMENT     │
     │ Super Admin / System Admin  │
     └──────────────┬──────────────┘
                    │
        ┌───────────┴────────────┐
        │                        │
        ▼                        ▼
┌─────────────────┐     ┌─────────────────────┐
│ MODULE 1        │     │ MODULE 2            │
│ VERIFICATION    │     │ CERTIFICATE         │
│ PLATFORM (VP)   │     │ GENERATION (CG)     │
├─────────────────┤     ├─────────────────────┤
│ Scan QR         │     │ Certificate Template│
│ Upload Cert.    │     │ CSV Import          │
│ Verify Record   │     │ Qualification Record│
│ Verification    │     │ Security ID         │
│ Report          │     │ QR Generator        │
│                 │     │ Certificate Generator│
└────────┬────────┘     └──────────┬──────────┘
         │                         │
         └───────────┬─────────────┘
                     ▼
          ┌──────────────────────┐
          │ AUTHORITATIVE AQVP   │
          │ QUALIFICATION DATA   │
          └──────────────────────┘
```

---

# 27. Overall Business Requirement

The core business requirement can therefore be stated as:

> **AQVP shall provide a secure, multi-tenant platform through which authorized educational and qualification-awarding institutions can register and generate digitally verifiable qualifications, while employers and other authorized organizations can independently verify those qualifications by scanning a secure QR code or uploading a certificate.**

The system shall maintain a clear separation between **Read-Only verification organizations** and **Read & Write issuing institutions**, while the Super Admin and System Admin provide centralized governance and administration.

The most important relationship in the entire system is:

**Trusted Issuing Institution → Trusted Qualification Record → Secure QR/Certificate → Independent Verification.**

This provides a strong foundation for the next documents: the **SRS**, detailed **use cases**, **functional/non-functional requirements**, **ERD/database design**, and **system architecture**.
