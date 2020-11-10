# Covid-Tracker
Contact Tracing Android Application 

## Abstract

COVID 19 is a global pandemic faced by many countries. It is an infectious disease caused by a novel coronavirus. Currently there are nearly 15 million covid 19 patients worldwide and the number is increasing everyday. This virus spreads from person to person through droplets generated when an infected person coughs,sneezes or talks. Healthy person in close proximity to an infected person could also get infected by breathing the virus droplets or by touching a contaminated area and then touching sensitive areas like eyes, nose or  mouth. 

To reduce the spread of COVID 19, it is advised to follow proper health and sanitization practices. Also it is important to promote social distancing as infectious virus droplets can not go more than a few feets in the air due to their weight. However it is difficult to keep social distancing in day to day life. As a solution to this problem, we came up with a mobile application to promote social distancing. Also this app can be used for contact tracing, So it will be easy to track  close contacts of a Covid 19 positive patient. 


## 1 Introduction

### 1.1 Background of the application domain/ problem

COVID 19 is a huge problem faced by countries like Sri Lanka. It is important to prevent the spread of the virus through society. To accomplish this goal, social distancing and proper health practices need to be promoted among the general public.  This project is intended to develop a mobile application to fight Covid-19 pandemic in Sri Lanka. This application will promote social distancing practice among users. The App will be able to trigger an alarm when two persons are closer than 1.5miters distance. Also it will track and store user interactions with other users. Thus those stored data may help to track all the users who have interacted with a Covid 19 positive patient.

### 1.2 Proposed Solution	

The proposed application is intended for use in the health and privacy domain. This mobile application will promote social distancing practice among users. The App will be able to trigger an alarm when two persons are closer than 1.5meters distance. Also it will track and store user interactions with other users for three weeks. Thus those stored data may help to track all the users who have interacted with a Covid 19 positive patient. Therefore it may help to make the quarantine process more efficient as well as effective. 

The system has basically two main components containing many sub components. A mobile application and a web server where it can store user ids and broadcast notifications and covid possessive patient ids to installed apps.. The application is basically developed as a solution to the social distancing issue and the covid 19 patient’s contact tracing app.

Most important feature of this system is that it does not send or collect user location data on a cloud server. Thus ensuring user data privacy and security. 
Originally it was proposed to use Bluetooth MAC address as a unique identifier or device which has active Bluetooth connection. 

Nearby users with active bluetooth connection will be identified as one of the following two encounters.

Close contact (Patient’s device was very close 0m -2m)
Same premises (Patient’s device has’t encountered a close contact, but it was within the range of bluetooth signal strength )

Depending on the category, the app users will be notified with guidance how to act to take the correct quarantine process and prevent Covid 19 from spreading to others. Also they will get required medical instruction regularly. But it’s solely their responsibility to respect these warnings and act accordingly. 

## 2 Application Flow with Screenshots

  ### 2.1 Download and install application

   Users can download and install the latest release of the application from the github repository. Application size is less than 10Mb.


   ### 2.2 Run the application for the first time

   Users can run the application by tapping on the CovidTracker icon in the home screen. Video of app usage can be found at the below link 

    https://drive.google.com/file/d/1_14XdcvrQxXOtfI1to3wlzNZ0o2XmPAO/view?usp=sharing
    
   ### 2.3 Screens
   
   #### Language selection
   This app supports both English and Sinhala language to improve usability of the app. Users can select the preferred language from the language selection screen.

   <img src="https://raw.githubusercontent.com/bGuom/Covid-Tracker/master/ScreenShots/2.jpg" width="240">
   
   #### App intro screens. 
   App will introduce the app functionality to the users through intro screens.
   
   <img src="https://raw.githubusercontent.com/bGuom/Covid-Tracker/master/ScreenShots/3.jpg" width="240">
   
   #### Bluetooth Permission screen
   This app works with Bluetooth technology and users need to enable Bluetooth at all times when they go out. 
   
   <img src="https://raw.githubusercontent.com/bGuom/Covid-Tracker/master/ScreenShots/4.jpg" width="240">
   
   #### Privacy secured
   This app uses a special mechanism to preserve user data security. (Description in the next section). 
   
   <img src="https://raw.githubusercontent.com/bGuom/Covid-Tracker/master/ScreenShots/5.jpg" width="240">
   
   #### Join Screen
  In this screen user can join with the CovidTracker app. Optionally they can provide their mobile number. Devices need to have an active internet connection for this   step. CovidTracker application will generate a unique identifier for the device and save it in the Firebase Firestore database. Also the application will retrieve     the secret keys from the database and save it in the local app memory.
  
  <img src="https://raw.githubusercontent.com/bGuom/Covid-Tracker/master/ScreenShots/6.jpg" width="240">

   #### Phone number verification
   If the user decided to provide their contact number in the earlier step. They will be asked to verify the contact number using a verification code sent to their      phone. Google Firebase Phone Number Authentication is used for this verification process. Application will automatically detect the incoming message and auto          verify    the number. If not, users need to manually enter the correct verification code received to their device.

  <img src="https://raw.githubusercontent.com/bGuom/Covid-Tracker/master/ScreenShots/7.jpg" width="240">

   #### User Dashboard
  This is the main screen the user will see after opening the app after completing the first open setup. There are three main sections in the dashboard. 
  Social Distancing Alerts
  Contact Tracing 
  Covid Stats
  
  
  <img src="https://raw.githubusercontent.com/bGuom/Covid-Tracker/master/ScreenShots/8.jpg" width="240">
  <img src="https://raw.githubusercontent.com/bGuom/Covid-Tracker/master/ScreenShots/9.jpg" width="240">

   
