package com.androidapp.fitbet.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetroInterface {
    /*@POST("login")
    Call<ResponseBody> LoginTrack(@Body JsonObject main);*/

    @GET("login")
    Call<ResponseBody> LoginTrack( @Query("Email") String username,
                                   @Query("Password") String password,
                                   @Query("DeviceID") String deviceID,
                                   @Query("DeviceName") String DeviceName);

    @GET("signup/google")
    Call<ResponseBody> LoginTrackWithGoogle( @Query("Firstname") String Firstname,
                                             @Query("Email") String Email,
                                             @Query("DeviceID") String DeviceID,
                                             @Query("googleID") String googleID,
                                             @Query("Picture") String Picture,
                                             @Query("Country") String Country,
                                             @Query("Countrycode") String CountryCode,
                                             @Query("DeviceName") String DeviceName);

    @GET("signup/fb")
    Call<ResponseBody> LoginTrackWithfb( @Query("Firstname") String Firstname,
                                             @Query("Email") String Email,
                                             @Query("DeviceID") String DeviceID,
                                             @Query("FacebookID") String fbID,
                                             @Query("Picture") String Picture,
                                             @Query("Country") String Country,
                                             @Query("Countrycode") String CountryCode,
                                             @Query("DeviceName") String DeviceName);

    @GET("signup")
    Call<ResponseBody> SignUpTrack( @Query("Firstname") String username,
                                    @Query("Email") String email,
                                    @Query("DeviceID") String deviceID,
                                    @Query("Password") String password,
                                    @Query("Country") String CountryName,
                                    @Query("Countrycode") String CountryCode,
                                    @Query("DeviceName") String DeviceName);

    @GET("group/addgroup?")
    Call<ResponseBody> AddGroup(@Query("name") String username,
                                   @Query("description") String password,
                                   @Query("reg_key") String deviceID);

    @GET("group/edit?")
    Call<ResponseBody> UpdateGroup(@Query("groupid") String groupid,
                                   @Query("name") String username,
                                   @Query("description") String password,
                                   @Query("reg_key") String deviceID);

    @GET("group/deletegroup?")
    Call<ResponseBody> DeleteGroup(@Query("SelectedGroups") String array);



    @Multipart
    @POST("group/image_upload?")
    Call<ResponseBody> updateProfile(@Part MultipartBody.Part file,@Part("groupid") RequestBody  groupid,@Part("reg_key") RequestBody  regkey);


    @GET("group?")
    Call<ResponseBody> GroupList(@Query("reg_key") String regkey,
                                @Query("search") String search);



    @GET("group/invitemembers?")
    Call<ResponseBody> InviteGroupList(@Query("groupid") String g_id,
                                 @Query("search") String search);


    @GET("group/groupdetail?")
    Call<ResponseBody> InviteGroupDeatails(@Query("groupid") String g_id);


    @GET("group/addinvitemember?")
    Call<ResponseBody> AddInviteMember(@Query("groupid") String g_id,@Query("reg_key") String reg_key);


    @GET("group/deletegroupusers?")
    Call<ResponseBody> DeleteInviteMember(@Query("SelectedUsers") String array,@Query("groupid") String g_id);


    @GET("bet/add?")
    Call<ResponseBody> AddBet(  @Query("betname") String betname,
                                @Query("description") String description,
                                @Query("date") String date,
                                @Query("enddate") String enddate,
                                @Query("distance") String distance,
                                @Query("startlocation") String startlocation,
                                @Query("endlocation") String endlocation,
                                @Query("startlongitude") String startlongitude,
                                @Query("endlongitude") String endlongitude,
                                @Query("startlatitude") String startlatitude,
                                @Query("endlatitude") String endlatitude,
                                @Query("route") String route,
                                @Query("credit") String credit,
                                @Query("reg_key") String reg_key,
                                @Query("bettype") String bettype);


  @GET("bet/edit?")
    Call<ResponseBody> UpdateBet(  @Query("betname") String betname,
                                @Query("description") String description,
                                @Query("date") String date,
                                @Query("enddate") String enddate,
                                @Query("distance") String distance,
                                @Query("startlocation") String startlocation,
                                @Query("endlocation") String endlocation,
                                @Query("startlongitude") String startlongitude,
                                @Query("endlongitude") String endlongitude,
                                @Query("startlatitude") String startlatitude,
                                @Query("endlatitude") String endlatitude,
                                @Query("route") String route,
                                @Query("credit") String credit,
                                @Query("reg_key") String reg_key,
                                @Query("bettype") String bettype,
                                @Query("betid") String betid);


    @GET("user/dashboardyear?")
    Call<ResponseBody> DashboardYear(@Query("reg_key") String regkey);



    @GET("user/dashboardmonth?")
    Call<ResponseBody> DashboardMonth(@Query("reg_key") String regkey);


    @GET("bet/betcancel?")
    Call<ResponseBody> CanceledBet(@Query("betid") String regkey,
                                   @Query("reg_key") String description,
                                   @Query("message") String date);



    @GET("user/dashboardweek?")
    Call<ResponseBody> DashboardWeek(@Query("reg_key") String regkey);



    @GET("user/dashboard6month?")
    Call<ResponseBody> Dashboard6Month(@Query("reg_key") String regkey);


    @GET("user/dashboard?")
    Call<ResponseBody> DashboardDetails(@Query("reg_key") String regkey);

    @GET("bet/skipwinner?")
    Call<ResponseBody>SkipWinner(@Query("reg_key") String regkey,@Query("betid") String betId,@Query("description") String description);

    @GET("Adminsettings/getcharge?")
    Call<ResponseBody> GetAdminCharge();

    @GET("user/logout?")
    Call<ResponseBody> LogOutApi(@Query("reg_key") String regkey);


    @GET("bet/betindividual?")
    Call<ResponseBody> MybetsUserInvite(@Query("betid") String betId);


    @GET("user/updatesetting?")
    Call<ResponseBody> UpdateSettingsDetails(@Query("name") String name,
                                             @Query("country") String country,
                                             @Query("Countrycode") String CountryCode,
                                             @Query("reg_key") String regkey);


    @GET("bet/upcomingbet?")
    Call<ResponseBody> UpcommingBet(@Query("reg_key") String regkey,
                                    @Query("search") String search);


    @GET("bet/archivebet?")
    Call<ResponseBody> Archivebet(@Query("reg_key") String regkey,
                                  @Query("search") String search);

    @GET("bet?")
    Call<ResponseBody> MyBets(@Query("reg_key") String regkey,
                              @Query("search") String search);

    @GET("bet/addbetindividual?")
    Call<ResponseBody> MybetUserInvite(@Query("reg_key") String regkey,
                                       @Query("betid") String betId);


    @GET("bet/betgrouplist?")
    Call<ResponseBody> MybetbetGroup(@Query("betid") String betId);


    @GET("bet/joinbet?")
    Call<ResponseBody> JoinBet(@Query("betid") String betId ,
                               @Query("reg_key")String regkey);

    @GET("bet/addbetgroup?")
    Call<ResponseBody> Addbetgroup(@Query("betid")String regkey,
                                   @Query("groupid") String betId);


    @GET("bet/startbet?")
    Call<ResponseBody> StartBet(@Query("challengerid") String challengerid ,
                                @Query("distance")String distance,
                                @Query("positionlatitude")String positionlatitude,
                                @Query("positionlongitude")String positionlongitude,
                                @Query("reg_key")String reg_key);



    @GET("bet/betdetail?")
    Call<ResponseBody> Betdetail(@Query("betid") String betId ,
                               @Query("reg_key")String regkey);

    @GET("bet/declineinvite?")
    Call<ResponseBody> declineInvite(@Query("reg_key") String regKey ,
                                 @Query("betid") String betId);

    @GET("bet/betloserdetails?")
    Call<ResponseBody> LoserDetail(@Query("betid") String betId ,
                                 @Query("reg_key")String regkey);

    @GET("message?")
    Call<ResponseBody> MessageList(@Query("betid") String betId);

    @GET("message/send?")
    Call<ResponseBody> SendMessage(@Query("betid") String betId ,
                                   @Query("reg_key")String regkey,
                                   @Query("message")String message);

    @GET("bet/archivebet?")
    Call<ResponseBody> Archivebet(@Query("reg_key") String regkey);

    @GET("bet/listjoinedbet")
    Call<ResponseBody> Listjoinedbet(@Query("reg_key") String regkey);

    @GET("bet/archivebetdetail?")
    Call<ResponseBody> Archivebetdetail(@Query("betid") String betId);



    @GET(" user/updatepassword?")
    Call<ResponseBody> UpdatePassword(@Query("password") String betId,
                                      @Query("reg_key")String regkey);


    @Multipart
    @POST("profile/image_upload?")
    Call<ResponseBody> UploadProfilePic(@Part MultipartBody.Part file,
                                        @Part("reg_key") RequestBody  groupid);

    @Multipart
    @POST("bet/winner?")
    Call<ResponseBody> UploadVideoOrImage(@Part MultipartBody.Part file,@Part("reg_key") RequestBody  groupid,@Part("betid") RequestBody  betid,@Part("filetype") RequestBody  filetype,@Part("description") RequestBody  description);

  /*  @Multipart
    @POST("bet/winner?")
    Call<ResponseBody> UploadVideoOrImage(@Part("file") RequestBody file,
                                        @Part("reg_key") RequestBody  groupid,@Part("betid") RequestBody  betid,@Part("filetype") RequestBody  filetype);*/


    @GET("stripeController/stripeTransfer?")
    Call<ResponseBody> StripeTransfer(@Query("country") String country,
                                      @Query("email") String email,
                                      @Query("amount") String amount,
                                      @Query("currency") String currency,
                                      @Query("account_number") String account_number,
                                      @Query("routing_number") String routing_number,
                                      @Query("account_holder_name") String account_holder_name,
                                      @Query("credit") String credit,
                                      @Query("stripe_charge") String stripe_charge,
                                      @Query("admin_charge") String admin_charge,
                                      @Query("reg_key") String reg_key);



    @GET("bet/livestartedbetdetail?")
    Call<ResponseBody> LiveBetDetails(@Query("reg_key") String regkey);

    @GET("bet/readytostartbetlist?")
    Call<ResponseBody> JoinBetList(@Query("reg_key") String regkey);




    @GET("bet/livestartbet?")
    Call<ResponseBody> LiveDetailsUpdation(@Query("challengerid") String challengerid ,
                                @Query("distance")String distance,
                                @Query("positionlongitude")Double positionlongitude,
                                @Query("positionlatitude")Double positionlatitude,
                                @Query("reg_key")String reg_key,
                                @Query("bettype")String bettype,
                                @Query("route")String route);




    @GET("json?")
    Call<ResponseBody> MapDetails(@Query("origin") String origin,
                                  @Query("destination") String destination,
                                  @Query("mode") String mode,
                                  @Query("key") String key);


    @GET("json?")
    Call<ResponseBody> PlaceDetails(@Query("placeid") String placeid,
                                  @Query("key") String key);



}
