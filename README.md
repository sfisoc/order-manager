# **Order Manager**
An **in-memory order management application** that simulates a **limit order type**. This application provides several **HTTP endpoints** for order management and trade history retrieval.

---

## **📌 API Documentation**

### **🛠 Parameters**
| Parameter | Type | Required | Description | Supported Values |
|-----------|------|----------|-------------|------------------|
| `{pairwise}` | Enum | ✅ Yes | Currency pair | `"BTCZAR"`, `"ETHZAR"`, `"ETHBTC"` |
| `{limit}` | Integer | ❌ No | Number of records to return | Any positive integer |

---

### **📌 Endpoints**

#### **1️⃣ Get Order Book**
**`GET /v1/{pairwise}/orderbook`**  
📌 Retrieves the list of orders for the specified currency pair.

---

#### **2️⃣ Get Trade History**
**`GET /v1/{pairwise}/tradehistory/{limit}`**  
📌 Retrieves trade history for the given currency pair with an optional limit on the number of records.

---

#### **3️⃣ Create a Limit Order**
**`POST /v1/{pairwise}/orderbook/limit`**  
📌 Places a new limit order for the specified currency pair.

📌 **Request Body (JSON, Required)**
```json
{
    "side": "BUY",            // Enum: SELL, BUY (Required)
    "price": "5000",          // Positive decimal (Required)
    "quantity": "234",        // Positive decimal (Required)
    "timeInForce": "GTC"      // Enum: GTC, FOK, IOC (Optional)
}
```

---

#### **4️⃣ Cancel an Order**
**`DELETE /v1/{pairwise}/orderbook/cancel`**  
📌 Cancels an existing order based on the given `orderId`.

📌 **Request Body (JSON, Required)**
```json
{
    "orderId": "d99f1e1a-441b-4fd0-9f9e-3b68886eb894" // 36-character UUID (Required)
}
```

---

## **📌 Running the Project**
This project was developed using **IntelliJ Community Edition**.

📌 **Steps to Run:**  
1️⃣ Import the project into **IntelliJ IDEA**.  
2️⃣ Run the `Main.kt` file.

📌 **Default Server Configuration:**
- The application starts an HTTP server on **port 8890**.

📌 **Example of a Running Instance:**  
![Server Running](src/main/resources/images/obruning.PNG)

---

## **📌 Running Unit Tests**
📌 **Location:** `src/test/kotlin/`
- Navigate to the test directory and run each test suite.

---

## **📌 Running Load Tests**
📌 The project uses **K6** for load testing.

📌 **Location of Test Script:**  
`src/main/resources/scripts/orderBookLoadTest.js`

📌 **How to Run Load Tests:**  
1️⃣ Install K6: [Installation Guide](https://grafana.com/docs/k6/latest/set-up/install-k6/)  
2️⃣ Run the test script with the following command:
```sh
k6 run --duration 5s orderBookLoadTest.js
```
📌 **Example Load Test Output:**  
![K6 Load Test](src/main/resources/images/k6tests.PNG)

---

## **📌 External Interaction (Postman Collection)**
📌 **Location:** `src/main/resources/order-manager.postman_collection.json`

📌 **How to Use:**  
1️⃣ Start the project.  
2️⃣ Import the **Postman collection**.  
3️⃣ Execute API requests.

📌 **Example Postman Execution:**  
![Postman Execution](src/main/resources/images/obpostman.PNG)

---

Here’s a **more structured and professional rewrite** of your points:

---

## **🔹 Key Areas for Improvement & Considerations**

### **1️⃣ API Security – Authentication & Authorization**
- Implement **secure authentication and authorization** mechanisms.
- Evaluate between:
    - **Using `vertx-auth-common`** for built-in authentication support.
    - **Placing an API gateway** (e.g., Kong, Nginx, or Traefik) for centralized security and rate limiting.
- Ensure proper **JWT/OAuth2 integration** for token-based authentication.

---

### **2️⃣ Order Engine Optimization**
- The **current implementation uses `TreeMap`** for order management with **O(log n) complexity**.
- Explore alternative **data structures** that optimize:
    - **Insertion and lookup speeds** for better order matching.
    - **Memory efficiency** for handling high-frequency trades.

---

### **3️⃣ Thread Safety & Concurrency Handling**
- **Current in-memory data structures are not thread-safe**, which may lead to race conditions.
- **Short-term improvement**: Use **`Collections.synchronizedMap`** for basic synchronization.
- **Further steps for concurrency testing & improvements:**
    - Implement **lock-free data structures** where possible.
    - Use **read-write locks (`ReentrantReadWriteLock`)** for shared resources.
    - Apply **Vert.x worker threads** to manage concurrent order processing efficiently.
    - Conduct **stress tests** with multiple concurrent clients to evaluate performance under load.

---