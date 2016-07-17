package chat.rocket.app.db.collections;

import com.google.gson.JsonParser;

import chat.rocket.app.db.dao.CollectionDAO;

/**
 * Created by julio on 24/11/15.
 */
public class RCSettings {
    public static final String AccountsRegistrationRequired = "Accounts_RegistrationRequired";
    public static final String AccountsEmailVerification = "Accounts_EmailVerification";
    public static final String AccountsAllowedDomainsList = "Accounts_AllowedDomainsList";
    public static final String AccountsOAuthFacebook = "Accounts_OAuth_Facebook";
    public static final String AccountsOAuthGoogle = "Accounts_OAuth_Google";
    public static final String AccountsOAuthGithub = "Accounts_OAuth_Github";
    public static final String AccountsOAuthGitlab = "Accounts_OAuth_Gitlab";
    public static final String AccountsOAuthLinkedin = "Accounts_OAuth_Linkedin";
    public static final String AccountsOAuthMeteor = "Accounts_OAuth_Meteor";
    public static final String AccountsOAuthTwitter = "Accounts_OAuth_Twitter";
    public static final String AccountsAllowUserProfileChange = "Accounts_AllowUserProfileChange";
    public static final String AccountsAllowUserAvatarChange = "Accounts_AllowUserAvatarChange";
    public static final String AccountsAllowUsernameChange = "Accounts_AllowUsernameChange";
    public static final String AccountsAllowPasswordChange = "Accounts_AllowPasswordChange";
    public static final String AccountsRequireNameForSignUp = "Accounts_RequireNameForSignUp";
    public static final String FileUploadEnabled = "FileUpload_Enabled";
    public static final String FileUploadMaxFileSize = "FileUpload_MaxFileSize";
    public static final String FileUploadMediaTypeWhiteList = "FileUpload_MediaTypeWhiteList";
    public static final String SiteUrl = "Site_Url";
    public static final String SiteName = "Site_Name";
    public static final String APIAnalytics = "API_Analytics";
    public static final String APIEmbed = "API_Embed";
    public static final String APIEmbedDisabledFor = "API_EmbedDisabledFor";
    public static final String MessageAllowEditing = "Message_AllowEditing";
    public static final String MessageAllowEditingBlockEditInMinutes = "Message_AllowEditing_BlockEditInMinutes";
    public static final String MessageAllowDeleting = "Message_AllowDeleting";
    public static final String MessageAllowPinning = "Message_AllowPinning";
    public static final String MessageShowEditedStatus = "Message_ShowEditedStatus";
    public static final String MessageShowDeletedStatus = "Message_ShowDeletedStatus";
    public static final String MessageKeepHistory = "Message_KeepHistory";
    public static final String MessageMaxAllowedSize = "Message_MaxAllowedSize";
    public static final String MessageShowFormattingTips = "Message_ShowFormattingTips";
    public static final String MessageAudioRecorderEnabled = "Message_AudioRecorderEnabled";
    public static final String Pushdebug = "Push_debug";
    public static final String Pushenable = "Push_enable";
    public static final String Pushproduction = "Push_production";
    public static final String Pushgcmprojectnumber = "Push_gcm_project_number";
    public static final String LayoutHomeTitle = "Layout_Home_Title";
    public static final String LayoutHomeBody = "Layout_Home_Body";
    public static final String LayoutTermsofService = "Layout_Terms_of_Service";
    public static final String LayoutPrivacyPolicy = "Layout_Privacy_Policy";
    public static final String LayoutSidenavFooter = "Layout_Sidenav_Footer";
    public static final String LayoutLoginHeader = "Layout_Login_Header";
    public static final String LayoutLoginTerms = "Layout_Login_Terms";
    public static final String APIGitlabURL = "API_Gitlab_URL";
    public static final String WebRTCEnableChannel = "WebRTC_Enable_Channel";
    public static final String WebRTCEnablePrivate = "WebRTC_Enable_Private";
    public static final String WebRTCEnableDirect = "WebRTC_Enable_Direct";
    public static final String APIWordpressURL = "API_Wordpress_URL";
    public static final String APIGitHubEnterpriseURL = "API_GitHub_Enterprise_URL";
    public static final String MessageAllowPinningByAnyone = "Message_AllowPinningByAnyone";
    public static final String MessageAllowStarring = "Message_AllowStarring";
    public static final String LDAPEnable = "LDAP_Enable";
    public static final String LDAPDN = "LDAP_DN";

    public static final String COLLECTION_NAME = "rocketchat_settings";


    private static String getValue(CollectionDAO dao) {
        JsonParser parser = new JsonParser();
        return parser.parse(dao.getNewValuesJson()).getAsJsonObject().get("value").getAsString();
    }

    public static String getValueFor(String settings) {
        CollectionDAO dao = CollectionDAO.query(RCSettings.COLLECTION_NAME, settings);
        if (dao == null) {
            return null;
        }
        return getValue(dao);
    }
}
