# Parking House Treap 🚗

A desktop Java application for managing a multi-floor parking house using a **Treap (Binary Search Tree + Heap)** data structure.

This project demonstrates practical usage of advanced data structures in a real-world scenario.

---

## Overview

The application simulates a parking house consisting of multiple floors, where each floor is represented by a **separate Treap instance**.

Each parking spot is stored as:
- **Key (K):** parking spot number
- **Priority:** randomly generated value
- **Value (V):** vehicle information (e.g., ID, license plate)

---

## Features

- 4 parking floors (each with its own Treap)
- 12 parking spots per floor
- Graphical user interface (Swing)
- Occupy / release parking spots
- Check if a spot is occupied
- List occupied spots in sorted order
- Find the nearest free parking spot
- Load initial data from file
- Save logs to file
- Validation of Treap properties (BST + Heap)

---

## Algorithms

### Treap Operations
- Insert (with rotations)
- Delete (with rotations)
- Search
- In-order traversal (sorted output)

### Nearest Free Spot
If the requested spot is:
- **free → returned immediately**
- **occupied → algorithm finds predecessor & successor**
  and selects the closest available spot

---

## Technologies

- Java
- Swing (GUI)
- Custom Treap implementation
- Object-Oriented Design

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

