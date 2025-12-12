<div align="center">

# 📱 **Medify**

### *Android application built with Kotlin & Jetpack Compose.*

---

[![Status](https://img.shields.io/badge/Status-Active-4CAF50?style=for-the-badge)]()
[![Android](https://img.shields.io/badge/Framework-Jetpack%20Compose-4285F4?style=for-the-badge)]()
[![Backend](https://img.shields.io/badge/Backend-Supabase-3ECF8E?style=for-the-badge)]()
[![License](https://img.shields.io/badge/License-MIT-4CAF50?style=for-the-badge)]()

---

🚀 **Modify** adalah aplikasi Android modern yang memanfaatkan **Jetpack Compose**, **Supabase**, dan arsitektur **MVVM** untuk menghadirkan pengalaman penggunaan yang clean, responsif, dan modular.

</div>

---

## ✨ **Features**

* 🔐 Authentication (Register, Login, Session Management)
* 👤 Editable User Profile (image upload + Supabase bucket)
* 🧾 Todo List CRUD
* 📦 Jetpack Compose UI with StateFlow
* 🔗 Supabase PostgREST & Auth Integration
* 🗂 MVVM Architecture

---

## ⚙️ **Installation**

### **1️⃣ Clone Repository**

```bash
git clone https://github.com/nowwie/Modify.git
cd Modify
```

### **2️⃣ Open Project in Android Studio**

* Buka Android Studio
* File → Open → pilih folder `Modify/`
* Tunggu Gradle sync selesai

---

## 🧩 **Project Structure**

```txt
Modify/
│
├── app/
│   ├── data/
│   │   ├── repositories/      # Repository layer (Auth, Todo, etc.)
│   │   ├── remote/            # Supabase holders & API config
│   │   └── models/            # Data models
│   │
│   ├── ui/
│   │   ├── screens/           # Compose screens
│   │   ├── components/        # Reusable UI components
│   │   └── theme/             # App theme styles
│   │
│   ├── viewmodel/             # ViewModels (MVVM)
│   └── nav/                   # Navigation graph & routes
│
└── build.gradle               # Dependencies
```

---

## 🧑‍💻 **Development Workflow**

### **1️⃣ Pull sebelum ngoding**

Selalu update project dulu:

```bash
git pull origin main
```

### **2️⃣ Jangan ngoding di branch `main`**

Buat branch baru sebelum kerja:

```bash
git checkout -b fitur-namaKamu
```

Contoh:

```bash
git checkout -b fix-auth-session
```

### **3️⃣ Commit perubahan**

```bash
git add .
git commit -m "deskripsi jelas perubahan"
```

### **4️⃣ Push ke branch**

```bash
git push origin fitur-namaKamu
```

### **5️⃣ Merge ke main hanya lewat Pull Request**

* Buka GitHub repo
* Buat **Pull Request**
* Tunggu review sebelum merge
* Jangan pernah `git push origin main`

Ini untuk mencegah konflik & kerusakan kode.

---

# 📏 **SOP Branching & Naming Convention (Formal)**

## **1. Branch Naming Convention**

Gunakan format:

```
type/short-description
```

**Tipe branch yang diperbolehkan:**

| Tipe        | Kegunaan                               |
| ----------- | -------------------------------------- |
| `feat/`     | Menambah fitur baru                    |
| `fix/`      | Memperbaiki bug                        |
| `refactor/` | Merapikan kode tanpa mengubah behavior |
| `docs/`     | Mengubah dokumentasi                   |
| `style/`    | UI changes, layout fix                 |
| `chore/`    | Update dependency atau config          |

**Contoh branch valid:**

```
feat/profile-screen
fix/supabase-update-crash
style/todo-item-redesign
refactor/auth-repository
```

---

## **2. Commit Message Convention**

Gunakan format:

```
<type>: <deskripsi>
```

**Tipe commit:**

* feat: fitur baru
* fix: bugfix
* refactor: perbaikan struktur
* style: UI / desain
* docs: dokumentasi
* chore: config / dependency

**Contoh commit yang benar:**

```
fix: resolve serialization crash on updateProfile()
feat: add profile picture upload to Supabase bucket
style: improve TodoItem card padding
```

---

## **3. File & Class Naming Convention**

| Jenis          | Format              | Contoh                               |
| -------------- | ------------------- | ------------------------------------ |
| Kotlin class   | PascalCase          | `UserRepository`, `ProfileViewModel` |
| Function       | camelCase           | `loadProfile()`, `uploadImage()`     |
| Compose screen | PascalCase + Screen | `ProfileScreen`, `TodoScreen`        |
| Package        | lowercase           | `data`, `repository`, `nav`          |

**Jangan ubah nama file/class tanpa diskusi**, karena akan mengacaukan import dan struktur MVVM.

---

## **4. Dependency Management Rules**

* Jangan menambahkan dependency tanpa alasan jelas
* Diskusikan dulu sebelum menambah library besar
* Jangan duplikasi versi library
* Periksa apakah dependency berdampak pada minSdk atau build size

---

## 📬 **Contact**

```txt
📧 Email: novazkaam@gmail.com
🐙 GitHub: https://github.com/nowwie
```

<div align="center">

💚 Built with clarity. Developed with consistency.

</div>

---
