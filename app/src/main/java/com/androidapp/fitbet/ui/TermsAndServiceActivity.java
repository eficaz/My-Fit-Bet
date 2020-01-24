package com.androidapp.fitbet.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.utils.SLApplication;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TermsAndServiceActivity extends BaseActivity {

    @Bind(R.id.btn_back)
    LinearLayout btn_back;

    @Bind(R.id.privacy_and_policy)
    AutoCompleteTextView privacy_and_policy;


    String content="<p align=\"center\"><strong>TERMS OF SERVICE</strong><a name=\"_GoBack\"></a><strong> </strong></p>\n" +
            "<p><strong><u>Introduction</u></strong><br>\n" +
            "  The FitBet mobile application (hereafter, the &quot;Services&quot;) are provided by FitBet, Inc. (hereafter, &quot;us&quot; or &quot;we&quot; or &quot;our&quot;) in connection with our partners, service providers, sponsors, or other affiliates. So that we may safely and responsibly provide our services for all of our users, your use of our services is subject to the terms and conditions set forth below, as well as the privacy policy set forth.</p>\n" +
            "<p>By accessing and using our services, you accept and agree to be bound by the terms and provision of this agreement. In addition, when using our services, you shall be subject to any posted guidelines or rules applicable to such services, which may be posted and modified from time to time. All such guidelines or rules are hereby incorporated by reference into the Terms of Service (TOS).</p>\n" +
            "<p>ANY USE OF OUR SERVICES WILL CONSTITUTE ACCEPTANCE OF THIS AGREEMENT. IF YOU DO NOT AGREE TO ABIDE BY THIS AGREEMENT, PLEASE DO NOT USE OUR SERVICES.</p>\n" +
            "<p><strong><u>Use of the Services</u></strong><br>\n" +
            "  The following activities are expressly prohibited:<br>\n" +
            "  (i) collecting contact information of other users by electronic or other means for the purpose of sending unsolicited communications<br>\n" +
            "  (ii) any use of the Services, which in our sole judgment, degrades the reliability, speed, or operation of the Services<br>\n" +
            "  (iii) use of data scraping methods to collect data about other users from FitBet services;<br>\n" +
            "  (iv) any use of the Services which is unlawful or in violation of these Terms of Use; and<br>\n" +
            "  (v) attempting to decompile or reverse engineer any software contained in the Services</p>\n" +
            "<p>Your use of the Services is subject, in our sole discretion, to termination at any time.</p>\n" +
            "<p><strong><u>Children under the Age of 18</u></strong><br>\n" +
            "  By using the Services, you warrant that you are at least 18 years of age. We may terminate your account and access to our services if we believe you are under the age of 18.</p>\n" +
            "<p><strong><u>User Content</u></strong></p>\n" +
            "<ul>\n" +
            "  <li>The Services include functionality to submit, share and publish content from the user (&quot;User Content&quot;).</li>\n" +
            "  <li>You shall be solely responsible for User Content you submit and the consequences of our posting or publishing such User Content. You also affirm that you have the rights, permissions, licenses and permissions to any User Content you publish.</li>\n" +
            "  <li>We do not guarantee confidentiality of user content.</li>\n" +
            "  <li>By submitting the User Content to us, you grant us a perpetual, worldwide, non-exclusive, royalty-free, sublicensable and transferable license to use, reproduce, distribute, prepare derivative works of, modify, display, and perform all or any portion of the User Content in connection with our provision of the Services.</li>\n" +
            "  <li>We may maintain copies of any User Content for purposes of backup, security, or maintenance, or as required by law.</li>\n" +
            "  <li>You agree that you will not:</li>\n" +
            "</ul>\n" +
            "<p>(i) submit material that is copyrighted, protected by trade secret or otherwise subject to third party proprietary rights unless you are the owner of such rights or have permission from their rightful owner;<br>\n" +
            "  (ii) publish falsehoods or misrepresentations that could damage us or any third party;<br>\n" +
            "  (iii) submit material that is unlawful, obscene, defamatory, libelous, threatening, pornographic, harassing, hateful, racially or ethnically offensive, or encourages conduct that would be considered a criminal offense, give rise to civil liability, violate any law, or is otherwise inappropriate;<br>\n" +
            "  (iv) post advertisements or solicitations of business; or<br>\n" +
            "  (v) impersonate another person.</p>\n" +
            "<ul>\n" +
            "  <li>We do not endorse any User Content or any opinion, recommendation, or advice expressed therein, and we expressly disclaim any and all liability in connection with any User Content. We may remove any Content and User Content without prior notice. We may also terminate your access to the Services, if you are determined to be a repeat infringer.</li>\n" +
            "  <li>We reserve the right to decide whether Content or User Content is appropriate and complies with these Terms of Use for violations other than copyright infringement and violations of intellectual property law. We reserve the right to remove any content without notice and to terminate offending accounts at our sole discretion.</li>\n" +
            "  <li>If you are a copyright owner or an agent thereof and believe that any User Content or other Content infringes upon your copyright, please contact us at info@fitbet.com.au</li>\n" +
            "</ul>\n" +
            "<p><strong><u>Indemnity</u></strong><br>\n" +
            "  You agree to defend, indemnify, and hold us harmless from and against any claims, actions or demands, including, without limitation, reasonable legal and accounting fees, arising or resulting from your breach of these Terms of Use or your uploading of, access to, or use or misuse of the Content or the Services. We shall provide notice to you of any such claim, suit, or proceeding and shall assist you, at your expense, in defending any such claim, suit or proceeding. We reserve the right to assume the exclusive defense and control of any matter which is subject to indemnification under this section. In such case, you agree to cooperate with any reasonable requests assisting our defense of such matter.</p>\n" +
            "<p>&nbsp;</p>\n" +
            "<p><strong><u>Disclaimer of Warranty and Limitation of Liability</u></strong><br>\n" +
            "  WE, OUR AFFILIATES, OUR PARTNERS, AND OUR AND THEIR RESPECTIVE OFFICERS, DIRECTORS, EMPLOYEES, AGENTS, SUPPLIERS, OR LICENSORS, MAKE NO WARRANTIES OR REPRESENTATIONS ABOUT THE CONTENT (INCLUDING THE USER CONTENT), INCLUDING BUT NOT LIMITED TO ITS ACCURACY, RELIABILITY, COMPLETENESS, TIMELINESS, OR RELIABILITY.</p>\n" +
            "<p>THE SERVICES AND CONTENT ARE PROVIDED ON AN &quot;AS IS&quot; AND &quot;AS AVAILABLE&quot; BASIS WITHOUT ANY WARRANTIES OF ANY KIND. WE HEREBY DISCLAIM ALL WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE WARRANTY OF TITLE, MERCHANTABILITY, NON INFRINGEMENT OF THIRD PARTIES' RIGHTS, AND FITNESS FOR PARTICULAR PURPOSE.</p>\n" +
            "<p>NEITHER WE NOR OUR AFFILIATES OR PARTNERS SHALL BE SUBJECT TO LIABILITY FOR TRUTH, ACCURACY, OR COMPLETENESS OF ANY INFORMATION CONVEYED TO USERS OF THE SERVICES OR FOR ERRORS, MISTAKES OR OMISSIONS THEREIN OR FOR ANY DELAYS OR INTERRUPTIONS OF THE DATA OR INFORMATION STREAM FROM WHATEVER CAUSE. YOU AGREE THAT YOU USE THE SERVICES AND THE CONTENT AT YOUR OWN RISK.</p>\n" +
            "<p>WE MAKE NO WARRANTY THAT THE SERVICES WILL BE AVAILABLE ERROR FREE OR THAT THE SERVICES OR THE CONTENT ARE FREE OF COMPUTER VIRUSES OR SIMILAR CONTAMINATION OR DESTRUCTIVE FEATURES. IF YOUR USE OF THE SERVICES OR THE CONTENT RESULTS IN THE NEED FOR SERVICING OR REPLACING EQUIPMENT OR DATA, WE SHALL NOT BE RESPONSIBLE FOR THOSE COSTS.</p>\n" +
            "<p>IN NO EVENT SHALL WE BE LIABLE FOR ANY DAMAGES (INCLUDING, WITHOUT LIMITATION, INCIDENTAL AND CONSEQUENTIAL DAMAGES, LOST PROFITS, OR DAMAGES RESULTING FROM LOST DATA OR BUSINESS INTERRUPTION) RESULTING FROM THE USE OR INABILITY TO USE THE SERVICES AND THE CONTENT, WHETHER BASED ON WARRANTY, CONTRACT, TORT (INCLUDING NEGLIGENCE), OR ANY OTHER LEGAL THEORY, IN EXCESS OF ONE HUNDRED DOLLARS, EVEN IF A WE HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.</p>\n" +
            "<p><strong><u>No Medical Advice</u></strong><br>\n" +
            "  THE SERVICES DO NOT CONTAIN OR CONSTITUTE, AND SHOULD NOT BE INTERPRETED AS, MEDICAL ADVICE OR OPINION. We are not licensed medical professionals, and we are not in the business of providing medical advice. You should always consult a qualified and licensed medical professional prior to beginning or modifying any diet or exercise program. YOUR USE OF THE WEBSITE OR THE MOBILE APPLICATION DOES NOT CREATE A DOCTOR-PATIENT RELATIONSHIP BETWEEN YOU AND US.</p>\n" +
            "<p><strong><u>CREDIT PURCHASE</u></strong><br>\n" +
            "  For being part of the FitBet betting challenge, every user needs to have betting credits. Betting credits can be purchased as a pack of 10 or 20 credits. A dollar is charged for each credit.</p>\n" +
            "<p><strong><u>CREDIT ENCASHING</u></strong><br>\n" +
            "  You have the option to encash the betting credits they possess. This is done via Stripe payment gateway. While encashing, the Payment Gateway charges and Administrative charges would be deducted.</p>\n" +
            "<p><strong><u>PAYMENT</u></strong><br>\n" +
            "  Payment for the FitBet must be done via Apple and Google InApp purchases. The payment amount would include InApp charges. Amounts paid for buying betting credits are not refundable.</p>";


    @Override
    protected void onMessageReceived(String message) {
        super.onMessageReceived(message);
        SLApplication.isCountDownRunning=true;
        startActivity(new Intent(this,DashBoardActivity.class));
        finish();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_service);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            privacy_and_policy.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        } else
            privacy_and_policy.setText(Html.fromHtml(content));
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
