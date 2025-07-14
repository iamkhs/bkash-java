# 🧾 bKash Payment Gateway Integration – Java Spring Boot

This project demonstrates how to integrate the **bKash Tokenized Checkout API** using **Java Spring Boot**, with **static sandbox credentials and payloads**.

---

## 📦 Features

- ✅ Get access token using app credentials  
- ✅ Create a payment session and get `bkashURL` for redirect  
- ✅ Handle callback after customer completes payment  
- ✅ Execute the payment to confirm success  
- ✅ Capture and log final transaction info (`trxID`, `amount`, etc.)

---

## 🚀 API Endpoints and Flow

This section explains the available endpoints in the Spring Boot application and how the bKash payment integration works step by step.

---

### 1. 🧾 `GET /pay` – Initiate Payment

- **Purpose:** Starts the payment process.
- **How it works:**
  - The backend first generates an **access token** by calling bKash’s `/token/grant` API.
  - Then it creates a **payment session** using bKash’s `/checkout/create` endpoint.
  - The response contains a `bkashURL` which is returned to the frontend.
  - The frontend should **redirect the user** to this URL so they can complete the payment in the bKash interface.

#### 🔁 Example flow:
```http
GET /pay
→ Returns bkashURL (example):
  https://sandbox.payment.bkash.com/?paymentId=TR001...&hash=...
```

### 2. 🔁 `/callback` – Handle bKash Payment Redirect

**Method:** `GET`  
**Endpoint:** `/callback`  
**Purpose:** Handles the response from bKash after the customer completes (or cancels) the payment.

---

#### 📥 Query Parameters Received from bKash:

| Parameter     | Type   | Description                          |
|---------------|--------|--------------------------------------|
| `paymentID`   | String | The unique ID for this payment       |
| `status`      | String | Payment status: `success`, `failure`, or `cancel` |
| `signature`   | String | Optional bKash-provided signature     |

---

#### ✅ On Success Flow:

If `status=success`, the flow is:

1. Extract `paymentID` from query params.
2. Call `getToken()` again to fetch a valid token.
3. Call `executePayment(paymentID, token)` to finalize the transaction.
4. You may store the response (which contains `trxID`, amount, etc.) in your database.
5. Return the result to the frontend/user.

**Success JSON Example:**
```json
{
  "paymentID": "TR0011Pplo8lS1752517035835",
  "trxID": "CGF20N477Q",
  "transactionStatus": "Completed",
  "amount": "100",
  "currency": "BDT",
  "intent": "sale",
  "paymentExecuteTime": "2025-07-15T00:17:41:194 GMT+0600",
  "merchantInvoiceNumber": "Inv123456",
  "payerReference": "017XXXXXXXX",
  "customerMsisdn": "01929918378",
  "statusCode": "0000",
  "statusMessage": "Successful"
}

