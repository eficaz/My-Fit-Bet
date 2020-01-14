package com.androidapp.fitbet.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidapp.fitbet.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PrivacyPolicyActivity extends BaseActivity {

    @Bind(R.id.btn_back)
    LinearLayout btn_back;

    @Bind(R.id.privacy_and_policy)
    AutoCompleteTextView privacy_and_policy;
    @Bind(R.id.txt_page_heading)
    TextView txtPageHeading;
    @Bind(R.id.txt_rules_heading)
            TextView txtRulesHeading;
    @Bind(R.id.txt_rules)
            TextView txtRules;


    String content="<p align=\"center\"><strong>PRIVACY POLICY</strong></p>\n" +
            "<p>FitBet's goal is to help users&rsquo; lives become more active and healthier using advanced mobile internet technology. To attain that; with the help of a betting medium; we collect, store and analyze variety of data from you and your devices.</p>\n" +
            "<p>This statement is intended to clearly explain in human readable language what data we collect and how it is used. If after reading this statement you have further questions, please contact us at the email address listed at the end of this document.</p>\n" +
            "<p><strong><u>WHAT INFORMATION WE COLLECT</u></strong><br>\n" +
            "  Our apps and services may collect the following types of information in order to provide services:</p>\n" +
            "<ul>\n" +
            "  <li>Your email address or your social login information as required to safeguard your data and verify your identity;</li>\n" +
            "  <li>Biographical information, e.g. photograph, biography, gender, age, geographic location;</li>\n" +
            "  <li>Physical activity related data, e.g., exercise logs (including location coordinates for GPS-enabled sessions) and passively tracked activity data such as steps taken, active time and distance travelled.</li>\n" +
            "  <li>Data entered via chats and free text fields, such as comments and annotations</li>\n" +
            "</ul>\n" +
            "<p>We may also collect certain information from your mobile device, including but not limited to the following:</p>\n" +
            "<ul>\n" +
            "  <li>Location Information that allows us to periodically determine your location, including your location relative to and within third party merchant locations.</li>\n" +
            "  <li>Device and Usage Information that may include (i) information specific to your mobile device (e.g., make, model, operating system, advertising identifier and similar information); (ii) information about your use of features, functions, or notifications on the device; and (iii) signal strength relating to WiFi or Bluetooth functionality, temperature, battery level, and similar technical data.</li>\n" +
            "</ul>\n" +
            "<p>We may collect this information even if the app is not running in the foreground.<br>\n" +
            "  [<strong>For iOS</strong>: You should be able to adjust the settings on your iOS mobile device to prevent our collection of Location Information by disabling the location services feature on your device.]<br>\n" +
            "  [<strong>For Android</strong>: You should be able to adjust your settings on your Android mobile device to prevent our collection of Location Information be disabling Bluetooth and Location.]</p>\n" +
            "<p><strong>App-Usage Data:</strong></p>\n" +
            "<ul>\n" +
            "  <li>We may collect data related to the usage of our services for the purposes of improving our services, e.g., sessions and button clicks</li>\n" +
            "</ul>\n" +
            "<p><strong>Data collected from other sources:</strong></p>\n" +
            "<ul>\n" +
            "  <li>If you choose to connect our services with 3rd party services such as social networks, we may collect information from these services in order to provide the expected functionality to you. Any information we collect will always be used in compliance with the policies of the relevant 3rd party. This data may include but is not limited to health data collected via basic social network profile information.</li>\n" +
            "</ul>\n" +
            "<p><strong><u>APPLE HEALTHKIT DATA</u></strong><br>\n" +
            "  User data collected from Apple Health will never be used for marketing or advertising purposes.</p>\n" +
            "<p><strong><u>POLICY FOR CHILDREN</u></strong><br>\n" +
            "  FitBet services are not directed at persons under the age of 18. We do not knowingly collect any personal data from children under the age of 18. If you are under the age of 18, we ask that you do not submit any personal data through our services. If you are aware of a person under the age of 18 who has submitted personal information to our services, please report it to info@fitbet.com.au and we will promptly delete such information from our databases.</p>\n" +
            "<p><strong><u>USE OF YOUR DATA</u></strong><br>\n" +
            "  We use the information we collect to:</p>\n" +
            "<ul>\n" +
            "  <li>Operate and improve our services;</li>\n" +
            "  <li>Provide user and other support;</li>\n" +
            "  <li>Provide you with information when you enter participating local venues;</li>\n" +
            "  <li>Carry out other purposes that are disclosed to you and to which you consent.</li>\n" +
            "</ul>\n" +
            "<p>We do not disclose your health-related data, physical activity data or health related goal data to any 3rd parties without your express permission.</p>\n" +
            "<p><strong><u>THIRD PARTY AND DISCLOSURE</u></strong><br>\n" +
            "  There may be circumstances in which your data may be shared with certain third parties. These are outlined below:</p>\n" +
            "<ul>\n" +
            "  <li><strong>Business transfer</strong>: in the event of a sale, merger, dissolution or similar event of FitBet, data held by us may be part of the transferred business assets.</li>\n" +
            "  <li><strong>Service providers</strong>: we may hire third parties to help us perform business functions in support of our services. In this event, any third parties will only be provided with the data necessary to perform their specific function, and the use of this data will only be to perform the specific requested function.</li>\n" +
            "  <li>If required to do so by law.</li>\n" +
            "</ul>\n" +
            "<p><strong><u>COMMUNICATION</u></strong><br>\n" +
            "  If you choose to share contact information with us (such as an email address), we may use this information to send you updates about our services from time to time. <br>\n" +
            "  <strong><u>SOCIAL SHARING</u></strong><br>\n" +
            "  If you choose to use the social features of FitBet, your daily activity data, goal check-in activity, and some profile information such as your display image, display name, profile bio text, and general location (city, state, country) may be visible to other users. By using social features of our services, you consent to sharing this information. You can choose to stop sharing data with other users in your groups by leaving the group(s) to which you no longer wish to share data. <br>\n" +
            "  <strong><u>DELETING YOUR DATA</u></strong><br>\n" +
            "  If you would like your account and related information deleted, please contact us at the e-mail address listed at the end of this document. Please note that requests may take several business days to complete, and that after completion you will no longer be able to use the services unless you create a new account.<br>\n" +
            "  <strong><u>CHANGES TO THIS POLICY</u></strong><br>\n" +
            "  We reserve the right to change our Privacy Policy in the future without prior notice. The services we offer are likely to change, and in the future, it is therefore likely that our Privacy Policy will also need to be revised accordingly. Our most up-to-date privacy policy will be posted here.<br>\n" +
            "  <strong><u>MORE INFORMATION</u></strong><br>\n" +
            "  Contact us at info@fitbet.com.au if you have further questions or concerns about your data. Last updated: 09/10/2019</p>";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        ButterKnife.bind(this);

        String name=getIntent().getStringExtra("name");


if(name.equals("privacy")) {
    privacy_and_policy.setVisibility(View.VISIBLE);
    txtPageHeading.setText(getString(R.string.privacy_and_policy));
    txtRules.setVisibility(View.GONE);
    txtRulesHeading.setVisibility(View.GONE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        privacy_and_policy.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
    } else
        privacy_and_policy.setText(Html.fromHtml(content));
}else{
    privacy_and_policy.setVisibility(View.GONE);
    txtPageHeading.setText(getString(R.string.rules_regulations));
    txtRules.setVisibility(View.VISIBLE);
    txtRulesHeading.setVisibility(View.VISIBLE);
}





        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
