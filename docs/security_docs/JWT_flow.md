# 🧩 4. JWT with Spring Security (Hands-on Core)
---

## 4.1 JWT Authentication Flow (End-to-End)

## 🔐 Step-by-Step JWT Authentication Flow

### 1️⃣ Login Request → Authentication

Client sends credentials **once**:

```
POST /login
{
  "username": "alice",
  "password": "password123"
}
```

What happens internally:

* `UsernamePasswordAuthenticationFilter` intercepts
* Creates **unauthenticated Authentication**
* Delegates to `AuthenticationManager`
* Credentials checked (DB, LDAP, etc.)

✅ If valid → user is authenticated
❌ If invalid → 401 Unauthorized

---

### 2️⃣ Token Generation (After Successful Login)

After authentication succeeds:

Spring Security now knows:

* Who the user is
* Their roles / authorities

Now **you generate JWT**:

JWT contains:

* `sub` → user identifier
* `roles` → authorities
* `iat`, `exp`

Important:

> **JWT is created ONLY at login**

---

### 3️⃣ Token Sent in Response

Server sends JWT back:

```
HTTP 200 OK
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

At this moment:

* Server **does NOT remember the user**
* No session stored
* JWT is the “proof”

---

### 4️⃣ Token Sent in Authorization Header

For **every future request**, client must attach token:

```
Authorization: Bearer <JWT>
```

Example:

```
GET /api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

⚠️ If token is missing → user is anonymous

---

### 5️⃣ Token Validation Per Request

On **every request**:

1. JWT filter extracts token
2. Signature verified
3. Expiration checked
4. User details loaded
5. Authentication stored in `SecurityContext`

Only **after this**, authorization happens.

---

### 🧠 Core JWT Rule (Remember This)

> **Login authenticates once.
> JWT authenticates every request.**

---

## 4.2 Custom JWT Filter (The Heart of Stateless Auth)

Spring Security **does NOT magically understand your JWT**.
You must teach it — using a **custom filter**.

---

## Why Extend `OncePerRequestFilter`?

Because JWT validation must:

* Run **once**
* Be predictable
* Avoid duplicate execution

So we use:

```
OncePerRequestFilter
```

---

## What Your JWT Filter Conceptually Does

### 🔁 High-level flow inside the filter

```
Request arrives
 ↓
Extract Authorization header
 ↓
Validate JWT
 ↓
Load UserDetails
 ↓
Create Authentication
 ↓
Store in SecurityContext
 ↓
Continue filter chain
```

---

## Step-by-Step Breakdown (Very Important)

---

### 1️⃣ Extract Authorization Header

Filter checks:

```
Authorization: Bearer <token>
```

If:

* Header missing → skip filter
* Header present → continue

Important:

> **Filter should NOT fail request if token is missing**
> Anonymous access may still be allowed.

---

### 2️⃣ Validate Token

Validation includes:

* Signature check
* Expiration check
* Issuer check (optional)

If token is:

* Invalid
* Expired
* Tampered

→ **AuthenticationException thrown**

---

### 3️⃣ Load UserDetails

From token:

```
sub = "alice"
```

Use it to:

* Load user from DB / cache
* Get roles & permissions

Why load user?

* To get fresh authorities
* To ensure user still exists

---

### 4️⃣ Create Authentication Object

Now create:

```
Authentication:
  principal   = UserDetails
  authorities = roles
  authenticated = true
```

This represents:

> “This request is from a valid, authenticated user”

---

### 5️⃣ Set Authentication in SecurityContext

This is the **most important line conceptually**:

```
SecurityContextHolder.getContext()
    .setAuthentication(authentication);
```

Now:

* Spring Security believes the user is logged in
* Authorization can happen

---

### 6️⃣ Continue Filter Chain

Finally:

```
filterChain.doFilter(request, response)
```

Without this:

* Request stops
* Controller never executes

---

## Where JWT Filter Fits (Interview Question)

Your JWT filter must be placed:

* **After** `SecurityContextPersistenceFilter`
* **Before** `FilterSecurityInterceptor`

Why?

* Authentication must happen **before authorization**

---

## 4.3 Stateless Session Management

Now let’s understand **why sessions are disabled**.

---

## ❓ Why `SessionCreationPolicy.STATELESS`

By default, Spring Security:

* Creates HTTP sessions
* Stores Authentication in session

But JWT philosophy is:

> **Server must remember nothing**

So we tell Spring Security:

```
SessionCreationPolicy.STATELESS
```

Meaning:

* ❌ No HttpSession created
* ❌ No session read
* ❌ No session stored

✔ Authentication happens **only via JWT**

---

## Disabling HTTP Session (Why It Matters)

Without disabling session:

* Spring may reuse old authentication
* Bugs appear in distributed systems
* Stateless promise breaks

With STATELESS:

* Every request must prove itself
* No hidden server state

---

## CSRF Implications (VERY IMPORTANT)

### Why CSRF is disabled for JWT APIs

CSRF attacks rely on:

* Cookies
* Automatic browser behavior

JWT APIs:

* Use `Authorization` header
* Browser does NOT auto-attach it

So:

```
CSRF protection is unnecessary
```

That’s why we usually:

* Disable CSRF
* Use stateless auth

---

### ⚠️ Important clarification

If:

* JWT stored in **cookies**

Then:

* CSRF protection is REQUIRED

Most APIs:
✔ Store JWT in memory / local storage
✔ Send via Authorization header

---

## 🧠 Final Mental Model (Lock This In)

```
Login → JWT created
JWT → sent with every request
JWT Filter → validates token
SecurityContext → populated per request
No session → no memory → true stateless
```

---

## 🎨 Visual JWT Flow Diagram

```mermaid
flowchart TD
    subgraph LOGIN ["1️⃣ Login Flow (One Time)"]
        direction TB
        Credentials[Client sends Credentials] -->|POST /login| LoginFilter
        LoginFilter -->|Check DB| AuthMgr{Valid?}
        AuthMgr -- No --> 401[401 Unauthorized]
        AuthMgr -- Yes --> GenToken[Generate JWT]
        GenToken --> SendToken[Send JWT to Client]
    end

    subgraph REQUEST ["2️⃣ Request Flow (Every Time)"]
        direction TB
        Req[Client sends Request] -->|Authorization: Bearer JWT| JwtFilter
        JwtFilter -->|Validate Sig & Exp| Valid{Valid?}
        
        Valid -- No --> 401Req[Throw AuthException]
        
        Valid -- Yes --> Extract[Extract User/Roles]
        Extract --> Context[Set SecurityContext]
        Context --> AuthZ{Authorized?}
        
        AuthZ -- No --> 403[403 Forbidden]
        AuthZ -- Yes --> Ctrl[Execute Controller]
    end
```