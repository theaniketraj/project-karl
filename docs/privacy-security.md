# Privacy & Security

User privacy and data security are the foundational principles of Project KARL. This document provides an overview of our approach.

## The KARL Privacy Model

KARL is designed as a **local-first** AI library. This means:

* **Zero Data Egress by Default:** All learning, data processing, and model storage occur exclusively on the user's device. No interaction data is sent to any external servers.
* **User Control:** Users inherently control their data. Deleting the application's data removes all of KARL's learned knowledge for that user.
* **Data Minimization:** We encourage developers to only feed KARL the necessary **metadata** about user interactions, not the sensitive content itself.

### Security Considerations

* **Encryption at Rest:** While KARL's core does not mandate a specific encryption method, we strongly recommend that all `DataStorage` implementations (like `:karl-room`) use robust encryption for the locally stored database to protect the AI's state and interaction history.
* **Responsible Disclosure:** We take security vulnerabilities seriously. We have a defined process for reporting and handling security issues.

→ For detailed information on our privacy model, please see the [**`Privacy and Security Details`**](https://github.com/theaniketraj/project-karl/blob/main/SECURITY.md)

→ To report a security vulnerability, please follow the guidelines in our main [**`SECURITY`**](https://github.com/theaniketraj/project-karl/blob/main/SECURITY.md) file.
