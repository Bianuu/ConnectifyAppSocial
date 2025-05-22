# 🌟 Social Platform Backend

Bun venit în backend-ul platformei sociale! Acest proiect este construit ca parte a unui proiect academic și reprezintă
un API REST robust care gestionează utilizatorii și postările într-o aplicație socială. 🚀

---

## 📚 Despre Proiect

Această aplicație backend este construită cu **Spring Boot** și alte tehnologii Java moderne, având ca scop
implementarea unui sistem de tip CRUD pentru utilizatori și postări. Oferim:

- Crearea, citirea, actualizarea și ștergerea utilizatorilor și postărilor
- Suport pentru încărcarea imaginilor în postări
- Validări personalizate pentru datele introduse
- Gestionarea erorilor centralizată

---

## 🛠️ Tehnologii Utilizate

- **Java 22** - Limbajul principal
- **Spring Boot** - Framework pentru dezvoltarea backend-ului
- **Spring Data JPA** - Manipularea bazei de date
- **Mockito & JUnit 5** - Pentru testarea unitară
- **Lombok** - Reducerea codului boilerplate

---


---

## ⚙️ Configurare și Rulare

### 1. Cerințe

Asigură-te că ai instalat:

- **JDK 22**
- **Maven**
- **PostgreSQL**
- Un IDE precum **IntelliJ IDEA** (recomandat)

### 2. Configurarea Proiectului

1. Clonează repository-ul:
   ```bash
   git clone https://github.com/Bianuu/ConnectifyAppSocial.git
   ```

2. Configurarea bazei de date:
   Actualizează fișierul `application.properties` cu datele tale:
   ```properties
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/Connectify
       username: postgres
       password: <parola-ta>
   ```

### 3. Construiește și rulează

1. Construiește proiectul:
   ```bash
   mvn clean install
   ```

2. Rulează aplicația:
   ```bash
   mvn spring-boot:run
   ```
   Accesează API-ul la: [http://localhost:3000](http://localhost:3000)

---

## 📋 Exemple de Endpoint-uri API

### 🧑‍🤝‍🧑 Gestionare Utilizatori

- **GET** `/users/all`  
  ➡️ Obține lista tuturor utilizatorilor

- **POST** `/users/insert`  
  ➡️ Creează un nou utilizator  
  **Body JSON**:
  ```json
  {
    "email": "user@example.com",
    "password": "password",
    "username": "username",
    "bio": "Short bio",
    "dateOfBirth": "2000-01-01T00:00:00"
  }
  ```

### 📝 Gestionare Postări

- **GET** `/posts/all`  
  ➡️ Obține toate postările

- **POST** `/posts/insert`  
  ➡️ Creează o postare nouă cu sau fără imagine  
  **Form Data**:
  ```
  content: "Post content"
  userId: <UUID>
  image: <MultipartFile>
  ```

- **DELETE** `/posts/delete/{id}`  
  ➡️ Șterge o postare după `id`

---

## 🧪 Testarea Aplicației

Testele sunt scrise utilizând **JUnit 5** și **Mockito**. Pentru a rula testele:

```bash
mvn test
```

Acestea includ teste pentru:

- Funcționalitatea controlerelor (`UserController`, `PostController`)
- Validările de date
- Interacțiunea cu serviciile

---
