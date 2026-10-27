# Pharos On-Chain Alert Bot

An intelligent agent developed in **Java** for the Pharos Agent Arena designed to watch Pharos network transactions for configurable conditions (e.g., large transfers or price thresholds) and deliver detailed notifications to channels like Telegram or Discord without repeat spam.

## 📋 Requirements Met

1. **Configurable Alert Condition (Req. 01):** Supports custom thresholds, such as filtering for large token transfers exceeding a specified amount.
2. **Channel Delivery (Req. 02):** Formats and dispatches alerts suitable for Discord webhooks or Telegram bot integration.
3. **Comprehensive Alert Details (Req. 03):** Each alert includes what happened, the transfer value, addresses, and a direct explorer link.
4. **Anti-Spam Protection (Req. 04):** Tracks processed transaction hashes to ensure no repeat spam for the same event.

## 🚀 How to Run

1. Make sure you have **Java JDK** installed.
2. Compile the file:
   ```bash
   javac OnChainAlertBot.java
