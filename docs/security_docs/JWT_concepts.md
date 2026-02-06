# 🔑 3. JWT (JSON Web Token) — Complete Breakdown

---

## 3.1 JWT Basics

### ❓ What is JWT (in plain words)

A **JWT is a self-contained token** that:

* Proves **who the user is**
* Carries **user data**
* Can be **verified without database calls**

It’s just a **string**, sent with every request.

Example:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSJ9.xxxxx
```

---

### 🧠 Why JWT is *stateless*

**Stateless = server does NOT store login session**

Traditional (stateful):

```
Login → Session stored in server memory / DB
```

JWT (stateless):

```
Login → Token given → Server forgets you
Next request → Token proves everything
```

✔ No session storage
✔ Horizontally scalable
✔ Perfect for microservices

---

### 🔨 JWT Structure (VERY IMPORTANT)

A JWT has **3 parts**, separated by dots:

```
HEADER.PAYLOAD.SIGNATURE
```

![Image](https://fusionauth.io/img/shared/json-web-token.png)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1400/1%2AWYtnaS7OUPxwX602Tph8fQ.png)

---

### 1️⃣ Header

Tells **how the token is created**

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

* `alg` → algorithm used to sign
* `typ` → token type

---

### 2️⃣ Payload

Contains **claims (user data)**

```json
{
  "sub": "user123",
  "role": "ADMIN",
  "exp": 1700000000
}
```

⚠️ **Payload is NOT encrypted**
Anyone can Base64-decode it.

---

### 3️⃣ Signature

Ensures:

* Token not modified
* Token created by trusted server

```
HMACSHA256(
  base64(header) + "." + base64(payload),
  secretKey
)
```

✔ If payload changes → signature breaks

---

### 🔐 Signed vs Encrypted JWT (Interview Favorite)

| Type                | Meaning            |
| ------------------- | ------------------ |
| Signed JWT (JWS)    | Verifies integrity |
| Encrypted JWT (JWE) | Hides data         |

Most systems use:
✔ **Signed, not encrypted JWT**

Why?

* HTTPS already encrypts traffic
* JWT encryption adds overhead

---

## 3.2 JWT Claims (VERY IMPORTANT)

### ❓ What is a claim?

A **claim = piece of information inside JWT**

Think of it as:

```
key → value
```

---

## 1️⃣ Standard Claims (Know These!)

### `sub` (Subject)

👉 **Who the token belongs to**

```json
"sub": "user123"
```

Usually:

* User ID
* Username

---

### `exp` (Expiration Time)

👉 **When token expires (epoch seconds)**

```json
"exp": 1700000000
```

✔ Prevents forever-valid tokens
✔ Enforced on every request

---

### `iat` (Issued At)

👉 **When token was created**

```json
"iat": 1699990000
```

Useful for:

* Debugging
* Token refresh logic

---

### `iss` (Issuer)

👉 **Who issued the token**

```json
"iss": "auth.myapp.com"
```

✔ Prevents token reuse from other systems

---

## 2️⃣ Custom Claims (Roles & Permissions)

You define these yourself.

Example:

```json
{
  "sub": "user123",
  "roles": ["ROLE_USER", "ROLE_ADMIN"],
  "permissions": ["READ", "WRITE"]
}
```

Spring Security later converts:

```
roles → GrantedAuthority
```

✔ Avoid DB hit on every request
❌ But token size increases

---

## 3️⃣ Token Expiration Handling

### What happens when token expires?

```
Request
 ↓
JWT filter checks exp
 ↓
Expired?
 ↓
401 Unauthorized
```

No server session to “invalidate”.

---

### Common strategy

* **Short-lived access token** (5–15 min)
* **Long-lived refresh token**

Access token expires → refresh token gives new one.

---

## 4️⃣ Clock Skew Problem (Real-world issue)

Servers and clients may have **time differences**.

JWT allows:

```
Allowed clock skew = ±30 seconds (example)
```

Without this:

* Valid token may look expired
* Random login failures 😬

---

## 3.3 JWT Security Concepts

---

## 1️⃣ Symmetric (HS256)

### How it works

* Same **secret key** used to:

  * Sign token
  * Verify token

```
Server secret = "my-secret"
```

✔ Simple
✔ Fast
❌ Secret must be shared

---

### When to use

* Single backend
* Monolithic app

---

## 2️⃣ Asymmetric (RS256)

### How it works

* **Private key** → sign token
* **Public key** → verify token

```
Private Key (Auth Server)
Public Key (API Servers)
```

✔ More secure
✔ Microservices friendly
❌ More complex

---

### Real-world usage

* OAuth2
* Auth server + many resource servers

---

## 🔐 Secret Key vs Public/Private Key

| Type        | Used For       |
| ----------- | -------------- |
| Secret key  | HS256          |
| Private key | Sign (RS256)   |
| Public key  | Verify (RS256) |

---

## 3️⃣ Token Tampering Protection

What if attacker modifies payload?

```
ROLE_USER → ROLE_ADMIN
```

❌ Signature won’t match
❌ Token rejected

✔ This is JWT’s biggest strength

---

## 4️⃣ Token Size & Performance Impact

JWT carries **all data inside it**.

### Bigger token =

* More network usage
* Slower requests
* Bigger headers

Best practice:

* Keep payload **minimal**
* Use IDs, not full objects

✔ Good:

```json
"userId": 42
```

❌ Bad:

```json
"userProfile": { huge JSON }
```

---

## 🧠 Final Mental Model (Lock This In)

```
JWT = ID card + signature
Payload = visible info
Signature = tamper protection
Expiration = safety switch
```
