# Retail Apparel Inventory Management System

**A Structured Design & Variant Inventory Management Web Application Built with Advanced Java (Servlets, JSP, JDBC, MySQL, and ZXing Barcodes)**

*Submitted in partial fulfillment of the requirements for the Degree of Bachelor of Technology in Computer Science & Engineering (Artificial Intelligence), Rajasthan Technical University (RTU), Kota / Jaipur Engineering College & Research Centre (JECRC).*  
**Author:** Divyana Mohnani (CSAI 65)

---

## 📌 Executive Summary & Problem Addressed

Retail apparel inventory poses a unique challenge: a single product design branches into dozens of combinations of size, colour, sleeve length, and lower length. In traditional apparel shops, records kept in paper notebooks or spreadsheets suffer from:
1. **Repeated Manual Typing**: Staff retype details from manufacturer invoices, causing costly digit errors.
2. **Variant Line Explosion**: Staff cannot immediately answer customer queries (e.g. *"Do we have the black trunks in large for gents?"*).
3. **Hidden Problem Stock**: Defective items, previous-season carryover, and dead stock are lumped together into plain counts.
4. **Online Platform SKU Limits**: Listing every variant as an individual product quickly exhausts catalog limits.
5. **No Barcodes on Job-Worker Goods**: Items made by outside stitchers/tailors lack manufacturer barcodes and MRP tags.

This project solves these problems by providing an **Advanced Java MVC Web Application** running on Apache Tomcat with a MySQL backend.

---

## 🏷️ Structured Variant Coding Scheme

Every variant is tracked under its parent **Design Number** and receives an auto-generated, readable, unique variant code:

$$\mathbf{Variant\ Code} = \mathbf{Gender} / \mathbf{Size} / \mathbf{Length} / \mathbf{Colour}$$

### Example Codes
- **Black trunks for males in size large**: `M/L/B/BK` (`M` = Male, `L` = Large, `B` = Trunks/Bottoms, `BK` = Black)
- **Males size 9 water shoes (Length = NA)**: `M/9/NA/BK`
- **Males anti-fog swim goggles (Free Size, Length = NA)**: `M/FS/NA/NV`

### Coding Rules & Lookup Standards
- **Gender**: `M` = Male / Gents, `W` = Female / Ladies, `B` = Boys, `G` = Girls
- **Size**: `S`, `M`, `L`, `XL`, `XXL`, `FS` (Free Size), or numeric shoe sizes (`6`, `7`, `8`, `9`, `10`, etc.)
- **Lower Length**: `B` = Trunks / Bottoms, `SH` = Shorts, `CP` = Capri, `FP` = Full pant, `NA`
- **Sleeve Length**: `FS` = Full sleeve, `HS` = Half sleeve, `SL` = Sleeveless, `NA`
- **Colours**: `BK` (Black), `BL` (Blue), `RD` (Red), `WH` (White), `GR` (Green), `YL` (Yellow), `GY` (Grey), `NV` (Navy), `PK` (Pink), `OR` (Orange), extendable via Master table.

---

## 🎨 Category NA & Dynamic Grey-Out Matrix

The stock-update screen dynamically disables irrelevant fields using JavaScript and validates them server-side in the servlet:

| Attribute | Accessories | Swimwear | Footwear |
| :--- | :--- | :--- | :--- |
| **Colour** | Applies | Applies | Applies |
| **Size** | Free Size (`FS`) or `NA` | Standard Apparel (`S`–`XXL`) | Numeric sizes (`6`–`12`) |
| **Sleeve Length** | **NA (Greyed out)** | Applies to tops (rashguards), otherwise **NA** | **NA (Greyed out)** |
| **Lower Length** | **NA (Greyed out)** | Applies to trunks/shorts, otherwise **NA** | **NA (Greyed out)** |
| **Quantity & Notes** | Applies | Applies | Applies |

---

## 🔍 Stock Conditions & Management Actions

| Condition | Meaning | Typical Retail Action |
| :--- | :--- | :--- |
| **Normal** | Sellable stock with no known defects | Sell at full printed MRP |
| **Defective** | Flaw in stitching, fabric tear, or print defect | Return to vendor, repair, or mark down |
| **Old** | Leftover stock from an earlier season/lot | Sell first or apply seasonal discount (e.g. 20%) |
| **Dead Stock** | Stopped moving; customer demand has ended | Clearance sale markdown (e.g. 50%) or cease reordering |

