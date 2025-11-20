Digital Envelope – Architecture Overview

This document provides a technical overview of the architecture behind the Digital Envelope Android Application, which implements a hybrid cryptographic system for secure file encryption and decryption.

📁 Project Structure

The application follows a modular Android architecture with clear separation between:

UI Activities (user interaction)

Utility Classes (cryptography + I/O)

Data Models (encrypted envelope format)

/app
 ├── activities/
 │    ├── SplashActivity.java
 │    ├── MainActivity.java
 │    ├── EncryptActivity.java
 │    └── DecryptActivity.java
 │
 ├── utils/
 │    ├── CryptoUtils.java
 │    └── IOUtils.java
 │
 └── models/
      └── EnvelopeFile.java

/layout (XML UI files)

🖥️ UI Layer (Activities)
SplashActivity

Displays a startup screen before launching the main interface.

MainActivity

Central hub where users choose between encryption or decryption.

EncryptActivity

Handles:

File selection

AES key generation

RSA public key usage

Creating the encrypted envelope

DecryptActivity

Handles:

Importing an existing envelope file

Using RSA private key to recover AES key

Decrypting the file contents

Each Activity uses a dedicated XML layout file for clean separation of UI and logic.

🔐 Cryptography Layer (Utility Classes)
CryptoUtils

Implements hybrid cryptography:

AES-GCM (Symmetric Encryption)

Used for encrypting file contents

Random AES key & IV generated

GCM provides integrity via authentication tags

RSA-OAEP (Asymmetric Encryption)

Used to encrypt the AES key

Public key → encrypt

Private key → decrypt

This hybrid model mirrors systems like PGP, S/MIME, and secure cloud storage.

📦 Data Model
EnvelopeFile

A serialized container holding everything needed for later decryption:

RSA-encrypted AES key

AES IV

AES-encrypted file bytes

This serves as the app’s secure file format.

🔄 Encryption Workflow

User selects a file

AES key + IV generated

File encrypted with AES-GCM

AES key encrypted with RSA public key

EnvelopeFile created containing:

Encrypted file bytes

RSA-encrypted AES key

IV

Envelope saved to device storage

🔁 Decryption Workflow

User selects an envelope file

Envelope parsed into EnvelopeFile

RSA private key decrypts AES key

AES decrypts file content

Plaintext restored to storage

✔ Architectural Strengths

Clear separation of concerns
Activities = UI, Utils = crypto logic, Models = structured data

Hybrid cryptography
AES ensures speed; RSA ensures secure key exchange

Extensible design
Easy to upgrade algorithms, UI components, or storage formats
