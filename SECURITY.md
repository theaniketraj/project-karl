# Security Policy for Project KARL

The Project KARL team and community take the security of our software seriously. We appreciate the efforts of security researchers and the community to help us maintain a high standard of security. This document outlines our policy for reporting security vulnerabilities.

## Supported Versions

We are committed to providing security updates for the latest stable release versions of Project KARL. As the project is currently in its early development (alpha/beta) stages, we encourage users to stay on the most recent release to receive all security patches.

| Version | Supported            |
| ------- | ------------------   |
| `1.0.x` | ✅                   |
| `< 1.0` | ❌                   |

## Reporting a Vulnerability

We take all security bugs in Project KARL seriously. We appreciate your efforts and responsible disclosure and will make every effort to acknowledge your contributions.

To report a security vulnerability, please use the **[Private Vulnerability Reporting feature](https://github.com/theaniketraj/project-karl/security/advisories/new)** on GitHub.

This is the fastest and most secure way to reach the maintainers.

**Please do not report security vulnerabilities through public GitHub issues.**

Instead, use the private reporting feature to ensure your report is handled confidentially and promptly.

When reporting a vulnerability, please include:

- A clear description of the vulnerability, including steps to reproduce it if possible.
- The version of Project KARL you are using.
- Any relevant logs, screenshots, or code snippets that can help us understand the issue.
- Your contact information (email or GitHub username) so we can follow up with you.
- Any additional context that may help us assess the impact and severity of the vulnerability.
- If you have a proposed fix or mitigation, please include that as well.
- If you prefer to remain anonymous, please let us know, and we will respect your wishes.

### Our Commitment

When you report a vulnerability to us, we pledge to:

1. **Acknowledge** receipt of your report promptly, typically within 48-72 hours.
2. **Investigate** the report thoroughly and work with you to understand the scope and impact of the issue.
3. **Keep you informed** of our progress as we work on a fix.
4. **Provide credit** for your discovery once the vulnerability has been addressed and disclosed, unless you prefer to remain anonymous.
5. **Release a patch** and security advisory in a timely manner.

We kindly ask that you do not disclose the vulnerability publicly until a patch has been released and we have had a chance to coordinate on a public announcement.

## Security Philosophy & Scope

Project KARL's core philosophy is **privacy-first and on-device processing**. As such, our primary security concerns revolve around:

- **Local Data Security:** Vulnerabilities that could lead to unauthorized access, modification, or exfiltration of data stored locally by KARL's `DataStorage` implementations (e.g., weaknesses in encryption, insecure file permissions).
- **Model Integrity:** Vulnerabilities that could allow an attacker to tamper with the locally stored AI model state (`KarlContainerState`), potentially leading to malicious or biased predictions.
- **Denial of Service:** Vulnerabilities where malformed input could cause the `LearningEngine` or other components to crash or enter an infinite loop, consuming excessive device resources.

Issues related to the security of the *host application* integrating KARL (e.g., an application's own insecure network communication, improper handling of user input outside of KARL) are generally considered out of scope for KARL's security policy, unless they are caused by a flaw in KARL's API design or implementation.

Thank you for helping keep Project KARL and its users secure.
