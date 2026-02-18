# IntegrityEconomy

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](./LICENSE)  
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()  
[![Contributions](https://img.shields.io/badge/Contributions-Welcome-orange.svg)]()

---

## 💰 Overview

**IntegrityEconomy** is a lightweight and flexible economy plugin for Minecraft servers, featuring EssentialsX-style commands for managing balances, transfers, and account info.

---

## ⚙️ Main Commands

| Command                | Description | Usage                                        | Permission |
|------------------------|-------------|----------------------------------------------|------------|
| `/eco` `economy` `/ie` | Administrative command to modify a player’s balance | `/eco <give/reset/remove/set> <player> [amount]` | `integrity.eco` |
| `/balance` `/bal`      | Displays the player’s current balance | `/balance <player>`                          |            |
| `/pay`                 | Sends money to another player | `/pay <player> <amount>`                         |            |

---

## 🛠️ Build Guide

To build the plugin locally:

1. Clone the repository:
   ```bash
   git clone https://github.com/IntegrityMC/IntegrityUpdater.git
   ```
2. Navigate into the project directory:
   ```bash
   cd IntegrityEconomy
   ```
3. Build using **Maven**:
   ```bash
   gradle shadowJar
   ```
4. The compiled `.jar` file will be located in:
   ```
   build/libs/IntegrityEconomy.jar
   ```

---

## 💡 Contributing

Contributions are welcome!  
To submit a **pull request**, follow these steps:

1. **Fork** the repository
2. Create a new branch for your feature or fix:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Make your changes and ensure code quality and style consistency
4. Commit your changes with a clear message:
   ```bash
   git commit -m "Added new features"
   ```
5. Push to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```
6. Open a **Pull Request** describing your changes

---

## ⚖️ Licensing Agreement for Contributors

By contributing to **IntegrityEconomy**, you agree to the following terms:

* **Initial License:** All contributions are submitted under the MPL 2.0 (Mozilla Public License 2.0).
* **Relicensing Rights:** By submitting a Pull Request, you grant the project owner organization (IntegrityMC) the absolute and irrevocable right to redistribute your contributions under any other license in the future.
* **Scope of Changes:** This includes, but is not limited to, changing the project license to:
    * **Strong Copyleft:** GNU GPL (v3 or later);
    * **Permissive:** MIT, Apache 2.0, or BSD;
    * **Proprietary:** Closed-source or commercial licenses.
* **Discretion:** Such changes can be made at the organization's sole discretion without further notice to or consent from the contributors.

This agreement ensures that the IntegrityMC organization maintains full flexibility to evolve the project and its licensing model as needed to protect the software and its community.

---

## 🐞 Reporting Issues

If you find a bug, please open an [**Issue**](../../issues) including:

- Steps to reproduce the issue
- Expected vs actual behavior
- Any relevant logs or screenshots

---

## 📜 License

This project is licensed under the **GNU General Public License v3.0 (GPLv3)**.  
See the [LICENSE](./LICENSE) file for full details.

---

![bStats](https://bstats.org/signatures/bukkit/IntegrityEconomy.svg)
