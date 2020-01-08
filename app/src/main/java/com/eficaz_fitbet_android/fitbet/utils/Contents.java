package com.eficaz_fitbet_android.fitbet.utils;

import android.Manifest;

public interface Contents {
    String REG_KEY = "reg_key";
    String REG_KEY1 = "reg_key";

    String USER_PROFILE_REG_KEY = "prifile_reg_key";

    String REG_WITH_F_OR_G = "f_or_g";

    String FCM_TOKEN = "fcm_token";

    String BAT_POSICTION = "0";

    String DASH_BOARD_POSICTION = "DASH_BOARD_POSICTION";

    String BET_PAGE_POSICTION = "BET_PAGE_POSICTION";

    String BET_START_STATUS = "BET_START_STATUS";

    String UPDATE_METER = "meter";

    String CREATE_BET_STATUS = "create_bet_status";

    String START_STATUS = "start_status";

    String GROUP_ID="groupid";

    String NAME="name";

    String DESCRIPTION="description";

    String GROUP_IMAGE="groupimage";

    String CREAT_DATE="createdate";

    String FOR_START_BET_LAT = "lat";
    String FOR_START_BET_LOG = "log";

    String BASEPATH="basepath";

    String STATUS="status";

    String USERS="users";

    String FIRST_NAME="firstname";

    String EMAIL="email";

    String CREDIT_SCORE="creditScore";

    String WON="won";

    String LOCATION="location";

    String START_LAT="lat";
    String START_LOG="log";
    String END_LAT="lat";
    String END_LOG="log";

    String LEGS="legs";

    String OVERVIEW_POLYLINE="overview_polyline";

    String ROUTES="routes";

    String POINTS="points";

    String START_LOCATION="start_location";

    String END_LOCATION="end_location";

    String LOST="lost";

    String COUNTRY="country";

    String PROFILE_PIC="profile_pic";

    String END_address="end_address";
    String START_address="start_address";

    String REG_TYPE="regType";

    String IMAGE_STATUS="image_status";

    String DISTANCE="distance";

    String Remaindistance="remaindistance";

    String TOTAL_DISTANCE="totaldistance";

    String AVERAGE_DISTANCE="averagedistance";

    String AVERAGESPEED="averagespeed";

    String DASH_BOARD_WEEK="dashboardweek";

    String WEEK_DISTANCE="distance";

    String DAY="day";

    String SPEED="speed";

    String DASH_BOARD_USERS="Users";

    String DASH_BOARD_FIRSTNAME="firstname";

    String DASH_BOARD_COUNTRY="country";

    String DASH_BOARD_MONTH="dashboardmonth";

    String DASH_BOARD_SIX_MONTH="dashboard6month";

    String DASH_BOARD_YEAR="dashboardyear";

    String MONTH="month";

    String DASH_BOARD_CREDIT_SCORE="creditScore";

    String DASH_BOARD_WON="won";

    String DASH_BOARD_LOST="lost";

    String COUNTRY_NAME="country_name";

    String MYBETS="Bets";



    String MYBETS_betid="betid";

    String MYBETS_betname="betname";
    String MYBETS_description="description";

    String MYBETS_date="date";
    String MYBETS_time="time";

    String MYBETS_enddate="enddate";
    String MYBETS_EDIT_OR_CREATE="edit_or_cer";

    String MYBETS_BET_ID="bet_id";

    String MYBETS_distance="distance";

    String MYBETS_startlocation="startlocation";
    String MYBETS_endlocation="endlocation";

    String MYBETS_startlongitude="startlongitude";
    String MYBETS_endlongitude="endlongitude";

    String MYBETS_startlatitude="startlatitude";
    String MYBETS_endlatitude="endlatitude";

    String MYBETS_route="route";
    String MYBETS_credit="credit";

    String MYBETS_winner="winner";
    String MYBETS_status="status";
    String MYBETS_upcoming="upcoming";

    String STATUS_A="Status";

    String LAT="lat";
    String LNG="lng";


    String MYBETS_createdate="createdate";
    String MYBETS_createdby="createdby";

    String MYBETS_bettype="bettype";
    String MYBETS_challengerid="challengerid";

    String MYBETS_started="started";
    String MYBETS_editstatus="editstatus";

    String START_Address="start_address";
    String END_Address="end_address";


    String MYBETS_betindividuals="betindividuals";

    String MYBETS_betbetgrops="betbetgrops";

    String MYBETS_betgrops="betgrops";

    String TOTAL_PARTICIPANTS="totalparticipants";

    String TOTAL_MESSAGE="totalmessage";

    String WINNER_description="winner_description";

    String WINNER_CREDIT="winner_credit";

    String PARTICIPANTS="participants";

    String MESSAGES="Messages";

    String MESSAGE="message";

    String SENDDATE="senddate";

    String SENDER="sender";

    String TOTAL="total";

    String RESULT="result";

    String GEOMETRY="geometry";

    String FILE_PATH="file_path";

    String FILE_TYPE="filetype";

    String PARTICIPANT="participant";

    String POSITION="position";

    String START_DATE="startdate";

    String POSITION_LONGITUDE="positionlongitude";

    String POSITION_LATITUDE="positionlatitude";

    String DEFAULT_COUNTRY="Australia";

    String DEFAULT_COUNTRY_SHORT_NAME="AU";

    String DEFAULT_CURRENCY="AUD";

    String BETDETAILS="betdetails";
    String ROUTE="route";

    String USER_start_longitude="userstartlongitude";

    String NOTIFICATION_CHANNEL_ID = "FITBET";

    String USER_start_latitude="userstartlatitude";

    String pass_startlatitude="pass_startlatitude";

    String pass_startlongitude="pass_startlongitude";

    String pass_endlatitude="pass_endlatitude";

    String pass_endlongitude="pass_endlongitude";

    String pass_join_details_count="join_detail_count";

    String bet_edit_details="bet_edit_details";

    String WINNER_PARTICIPANT="winnerparticipant";


    int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    int CHOOSER_PERMISSIONS_REQUEST_CODE = 7459;
    int CAMERA_REQUEST_CODE = 7500;
    int CAMERA_VIDEO_REQUEST_CODE = 7501;
    int GALLERY_REQUEST_CODE = 7502;
    int DOCUMENTS_REQUEST_CODE = 7503;

    String[] MULTI_PERMISSIONS = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.INSTALL_SHORTCUT,
            Manifest.permission.GET_ACCOUNTS,
            android.Manifest.permission.CAMERA
    };



}
