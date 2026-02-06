# 🔐 5. OAuth2 – Conceptual & Practical (High-Level → Spring Boot)

---

## 5.1 OAuth2 Fundamentals

### ❓ First: What OAuth2 actually is (very important)

> **OAuth2 is NOT authentication.
> OAuth2 is authorization delegation.**

In simple words:

> “Allow an app to access something **on your behalf**, without sharing your password.”

---

### 🚫 OAuth2 vs JWT (NOT the same!)

| OAuth2                          | JWT                       |
| ------------------------------- | ------------------------- |
| Protocol / framework            | Token format              |
| Defines *how* tokens are issued | Defines *how* tokens look |
| About delegation                | About representation      |
| Can use JWT tokens              | Can be used without OAuth |

✅ OAuth2 **may use JWT**
❌ JWT alone is **not OAuth2**

---

### 🧠 Real-life example (Google login)

When you click **“Login with Google”**:

* You are **not giving password** to the app
* You are authorizing Google to give limited access

👉 That’s OAuth2.

---

## 👥 OAuth2 Roles (MUST KNOW)

![Image](https://jenkov.com/images/oauth2/overview-roles.png)

![Image](https://docs.oracle.com/cd/E55956_01/doc.11123/oauth_guide/content/images/oauth/oauth_overview.png)

---

### 1️⃣ Resource Owner

👉 **The user**

Example:

* You
* Account owner

---

### 2️⃣ Client

👉 **The application requesting access**

Example:

* Your frontend app
* Mobile app
* Backend service

---

### 3️⃣ Authorization Server

👉 **Issues tokens**

Responsibilities:

* Authenticate user
* Ask for consent
* Issue access token

Examples:

* Keycloak
* Auth0
* Google
* Okta

---

### 4️⃣ Resource Server

👉 **API holding protected data**

Example:

* Your Spring Boot REST API

---

## 🎟 Access Token vs Refresh Token

### 🔑 Access Token

* Short-lived
* Sent with every request
* Grants access to APIs

```
Authorization: Bearer <access_token>
```

---

### 🔄 Refresh Token

* Long-lived
* Used to get new access token
* Never sent to APIs

Flow:

```
Access token expired
↓
Send refresh token
↓
Get new access token
```

---

## 5.2 OAuth2 Grant Types (Flows)

---

## 1️⃣ Authorization Code Grant (MOST IMPORTANT)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1400/1%2AULF38OTiNJNQZ4lHQZqRwQ.png)

![Image](https://docs.apigee.com/static/api-platform/images/oauth-auth-code-flow-%281%29.png)

### Used for:

* Web apps
* Mobile apps
* “Login with Google”

---

### Step-by-step (simple)

1. Client redirects user to Authorization Server
2. User logs in & approves
3. Authorization Server sends **authorization code**
4. Client exchanges code for tokens
5. Client uses access token to call API

✔ Most secure
✔ Industry standard
✔ Used everywhere

---

## 2️⃣ Client Credentials Grant

### Used for:

* Machine-to-machine
* Backend → backend

Example:

```
Service A → Service B
```

Flow:

* No user
* Client authenticates using client_id + secret
* Gets access token

✔ Simple
✔ No refresh token usually

---

## 3️⃣ Password Grant (DEPRECATED ❌)

### Why it existed

* Client sends username + password directly

### Why deprecated

* Client sees user password 😱
* Breaks OAuth2 security model
* Encourages bad practices

⚠️ **Interview tip**:

> “Password grant is deprecated because it violates separation of concerns and exposes user credentials to clients.”

---

## 4️⃣ Refresh Token Flow

When:

* Access token expires

Steps:

1. Client sends refresh token
2. Authorization server validates it
3. Issues new access token (maybe new refresh token)

✔ User stays logged in
✔ No re-login

---

## 5.3 OAuth2 in Spring Boot

Now let’s connect **concepts → Spring Boot**.

---

## 🔹 OAuth2 Client vs Resource Server

### 🧑 OAuth2 Client

Spring Boot app that:

* Redirects users to login
* Exchanges authorization code
* Stores tokens

Examples:

* Frontend app
* BFF (Backend for Frontend)

---

### 🛡 OAuth2 Resource Server

Spring Boot app that:

* Protects APIs
* Validates access tokens
* Does NOT issue tokens

Examples:

* REST APIs
* Microservices

---

## 🔧 Spring Security OAuth2 Modules

Spring Security provides:

### 1️⃣ OAuth2 Client

Used for:

* Login with Google / Keycloak
* Authorization Code flow

---

### 2️⃣ OAuth2 Resource Server

Used for:

* Securing APIs
* Validating tokens

Supports:

* JWT validation
* Token introspection

---

## 🔍 Token Validation: Two Approaches

---

### 1️⃣ JWT Validation (Most common)

How it works:

* Token is JWT
* Resource server validates:

  * Signature
  * Expiration
  * Claims

✔ Fast
✔ No network call
✔ Fully stateless

Used with:

* Keycloak
* Auth0
* OAuth2 JWT tokens

---

### 2️⃣ Token Introspection

How it works:

* Token is opaque
* Resource server calls auth server:

```
Is this token valid?
```

✔ Can revoke tokens instantly
❌ Network overhead
❌ Less scalable

---

## 🔐 Integrating with Keycloak / Auth0 (Conceptually)

### With Keycloak / Auth0

They act as:

* **Authorization Server**

Your Spring Boot API:

* Acts as **Resource Server**

Flow:

```
Client → Auth Server → Access Token
Client → API (with token)
API → validates token → returns data
```

You do NOT:

* Store users
* Manage passwords
* Issue tokens

✔ Centralized identity
✔ Enterprise-ready
✔ Scales well

---

## 🧠 Final Mental Model (Lock This In)

```
OAuth2 = rules of the game
JWT = ticket format
Authorization Server = issues tickets
Resource Server = checks tickets
Spring Boot = enforces rules
```
