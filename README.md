**Digital Envelope – Architecture Overview**

This document provides a technical overview of the architecture behind the Digital Envelope Android Application, which implements a hybrid cryptographic system for secure file encryption and decryption.

**IMPORTANT**

**This code was designed to be ran in Android Studio via cloning the repository. Trying in any other environement might cause unintended errors.** 

**Project Structure**

The project is organized into activities that manage user interactions, utility classes that encapsulate cryptographic and file-handling operations, and data models used for representing encrypted content. The high-level structure includes the activities folder containing SplashActivity, MainActivity, EncryptActivity, and DecryptActivity; the utils folder containing CryptoUtils and IOUtils; and the models folder containing the EnvelopeFile class, with corresponding UI layouts stored under the Android XML layout directory.

**Abbreviated Keywords**

Advanced Encryption Standard - AES

Galois/Counter Mode - GCM

Rivest, Shamir, and Adleman - RSA

Optimal Asymmetric Encryption Padding - OAEP

Inital Vector - IV

**UI Layer (Activities)**

SplashActivity -
This activity presents the initial loading screen and performs any required initialization before transitioning into the main application interface.

MainActivity -
This screen serves as the central control hub where users choose between performing encryption or decryption operations.

EncryptActivity -
This activity guides the user through selecting a file and creating the final encrypted envelope that will be stored on the device.

DecryptActivity -
This activity allows the user to load an existing envelope file and restores the original file to the device’s storage.

**Cryptography Layer (Utility Classes)**

CryptoUtils -
This class implements the hybrid cryptographic model used by the app, combining AES-GCM for encrypting the file’s binary data with RSA-OAEP for encrypting the AES key. I used AES-GCM for its performance and built-in authentication tags, ensuring both confidentiality and integrity, while RSA-OAEP provides secure public-key encryption needed to wrap the symmetric AES key for that extra layer of security.

IOUtils -
Since I am working with Android SDK API 24, this utility class is needed safely reading and writing files, handling byte streams, and managing how encrypted and decrypted data is moved between storage and memory.

**Data Model**

EnvelopeFile -
The EnvelopeFile model has the encrypted data produced during the encryption process. It stores the RSA-encrypted AES key, the AES IV, and the AES-encrypted file bytes together in one main structure so that all information required for decryption is bundled into a single object.

**Encryption Workflow**

The encryption workflow begins when the user selects a file. The application generates a random AES key and IV, encrypts the file contents using AES-GCM, encrypts the AES key using the RSA public key, creates an EnvelopeFile containing the encrypted file bytes, encrypted AES key, and IV, and finally allows the user to write the envelope to device storage for later retrieval.

**Decryption Workflow**

The decryption workflow starts with the user selecting an envelope file previously encrypted through the app. The app decrypts the AES key using the RSA private key, applies AES-GCM decryption to recover the original file bytes, and writes the restored file back into device storage.

**Limitations & Future Improvements**

Limited to local device -
The application performs all cryptographic operations and file handling exclusively on the user’s device. It does not support cloud syncing, remote decryption, or cross-device access, meaning encrypted envelopes must be manually transferred if needed elsewhere.

Add support for selecting and encrypting larger files -
While functional for typical documents and media, the current implementation may struggle with very large files. Expanding file-size support would allow users to securely handle high-resolution media, archives, and large assets without manual compression.

Add a digital signature for authentication -
Integrating digital signatures ensures that recipients can verify the sender’s identity and confirm the file has not been tampered with.

Cleaner app design -
The app itself is very basic with a lot of whitespace. The app includes only essential UI components and provides limited runtime feedback during long operations. This may lead to reduced usability or confusion during encryption and decryption tasks. Updating the UI would improve usability and can improve user satifaction.

## License

    Copyright [2025] [Jayson Joseph]
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
