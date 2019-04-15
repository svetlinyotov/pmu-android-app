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
    String START_TEAM_PLAYER_GAME_CREATE_TEAM = PREFIX + "/game/start/team/create";
    String START_TEAM_PLAYER_GAME_JOIN_TEAM = PREFIX + "/game/start/team/join";
    String START_TEAM_PLAYER_GAME_UN_JOIN_TEAM = PREFIX + "/game/start/team/unJoin";
    String START_TEAM_PLAYER_GAME_LIST_TEAMS = PREFIX + "/game/start/team/list";
    String GAME_LOCATION = PREFIX + "/game/location";
    String GAME_START_TEAM_PLAY = PREFIX + "/game/start/team/start";
    String GAME_START_TEAM_LIST_PLAYERS = PREFIX + "/game/start/team/list/players";
    String GAME_QR = PREFIX + "/game/qr";
}