---

## 🏛️ System Architecture & Technology Stack

```
[ Browser / USB Barcode Scanner ]
              │
              ▼
   [ Apache Tomcat Container ]
     ├── AuthFilter (Session & Role Authorization: OWNER vs STAFF)
     ├── Servlets (Controllers: Login, Stock, Search, Import, Label, Report, Masters)
     ├── JSP & JSTL (Views: Dynamic NA grey-out, Print media stylesheets)
     └── JavaBeans & DAOs (DesignDAO, VariantDAO, StockDAO, MasterDAO, UserDAO)
              │ (JDBC)
              ▼
    [ MySQL Database (inventory_db) ]
```

- **Languages**: Java 17 (Core & Advanced Java: Servlets 4.0, JSP, JSTL, JDBC)
- **Database**: MySQL (5 core normalized tables + User auth table)
- **Libraries**:
  - **ZXing (Zebra Crossing)**: Code 128 barcode generation and Base64 label rendering
  - **Apache Commons CSV**: Vendor CSV file import
  - **Apache POI**: Vendor Excel (`.xlsx`, `.xls`) file processing
  - **Gson**: JSON serialization for AJAX lookups
  - **JUnit 5**: Unit testing validation and code generation logic

---

## 📂 Project Directory Structure

```
inventory-management-system/
├── pom.xml                                   # Maven dependencies & build configuration
├── README.md                                 # Complete documentation & viva guide
└── src/
    ├── main/
    │   ├── java/com/retail/inventory/
    │   │   ├── controller/                   # Servlets (Controllers)
    │   │   │   ├── LoginServlet.java
    │   │   │   ├── LogoutServlet.java
    │   │   │   ├── DashboardServlet.java
    │   │   │   ├── StockServlet.java
    │   │   │   ├── SearchServlet.java
    │   │   │   ├── ImportServlet.java
    │   │   │   ├── LabelServlet.java
    │   │   │   ├── BarcodeImageServlet.java
    │   │   │   ├── ReportServlet.java
    │   │   │   ├── MasterServlet.java
    │   │   │   ├── BackupServlet.java
    │   │   │   ├── BillingServlet.java       # POS counter checkout & stock reduction
    │   │   │   ├── ReceiptServlet.java       # Tax invoice receipt view
    │   │   │   └── SalesHistoryServlet.java  # Daily sales register
    │   │   ├── dao/                          # JDBC Data Access Objects
    │   │   │   ├── DBConnection.java
    │   │   │   ├── DesignDAO.java
    │   │   │   ├── VariantDAO.java
    │   │   │   ├── StockDAO.java
    │   │   │   ├── MasterDAO.java
    │   │   │   ├── UserDAO.java
    │   │   │   └── SalesDAO.java             # Transactional billing & inventory reduction
    │   │   ├── filter/
    │   │   │   └── AuthFilter.java           # Role-based access control
    │   │   ├── model/                        # JavaBeans
    │   │   │   ├── Design.java
    │   │   │   ├── Variant.java
    │   │   │   ├── Stock.java
    │   │   │   ├── Colour.java
    │   │   │   ├── StockCondition.java
    │   │   │   ├── User.java
    │   │   │   ├── StockItemView.java
    │   │   │   ├── SalesBill.java            # Master invoice bean
    │   │   │   └── SalesItem.java            # Cart line item bean
    │   │   ├── service/                      # Business Services
    │   │   │   ├── VariantCodeGenerator.java # Synopsis coding algorithm
    │   │   │   ├── BarcodeService.java       # ZXing Code 128 generator
    │   │   │   ├── VendorImportService.java  # CSV & Excel parser
    │   │   │   └── BackupService.java        # SQL export utility
    │   │   ├── util/
    │   │   │   └── ValidationUtil.java       # Business rules & NA validation
    │   │   └── demo/
    │   │       └── DemoRunner.java           # Standalone CLI demonstration
    │   ├── resources/
    │   │   ├── db.properties                 # MySQL connection properties
    │   │   ├── sample_vendor_stock.csv       # Test vendor data file
    │   │   └── sql/
    │   │       ├── schema.sql                # Complete DDL tables
    │   │       ├── billing_schema.sql        # Invoicing and transactions schema
    │   │       └── sample_data.sql           # Realistic seed data
    │   └── webapp/
    │       ├── WEB-INF/
    │       │   ├── web.xml                   # Deployment descriptor
    │       │   └── common/
    │       │       └── navbar.jsp            # Common navigation header
    │       ├── static/
    │       │   ├── css/app.css               # Clean retail stylesheet & print CSS
    │       │   └── js/stock-update.js        # Dynamic NA grey-out & live preview
    │       ├── index.jsp
    │       │── login.jsp                     # Sign in screen
    │       ├── dashboard.jsp                 # KPI metrics & alerts
    │       ├── stockUpdate.jsp               # Synopsis Figure 4 form
    │       ├── search.jsp                    # Multi-criteria filtering
    │       ├── import.jsp                    # Bulk upload & barcode scanner terminal
    │       ├── label.jsp                     # Barcode label print sheet
    │       ├── billing.jsp                   # POS counter checkout
    │       ├── receipt.jsp                   # Printable sales receipt
    │       ├── salesHistory.jsp              # Daily revenue counter
    │       ├── reports.jsp                   # Problem stock valuation & clearance
    │       └── masters.jsp                   # Owner master data manager
    └── test/
        └── java/com/retail/inventory/
            ├── service/
            │   ├── VariantCodeGeneratorTest.java
            │   └── BarcodeServiceTest.java
            ├── util/
            │   └── ValidationUtilTest.java
            └── model/
                └── SalesItemTest.java
```

