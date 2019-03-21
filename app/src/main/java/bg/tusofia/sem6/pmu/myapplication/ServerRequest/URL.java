package bg.tusofia.sem6.pmu.myapplication.ServerRequest;

public interface URL {
    String PROTOCOL = "https";
    String HOST = "snsdevelop.com";
    String PORT = "443";

    String GYM_LOGO_PREFIX = PROTOCOL + "://" + HOST + ":" + PORT + "/uploaded-content/clients";

    String PREFIX = PROTOCOL + "://" + HOST + ":" + PORT + "/time-travellers/api/v1/app";

    String OAUTH_LOGIN = PREFIX + "/login";
    String IF_SET_PASSWORD_LOGIN = PREFIX + "/login/check_password";
    String RESEND_EMAIL_WITH_PASSWORD_LOGIN = PREFIX + "/login/resend_email";
    String CAPTURE_QR_GENERATE = PREFIX + "/capture/qr/generate";
    String CAPTURE_GET_ALL_CONTENT = PREFIX + "/capture/all";
    String CAPTURE_UPLOAD_EDITED_PHOTO = PREFIX + "/save-image";
    String CAPTURE_UPLOAD_TIMELAPSE_CHOICE = PREFIX + "/save-timelapse";
    String CAPTURE_LOAD_CONTENT = PREFIX + "/get-content";
    String CAPTURE_LOAD_THUMB = PREFIX + "/get-content/thumb";
    String CAPTURE_DELETE_PHOTO = PREFIX + "/delete-image";
    String CAPTURE_DELETE_MULTIPLE_CONTENT = PREFIX + "/delete-multiple";
    String DATA_STREAM = PREFIX + "/data-stream";
    String GYMS_LIST_ALL = PREFIX + "/gyms";
}