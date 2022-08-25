/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import commons.Activity;
import commons.Client;
import commons.Question;
import commons.Score;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import org.glassfish.jersey.client.ClientConfig;

public class ServerUtils {
  /**
   * Initiate connection to the server.
   *
   * @param ip       the ip of the server
   * @param username the chosen username
   * @return the UUID of the client
   */
  public String connect(String ip, String username) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/player/connect").queryParam("username", username)
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .post(null, String.class);
  }

  /**
   * Keep the connection to the server alive.
   *
   * @param ip             the ip of the server
   * @param id             the client's UUID
   * @param waitingForGame whether the client is in the lobby
   * @return String  the client's UUID
   */
  public Client keepAlive(String ip, String id, boolean waitingForGame) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/player/keepAlive").queryParam("id", id)
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .put(Entity.json(waitingForGame), Client.class);
  }

  private static ScheduledExecutorService EXECUpdate;

  /**
   * Player Updates
   *
   * @param ip       ip
   * @param consumer consumer
   */
  public void registerForPlayerUpdates(String ip, Consumer<Boolean> consumer) {

    EXECUpdate = Executors.newSingleThreadScheduledExecutor();
    try {
      EXECUpdate.submit(() -> {
        while (!EXECUpdate.isShutdown()) {
          var res = ClientBuilder.newClient(new ClientConfig())
            .target(ip).path("api/player/updates")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get(Response.class);
          if (res.getStatus() == 204) {
            continue;
          }
          consumer.accept(res.readEntity(Boolean.class));
        }
        consumer.accept(true);
      });
    } catch (Exception e) {
      if (!EXECUpdate.isShutdown()) {
        registerForPlayerUpdates(ip, consumer);
      }
    }
  }

  /**
   * Stops updates
   */
  public void stopUpdates() {
    EXECUpdate.shutdownNow();
  }

  /**
   * Request to take all players from the waiting room and assign them to a game
   *
   * @return unique generated game id
   */
  public String startGame(String ip, Boolean multiplayer) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip)
      .path("api/game/play").queryParam("m", multiplayer)
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .put(Entity.json("START GAME"), String.class);
  }

  /**
   * Starts a SinglePLayer game
   *
   * @param ip  server ip
   * @param uid userid
   * @return String
   */
  public String startSingleGame(String ip, String uid) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip)
      .path("api/game/playSingle")
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .post(Entity.json(uid), String.class);
  }

  private static ScheduledExecutorService EXECNextQuestion;

  /**
   * Request to get a new question from the server
   * <p>
   * Accept the Question in the consumer, so it propagates to MainCtrl
   */
  public void nextQuestion(String ip, String gameId, Consumer<Question> consumer) {

    EXECNextQuestion = Executors.newSingleThreadScheduledExecutor();
    try {
      EXECNextQuestion.submit(() -> {
        while (!EXECNextQuestion.isShutdown()) {
          var res = ClientBuilder.newClient(new ClientConfig())
            .target(ip)
            .path("/api/game/next/" + gameId)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get(Response.class);
          if (res.getStatus() == 204) {
            continue;
          }
          if (EXECNextQuestion.isShutdown()) {
            break;
          }
          consumer.accept(res.readEntity(Question.class));
        }
      });
    } catch (Exception e) {
      if (!EXECNextQuestion.isShutdown()) {
        nextQuestion(ip, gameId, consumer);
      }
    }

  }

  /**
   * Stops QuestionThread
   */
  public void stopQuestionThread() {
    EXECNextQuestion.shutdownNow();
  }

  /**
   * Get all activities from the server
   *
   * @param ip the server's IP address
   * @return list of all activities
   */
  public List<Activity> getActivities(String ip) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/activity/list")
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .get(new GenericType<>() {
      });
  }

  /**
   * Get the activity's image
   *
   * @param ip the server's IP address
   * @param id the ID of the activity
   * @return image of the activity
   */
  public byte[] getActivityImage(String ip, String id) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/activity/image/" + id)
      .request("image/jpeg")
      .get(byte[].class);
  }

  /**
   * Changes the image associated with the activity
   *
   * @param ip the server's IP address
   * @param id the ID of the activity
   */
  public void changeActivityImage(String ip, String id, byte[] image) {
    ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/activity/image/" + id)
      .request("image/jpeg")
      .put(Entity.entity(image, MediaType.APPLICATION_OCTET_STREAM));
  }

  /**
   * Updates the activity
   *
   * @param ip       server ip
   * @param activity we're updating
   * @return Activity
   */
  public Activity updateActivity(String ip, Activity activity) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/activity/update")
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .put(Entity.json(activity), Activity.class);
  }

  /**
   * Returns the list of Clients
   *
   * @param ip server ip
   * @return List of Clients
   */
  public List<Client> getPlayers(String ip) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/player/list")
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .get(new GenericType<>() {
      });
  }

  /**
   * Gets a client by id
   *
   * @param ip server id
   * @param id client id
   * @return Client
   */
  public Client getClient(String ip, String id) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/player/" + id)
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .get(Client.class);
  }

  /**
   * Adds a score to the database
   *
   * @param ip    server ip
   * @param score we're adding
   * @return Score
   */
  public Score addScore(String ip, Score score) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/score/add")
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .post(Entity.json(score), Score.class);
  }

  /**
   * Returns a list of Scores for the SinglePlayerLeaderboard from the database
   *
   * @param ip server ip
   * @return List of Scores
   */
  public Iterable<Score> getSingleLeaderboard(String ip) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/score/leaderboard")
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .get(new GenericType<>() {
      });
  }

  /**
   * Gets the multiplayer leaderboard at the time for a given game
   *
   * @param ip     of the server
   * @param gameId of the game the client is in
   * @return the set of leaderboard scores
   */
  public Iterable<Score> getMultiLeaderboard(String ip, String gameId) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/game/multiLeaderboard/" + gameId)
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .get(new GenericType<>() {
      });
  }

  /**
   * Returns a score of a player as an int
   *
   * @param id id of a player
   * @return a score of the player specified by the passed parameter
   */
  public int playerScore(String ip, String id) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/game/score/" + id)
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .get(Integer.class);
  }

  /**
   * Updates the score of the player specified by id in the game controller
   *
   * @param id id of a player
   * @return true if the score was updated, false otherwise
   */

  public Score updateScore(String ip, String id, int score) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/game/score/update/" + id).queryParam("score", score)
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .get(Score.class);
  }

  /**
   * Sends the score to the server
   *
   * @param ip    of the server
   * @param score to be sent
   * @return Score score
   */
  public Score sendScore(String ip, Score score, String gameId) {
    return ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/game/score/send/" + gameId)
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .post(Entity.json(score), Score.class);
  }

  public void removePlayer(String ip, String gameId, String clientId) {
    ClientBuilder.newClient(new ClientConfig())
      .target(ip).path("api/game/removePlayer/" + gameId)
      .request(APPLICATION_JSON)
      .accept(APPLICATION_JSON)
      .put(Entity.json(clientId), String.class);
  }
}
