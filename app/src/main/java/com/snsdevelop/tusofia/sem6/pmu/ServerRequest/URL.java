package com.snsdevelop.tusofia.sem6.pmu.ServerRequest;

public interface URL {
    String PROTOCOL = "https";
    String HOST = "snsdevelop.com";
    String PORT = "443";

    String PREFIX = PROTOCOL + "://" + HOST + ":" + PORT + "/time-travellers/api/v1/app";

    String OAUTH_LOGIN = PREFIX + "/login";
    String GET_ALL_LOCATIONS = PREFIX + "/locations";
    String GET_GLOBAL_RANKING = PREFIX + "/ranking";
    String GET_ALL_GAMES = PREFIX + "/ranking/personal";
    String START_SINGLE_PLAYER_GAME = PREFIX + "/game/start/single";
}