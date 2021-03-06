package io.underflowers.underification.api.spec.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.underflowers.underification.ApiException;
import io.underflowers.underification.api.DefaultApi;
import io.underflowers.underification.api.dto.Badge;
import io.underflowers.underification.api.dto.Event;
import io.underflowers.underification.api.dto.Rule;
import io.underflowers.underification.api.dto.UserScore;
import io.underflowers.underification.api.dto.LeaderBoardEntry;
import io.underflowers.underification.api.spec.helpers.Environment;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class EventSteps {

    private Environment environment;
    private BasicSteps basicSteps;
    private ApplicationSteps applicationSteps;
    private RuleSteps ruleSteps;
    private BadgeSteps badgeSteps;
    private DefaultApi api;

    private Event eventPayload;

    public EventSteps(Environment environment,
                      BasicSteps basicSteps,
                      ApplicationSteps applicationSteps,
                      RuleSteps ruleSteps,
                      BadgeSteps badgeSteps) {
        this.environment = environment;
        this.basicSteps = basicSteps;
        this.applicationSteps = applicationSteps;
        this.ruleSteps = ruleSteps;
        this.badgeSteps = badgeSteps;

        this.api = environment.getApi();
    }

    @Given("I have an event payload of user {string}")
    public void i_have_a_event_payload(String userId) throws Throwable {
        this.eventPayload = new Event()
                .appUserId(userId)
                .eventType(this.ruleSteps.getLastReceivedRule().getEventType());
    }

    @When("^I POST the event payload$")
    public void i_post_the_event_payload() throws Throwable {
        this.api.triggerEvent(this.eventPayload);
    }

    @When("I send a GET to the user badges endpoint of user {string}")
    public void iSendAGETToTheUsersBadgesEndpoint(String userId) {
        try {
            basicSteps.processApiResponse(api.getUserBadgesWithHttpInfo(userId));
        } catch (ApiException e) {
            basicSteps.processApiException(e);
        }
    }

    @And("^I see I have a badge$")
    public void i_earned_a_badge() throws Throwable {
        ArrayList<Badge> scores = (ArrayList<Badge>)this.basicSteps.getLastApiResponse().getData();
        assertEquals(badgeSteps.getLastReceivedBadge().getName(), scores.get(0).getName());
    }

    @When("I send a GET to the users scores endpoint of user {string}")
    public void iSendAGETToTheUsersScoresEndpoint(String userId) {
        try {
            basicSteps.processApiResponse(api.getUserScoresWithHttpInfo(userId));
        } catch (ApiException e) {
            basicSteps.processApiException(e);
        }
    }

    @And("^I see I have a point$")
    public void i_earned_a_point() throws Throwable {
        ArrayList<UserScore> scores = (ArrayList<UserScore>) this.basicSteps.getLastApiResponse().getData();
        assertEquals((Integer)1, scores.get(0).getScore());
    }

    @Then("User {string} is first in {string} leaderboard")
    public void user_is_first_in_leaderboard(String userId, String pointScale) {
        try {
            basicSteps.processApiResponse(api.getLeaderBoardWithHttpInfo(pointScale, 2));
            ArrayList<LeaderBoardEntry> entries = (ArrayList<LeaderBoardEntry>)this.basicSteps.getLastApiResponse().getData();
            assertEquals(entries.get(0).getAppUserId(), userId);
        } catch (ApiException e) {
            basicSteps.processApiException(e);
        }
    }

}
