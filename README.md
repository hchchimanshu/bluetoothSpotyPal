# Bluetooth SpotyPal 🔵📍

An Android app built with Kotlin that connects and interacts with SpotyPal Bluetooth devices. It enables scanning, listing, and managing Bluetooth connections while showcasing clean architecture and responsive UI practices.

## 🚀 Features

- 🔍 Scan for nearby SpotyPal Bluetooth devices
- 📡 Establish and manage BLE connections
- 📋 Display available devices in a list
- 🔄 Refresh/restart scanning seamlessly
- 🔔 Show device details and status in real-time

## 🧠 Tech Stack

- **Language**: Kotlin
- **Bluetooth**: BLE (Bluetooth Low Energy)
- **UI**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Permissions**: Runtime handling for Bluetooth & Location

## 🛠 Project Structure

bluetoothSpotyPal/
├── MainActivity.kt # Entry point with Compose UI
├── viewmodel/
│ └── BluetoothViewModel.kt # Core logic for device scanning and state
├── utils/
│ └── BluetoothUtils.kt # BLE helper functions
├── model/
│ └── BluetoothDeviceInfo.kt # Data class representing devices


## 🧪 How to Run

1. **Clone the repo:**

git clone https://github.com/hchchimanshu/bluetoothSpotyPal.git
Open in Android Studio

Run on a real device (Bluetooth permissions needed)

⚠️ BLE scanning requires Location permission and a physical device (emulator does not support Bluetooth).

🔐 Permissions
Ensure the following permissions are handled:
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
🙋 Author
Himanshu HC
🔗 LinkedIn
