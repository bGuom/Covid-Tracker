package com.mobicom.covidtracker.Const;

public class Config {

    //How long will splash screen stay before closing ( how many seconds * 1000)
    // 3000 = 3 seconds
    public static final int SPLASH_DISPLAY_LENGTH = 3000;

    //Show Intro screens on First app open
    public static final boolean SHOW_INTRO = true;

    //Will display a loading animation in the center of the screen
    public static boolean ENABLE_CENTER_LOADING =true;


    //Will be able to view pdf inside the app
    public static boolean ENABLE_PDF_VIEW =true;

    //Will be able to open dialer from clicking on phone numbers in the website
    public static boolean ENABLE_PHONE_DIALER =true;

    public static boolean ENABLE_WHATSAPP_SHARE =true;



    //Remove the website footer to give more app like feeling
    public static boolean REMOVE_FOOTER = true;



    public static boolean ENABLE_ADS = false;

    public static boolean ENABLE_FULL_ADS = false;


    // How much seconds to wait after first open of the App to show Full Screen Ads
    public static int FULL_AD_DELAY_SEC = 60;


    //Please only Enable one of the below. Enabling many may results in break of AdMob Rules
    public static boolean TOP_AD = false;

    public static boolean TOP_OVERLAY_AD = false;

    public static boolean BOTTOM_AD = false;

    public static boolean BOTTOM_OVERLAY_AD = false;





}
