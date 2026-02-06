# 🔐 Spring Security – Core Fundamentals (Explained Like a Human)

---

## 1️⃣ Security Architecture (The Big Picture)

### 👉 What Spring Security actually does

At its core, **Spring Security is just a giant gatekeeper** sitting **in front of your controllers**.

It answers 3 questions for **every HTTP request**:

1. **Who are you?** → *Authentication*
2. **Are you allowed to do this?** → *Authorization*
3. **If yes, let you reach the controller**

💡 **Important mental model**

```
Browser
   ↓
Spring Security Filters (gatekeepers)
   ↓
DispatcherServlet
   ↓
Controller
```

If security fails → **request never reaches your controller** ❌

---

## 1.1 Authentication vs Authorization (Deep Difference)

### 🔑 Authentication = Identity Check

> “Who are you?”

Examples:

* Username + Password
* JWT token
* OAuth token

✔ Result: **You are logged in**

---

### 🛂 Authorization = Permission Check

> “Are you allowed to do this?”

Examples:

* Can USER access `/admin`?
* Can this role DELETE data?

✔ Result: **Access granted or denied**

---

### 🔥 Very Important Rule

| Concept        | Happens When         |
| -------------- | -------------------- |
| Authentication | User logs in         |
| Authorization  | On **every request** |

---

### 🧠 Real-life analogy

| Scenario              | Meaning        |
| --------------------- | -------------- |
| Showing ID at airport | Authentication |
| Allowed into cockpit? | Authorization  |

---

## 1.2 SecurityContext & SecurityContextHolder

### 📦 SecurityContext

Think of it as **a box that stores logged-in user info**.

It contains:

* Who the user is
* Their roles
* Authentication status

```
SecurityContext
 └── Authentication
      ├── Principal (User)
      ├── Credentials
      ├── Authorities (Roles)
```

---

### 🧠 SecurityContextHolder

This is **where Spring Security stores the SecurityContext**.

📌 By default:

* Stored in **ThreadLocal**
* One context per request thread

```java
SecurityContext context =
    SecurityContextHolder.getContext();
```

---

### 🔄 Thread-local behavior (IMPORTANT)

Each HTTP request:

* Runs on **its own thread**
* Has its **own SecurityContext**

```
Thread-1 → User: Alice
Thread-2 → User: Bob
```

✔ No data leak between users
❌ But async threads need special care

---

## 1.3 Principal, Authentication, GrantedAuthority

### 👤 Principal

Represents the **logged-in user**.

Examples:

* Username (`String`)
* `UserDetails` object
* JWT subject

```java
Authentication auth = SecurityContextHolder
        .getContext()
        .getAuthentication();

Object principal = auth.getPrincipal();
```

---

### 🔐 Authentication

This is **THE core object** in Spring Security.

It represents:

* Who you are
* How you authenticated
* Your roles

```java
Authentication {
   principal      // user info
   credentials    // password / token
   authorities    // roles
   authenticated  // true/false
}
```

---

### 🏷 GrantedAuthority

Represents **permissions or roles**.

Examples:

* `ROLE_USER`
* `ROLE_ADMIN`
* `READ_PRIVILEGE`

```java
Collection<? extends GrantedAuthority> authorities =
        auth.getAuthorities();
```

✔ Roles are just **strings**
✔ Convention: roles start with `ROLE_`

---

## 2️⃣ Security Filter Chain (MOST IMPORTANT PART)

> ⚠️ If you understand this, Spring Security becomes easy.

---

## 2.1 What is a Servlet Filter?

A **Servlet Filter** is something that:

* Runs **before** controller
* Can modify request / response
* Can block request

```java
doFilter(request, response, chain)
```

```
Request → Filter → Controller → Response
```

---

### 🧠 Real-life analogy

Think of filters as **security checks at airport**:

* ID check
* Baggage scan
* Boarding pass check

---

## 2.2 DelegatingFilterProxy (Spring + Servlet Bridge)

### ❓ Why does this exist?

Servlet containers (Tomcat):

* Know **Servlet Filters**
* DON’T know Spring Beans

Spring:

* Knows Beans
* Doesn’t control servlet lifecycle

➡️ **DelegatingFilterProxy bridges them**

---

### 🔄 What it does

1. Tomcat calls `DelegatingFilterProxy`
2. It **delegates the call** to Spring-managed bean

```
Tomcat
 ↓
DelegatingFilterProxy
 ↓
Spring Security FilterChain
```

✔ This is how Spring Security enters the request

---

## 2.3 FilterChainProxy (The Boss Filter)

`FilterChainProxy` is **Spring Security’s main filter**.

It:

* Holds **multiple security filters**
* Decides **which filters run for which URL**

Example filters:

* UsernamePasswordAuthenticationFilter
* JwtAuthenticationFilter
* ExceptionTranslationFilter
* AuthorizationFilter

---

### 🧠 Mental model

```
FilterChainProxy
 ├── Filter 1
 ├── Filter 2
 ├── Filter 3
 └── Filter N
```

Each request passes through **many filters**.

---

## 2.4 How Spring Security intercepts requests

### Step-by-step Flow

```
1. Request arrives
2. DelegatingFilterProxy triggered
3. FilterChainProxy selected
4. Security filters run in order
5. Authentication happens (if needed)
6. Authorization check
7. Controller executes
```

If any step fails → ❌ request stops

---

### Example: Protected API

```java
@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public String admin() {
    return "Secret data";
}
```

If:

* User not authenticated → 401
* User lacks role → 403

Controller **never executes** 🚫

---

## 2.5 Once-Per-Request Filters vs Normal Filters

### 🔁 Normal Filter

May execute:

* Multiple times
* During forwards / includes / async

❌ Can cause:

* Duplicate authentication
* Token re-processing

---

### ✅ OncePerRequestFilter (Spring Security Favorite)

Guarantees:

* Runs **ONLY ONCE per HTTP request**

```java
public class JwtFilter extends OncePerRequestFilter {
   @Override
   protected void doFilterInternal(...) {
       // extract token
       // authenticate user
   }
}
```

✔ Used for:

* JWT
* OAuth
* Custom auth filters

---

### 🧠 Why Spring Security uses it

Security logic must be:

* Predictable
* Idempotent
* Safe

So **most security filters say “once is enough”**

---

## 🔚 Final Mental Model (Remember This)

```
Request
 ↓
DelegatingFilterProxy
 ↓
FilterChainProxy
 ↓
Security Filters
 ↓
SecurityContext populated
 ↓
Authorization check
 ↓
Controller
```

---

## 🧩 Key Takeaways

✔ Spring Security = **filters, not magic**
✔ Authentication ≠ Authorization
✔ SecurityContext = user state
✔ ThreadLocal = per-request safety
✔ FilterChainProxy = heart of Spring Security
