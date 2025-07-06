# 📘 URL Shortener API - Usage Guide

This API allows you to shorten long URLs, expand shortened URLs, and test redirections.

## 🔧 How to Use

### 1. Shorten a Long URL

Use the `shorten-long-url.http` file to send a request that converts a long URL into a shortened one.

- Open the file: `shorten-long-url.http`
- Send the request to generate a short URL.
- You’ll receive a response with a shortened version of the URL.

---

### 2. Expand a Short URL

Use the `expand-short-url.http` file to retrieve the original long URL from a short one.

- Open the file: `expand-short-url.http`
- Modify the request by supplying the short URL as a parameter.
- Send the request to receive the corresponding long URL.

---

### 3. Test Redirection

Use the `redirect.http` file to test the redirection functionality directly in your browser.

- Open the file: `redirect.http`
- Copy the redirect URL into your browser’s address bar.
- Observe if it redirects you to the original long URL.

---