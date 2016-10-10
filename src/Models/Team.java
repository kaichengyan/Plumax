package Models;

public enum Team {

    TEAM_RED("Team Red"),
    TEAM_GREEN("Team Green"),
    TEAM_BLUE("Team Blue");

    private final String teamDescription;

    private Team(String value) {
        teamDescription = value;
    }

    public String getTeamDescription() {
        return teamDescription;
    }

}
