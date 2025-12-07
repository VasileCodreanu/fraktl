# 📘 URL Shortener API - Usage Guide

This API allows you to shorten long URLs, expand shortened URLs, and test redirections.

## 🔧 How to Use

### 1. Create-short-url

Use the `create-short-url.http` file to send a request that converts a long URL into a shortened one.

- Open the file: `create-short-url.http`
- Send the request to generate a short URL.
- You’ll receive a response with a 'shortUrl' version of the 'originalUrl'.

---

### 2. Get-url-details-by-short-code

Use the `get-url-details-by-short-code.http` file to retrieve the original long URL from a short url.

- Open the file: `get-url-details-by-short-code.http`
- Modify the request by supplying the 'shortUrl' as a parameter.
- Send the request to receive the corresponding long URL.

---

### 3. Test Redirection

Use the `redirect.http` file to test the redirection functionality directly in your browser.

- Open the file: `redirect.http`
- Copy the redirect URL into your browser’s address bar.
- Observe if it redirects you to the original long URL.

---