## 3 Materials

This application is built for the Android platform using available tools and technologies. Android studio was used for coding and JAVA language was used for programming. Graphics were designed using Adobe Fireworks and animation was obtained from AirBnb Lottie Platform. 

For the serverless backend, Google Firebase is used. Firebase free tier is used for the demonstration and testing of the app. Following firebase services are used for various parts of the system. 

Firebase Firestore Database
Firebase Realtime Database
Firebase Authentication
Firebase Analytics
Firebase Cloud Messaging
Firebase ML Kit

## 4 Unique Device ID 

### 4.1 Use of Unique ID

One of the main functionality of the app is contact tracing. App runs bluetooth search for every 1 minutes and records every device around it in a local database. To record and to uniquely identify these devices, an unique identifier is required. When the authorities found a new Covid 19 positive patient, they read their device unique identifier and broadcast it as a notification. CovidTracker applications installed by other users will receive this notification and then cross check the id with the contact history of the user stored in the local database. 

### 4.2 Problem with Bluetooth MAC address

Initially we planned to use the device bluetooth MAC address as the unique identifier. However it was not possible due to Android security constraints. According to the Android developer documentation [3] from Android 6.0 and upwards, Android has removed programmatically access to the device's local hardware identifier for apps using the Wi-Fi and Bluetooth APIs. Therefore it was not possible to read the real bluetooth MAC address of the device. It always returns a constant value of 02:00:00:00:00:00.

Furthermore, Android broadcasts a random MAC address for every new network scan. Therefore publicly visible MAC addresses are randomly generated values. This makes it impossible to use a bluetooth MAC address as a unique identifier. 

### 4.3 Solution - Generated Unique identifier 

As a solution to this issue we used different approaches which ensure privacy and also fulfill the requirement.

In CovidTracker application  we use two main types of unique keys.

#### 4.3.1 Diagnosis Key
This key is the unique identifier shared with the health authorities. This key is not visible or directly shared with external users while Bluetooth broadcasts. This is a randomly generated GUID. This key is generated at the time of the user joining the CovidTracker application. Generated diagnosis key is stored both inside the application and in a remote server owned by health authorities (Currently in Firebase Firestore database). 

#### 4.3.2 Broadcast Keys
For security and privacy concern CovidTracker application will not broadcast the Diagnosis key. Application uses AES (Advanced Encryption Standard)  Encryption to encrypt the Diagnosis key into four different Broadcast keys using four different secrets. Then one of the broadcast keys will be randomly selected at Bluetooth broadcast. This multi key method was used to prevent tracking users using the unique identifier by external parties.

Received device will decrypt the broadcast key and will convert it to the original diagnosis key. Then this key will be stored along with the contact records.


### 4.4 Bluetooth Broadcast Payload

CovidTracker app installed devices will broadcast a payload with multiple data fields which will be useful for the receiving device to calculate the distance and to keep contact records. Following are the important fields included in the broadcast payload.

One of randomly selected broadcast key
SecretKeyId used for encryption. (Not the secret key)
Data required for classification model (battery level, bluetooth version ,etc)


### 4.5 Secure Encryption Mechanism

CovidTracker app uses AES encryption to ensure the data privacy. Following diagram indicates the process of Encryption and Decryption.


### 4.6 Social Distance Alert

The developed application is capable of identifying the average distance of nearby devices that are not installed CovidTracker application.What we did here is we got a threshold value of RSSI value.According to the online site[4] ,(0-70) dBm is good signal strength so we less than 70dBm value will give a alert to the CovidTraker application user.By this method we are able to notify the user(by mobile vibration), even he closer to the someone who has bluetooth device but not having the CovidTracker application. 

If both persons are having the CovidTracker application then Alert will give accurately and the received device ID will save into the database for further Alert.



### 4.7 Contact Tracing Risk Classification


In the CovidTracker application we have categorized the risk into 3


High Risk - User had close contact (0-2m range) with covid-19 positive patients and their Device ID is in the user's local storage.

Medium Risk - User had no close contact with covid-19 patients but his User ID is in the user's local storage.which means covid-19 patient was nearby but not in the range of 0-2m.

Low Risk - User had close contact or user was in the Bluetooth range, but those Device  ID’s are not labeled as covid positive patients. 



### 4.8 Covid 19 Stats

Covid 19 Stats was obtained using API available at https://hpb.health.gov.lk/en/api-documentation

# 5. Conclusion

As various environmental factors affect the received Bluetooth signals, Bluetooth signal strength (RSSI) value cannot be used for accurate distance measurements. RSSI value directly depends on device battery level and device type. And also walls,floor divisions and other physical obstacles also reduce the received signal strength. Even Though we can not  account for physical  obstacles or other device related factors can be analysed using machine learning techniques. Also contact tracing and user data broadcasting need to be done in a secure manner and protect user privacy at all times. This application will be very effective and helpful to prevent Covid-19 if most of the population use this CovidTraker Application.

 

