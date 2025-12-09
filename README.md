❤️ Heart Rate Estimation App
Non-invasive Heart Rate Monitoring using Smartphone Camera, Computer Vision & Machine Learning


📌 Overview
The Heart Rate Estimation App is an Android application that estimates a user's heart rate using only the smartphone camera. The app processes video frames, analyzes facial color variations, and sends extracted data to a server-based ML model to predict heart rate with improved accuracy.
Built using Kotlin + Jetpack Compose, the app integrates modern Android technologies including:

CameraX – High-quality, device-compatible video recording
Google ML Kit – Face detection & contour segmentation
Firebase Authentication & Storage – Secure login + cloud video storage
Ngrok Tunnel – Secure communication with server
Server ML Model (IIT Indore) – Processes RGB time-series to estimate heart rate


⭐ Features
🔒 Secure Login & Signup using Firebase Auth
🎥 CameraX video recorder with smart bounding box
🙂 Real-time face detection – starts recording only when face is perfectly aligned
🗂️ Cloud-based Gallery – fetch recorded videos from Firebase
📊 Frame-by-Frame Image Processing
🌈 RGB extraction of facial regions
🔗 Server-side ML inference for accurate heart rate prediction
📱 Modern Jetpack Compose UI


🏗️ Architecture
1. User Interface Layer (Jetpack Compose)

Login / Register
Home Screen
Camera Screen (with live bounding box)
Gallery Screen
Video Player & Analysis Screen

2. Authentication & Storage Layer
Firebase Authentication
Firebase Cloud Storage for user video 

3. Video Capture Layer
CameraX API
ML Kit face detection before & during recording

4. Image Analysis Layer
Face isolation (masking background)
Contour detection
Face region divided into grid boxes
RGB extraction (green channel emphasized)
Time-series creation from per-frame RGB data

5. Server & ML Prediction Layer
Processed RGB array → sent via HTTP POST
Server hosted ML model (IIT Indore)
Ngrok for secure URL endpoint
Returns predicted heart rate


🧠 How It Works (Pipeline)
User logs in
Records video with face properly aligned
Frames → Face isolated → Contour extracted
Grid segmentation → RGB values computed
Time-series sent to ML server
Model predicts heart rate
Result displayed in app


🚀 Installation (Developer Setup)
Prerequisites

Android Studio Flamingo or newer
Firebase project
Ngrok account (for server tunneling)
ML model hosted on server


Steps
Clone the repository
git clone https://github.com/your-username/your-repo.git

Open in Android Studio
Add your google-services.json in /app
Configure Firebase Authentication & Storage
Set your server/ngrok URL in the networking file
Run app on a real Android device (recommended)


📈 Performance
✔️ Higher accuracy vs traditional video-based apps
✔️ Robust face detection reduces background noise
✔️ Server-side ML handles lighting variations better
✔️ Works across wide range of Android devices


⚠️ Limitations
Dependent on stable internet (server processing)
Sensitive to lighting changes
Requires still head position during recording
Lower performance on low-end devices for HD videos


🔮 Future Improvements
On-device ML to remove internet dependency
Multi-parameter health monitoring (SpO2, respiration, stress)
Automatic lighting normalization
Integration with wearables and fitness platforms
AI-based motion stabilization



📄 License
This project is for academic & research use. Add a license if required.