---

## 🚀 Setup & Execution Guide

### 1. Database Setup (MySQL)
Open **MySQL Workbench** or MySQL CLI and run the SQL scripts in order:

```sql
-- Step 1: Run the schema script
source /Users/divyana/.gemini/antigravity/scratch/inventory-management-system/src/main/resources/sql/schema.sql;

-- Step 2: Seed the initial data
source /Users/divyana/.gemini/antigravity/scratch/inventory-management-system/src/main/resources/sql/sample_data.sql;
```

Check your MySQL credentials in `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/inventory_db?useSSL=false&allowPublicKeyRetrieval=true
db.user=root
db.password=YOUR_PASSWORD
```

### 2. Running Automated Tests & Standalone Demo
You can run the JUnit tests and the demo runner via Maven:

```bash
# Run unit tests
mvn test

# Package WAR file
mvn clean package
```

### 3. Deploying to Apache Tomcat
1. Copy the generated `target/inventory.war` to your Tomcat `webapps/` folder:
   ```bash
   cp target/inventory.war $TOMCAT_HOME/webapps/
   ```
2. Start Tomcat (`$TOMCAT_HOME/bin/startup.sh` or through your IDE).
3. Access in your browser:
   ```
   http://localhost:8080/inventory/
   ```

### 4. Logging In
- **Store Owner**: `owner` / `owner123` (Full access to Master data, database exports, reports, and stock entry)
- **Counter Staff**: `staff` / `staff123` (Stock updates, barcode scanner lookup, search, label printing)

---

## 🎓 Academic Viva & Presentation Highlights

When demonstrating this project to examiners or project coordinators:
1. **Show the Stock Update Screen (Figure 4)**: Switch category between *Swimwear*, *Footwear*, and *Accessories*. Show how the sleeve length and lower length dropdowns immediately disable with `NA` and how the variant code banner updates live in real-time.
2. **Show the Barcode Label Printing (ZXing)**: Navigate to *Barcode Labels*, select a job-worker variant, and click *Print Labels*. Demonstrate the generated Code 128 barcode image with printed MRP and tax details.
3. **Demonstrate Vendor Import & USB Scanner**: Go to *Vendor Import*, upload `sample_vendor_stock.csv`, and show how stock is updated without typing. Type or scan a barcode into the USB Scanner terminal to show single-touch stock incrementing.
4. **Show Problem Stock Analytics**: Open *Reports & Alerts* to demonstrate the financial separation of normal vs defective vs old season vs dead stock with recommended clearance markdowns.
5. **Show Role Security**: Log in as `staff` and demonstrate that trying to access `/masters` is securely blocked by `AuthFilter`.
