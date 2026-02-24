# 📘 URL Shortener API - Usage Guide

This guide explains how to interact with the protected API endpoints using IntelliJ HTTP Client.

The application uses JWT-based authentication via Keycloak.

---

## 🔐 Authentication (Required First)

All protected endpoints require a valid Bearer token.

Before calling any API:
1. Open `auth.http`
2. Run the request to retrieve an access token
3. The token will be automatically stored as a global variable: `{{auth_token}}`

The token is retrieved from: http://localhost:8080/realms/fraktl/protocol/openid-connect/token

Once retrieved, all other requests will automatically include: **Authorization: Bearer {{auth_token}}**

---

## 🔧 How to Use

### 1️⃣ Create Short URL (Protected)

Use the `create-short-url.http` file to generate a shortened URL.

Steps:
- Ensure you have executed `auth.http`
- Open `create-short-url.http`
- Send the request

---

---

### 2. Get URL Details by Short Code (Protected)

Use `get-url-details-by-short-code.http` file.

Steps:
- Ensure auth.http was executed
- Replace the short code in the request(by supplying the 'shortUrl' as a parameter)
- Send the request

---

### 3. Test Redirection (This endpoint does NOT require authentication)

Use the `redirect.http` file to test the redirection functionality directly in your browser.

- Open the file: `redirect.http`
- Copy the redirect URL.
- Paste it into your browser
- Observe if it redirects you to the original long URL.

---
## 🧠 Important Notes

- Access tokens expire (default Keycloak behavior - 300s)
- If you receive 401 Unauthorized, re-run auth.http
- Token is stored globally across HTTP files in IntelliJ