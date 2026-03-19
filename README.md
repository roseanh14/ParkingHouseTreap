# Parking House Treap 🚗

A desktop Java application for managing a multi-floor parking house using a **Treap data structure**.

## Features

- 4 parking floors
- 12 parking spots per floor
- Visual GUI for parking layout
- Occupy / Release parking spots
- Find nearest free spot (using Treap)
- Load initial data from file
- Save action logs to file
- Validate Treap structure

---

## Technologies

- Java (Swing)
- Custom Treap (BST + Heap)
- OOP architecture

---

## How to Run

### Option 1 — IntelliJ IDEA

- Open project
- Run `Main.java`

### Option 2 — Terminal

```bash
javac -d out Main.java GUI/*.java Model/*.java Service/*.java Data/*.java Algorithms/*.java
java -cp out Main

