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

package client;

import static com.google.inject.Guice.createInjector;

import client.scenes.ActivityListCtrl;
import client.scenes.ConnectScreenCtrl;
import client.scenes.EditActivityCtrl;
import client.scenes.EndScreenCtrl;
import client.scenes.ExitOverlayCtrl;
import client.scenes.GuessCtrl;
import client.scenes.HelpOverlayCtrl;
import client.scenes.HowMuchCtrl;
import client.scenes.InsteadOfCtrl;
import client.scenes.IntermediateLeaderboardCtrl;
import client.scenes.MainCtrl;
import client.scenes.SpWaitingRoomCtrl;
import client.scenes.SplashCtrl;
import client.scenes.WaitingRoomCtrl;
import client.scenes.WhatRequiresMoreEnergyCtrl;
import com.google.inject.Injector;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {

  private static final Injector INJECTOR = createInjector(new MyModule());
  private static final MyFXML FXML = new MyFXML(INJECTOR);

  /**
   * Calls launch
   *
   * @param args provided
   * @throws URISyntaxException URISyntaxException
   * @throws IOException        IOException
   */
  public static void main(String[] args) throws URISyntaxException, IOException {
    launch();
  }

  /**
   * Initializes all the pairs, injects
   *
   * @param primaryStage we're using
   * @throws IOException IOException
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    var splash = FXML.load(
      SplashCtrl.class,
      "client/scenes/Splash.fxml",
      "client/css/Splash.css"
    );
    var connectScreen = FXML.load(
      ConnectScreenCtrl.class,
      "client/scenes/ConnectScreen.fxml",
      "client/css/ConnectScreen.css"
    );
    var waitingRoom = FXML.load(
      WaitingRoomCtrl.class,
      "client/scenes/WaitingRoom.fxml",
      "client/css/WaitingRoom.css"
    );

    var spWaitingRoom = FXML.load(
      SpWaitingRoomCtrl.class,
      "client/scenes/SpWaitingRoom.fxml",
      "client/css/SpWaitingRoom.css"
    );

    var howMuch = FXML.load(
      HowMuchCtrl.class,
      "client/scenes/HowMuch.fxml",
      "client/css/HowMuch.css"
    );

    var whatRequiresMoreEnergy = FXML.load(
      WhatRequiresMoreEnergyCtrl.class,
      "client/scenes/WhatRequiresMoreEnergyScreen.fxml",
      "client/css/WhatRequiresMoreEnergy.css"
    );

    var guess = FXML.load(
      GuessCtrl.class,
      "client/scenes/Guess.fxml",
      "client/css/Guess.css"
    );
    var instead = FXML.load(
      InsteadOfCtrl.class,
      "client/scenes/InsteadOfScreen.fxml",
      "client/css/HowMuch.css"
    );
    var intermediateLeaderboard = FXML.load(
      IntermediateLeaderboardCtrl.class,
      "client/scenes/IntermediateLeaderboard.fxml",
      "client/css/IntermediateLeaderboard.css"
    );

    var activityList = FXML.load(
      ActivityListCtrl.class,
      "client/scenes/ActivityList.fxml",
      "client/css/ActivityList.css"
    );
    var editActivity = FXML.load(
      EditActivityCtrl.class,
      "client/scenes/EditActivity.fxml",
      "client/css/EditActivity.css"
    );
    var helpOverlay = FXML.load(
      HelpOverlayCtrl.class,
      "client/scenes/HelpOverlay.fxml",
      "client/css/HelpOverlay.css"
    );
    var exitOverlay = FXML.load(
      ExitOverlayCtrl.class,
      "client/scenes/ExitOverlay.fxml",
      "client/css/ExitOverlay.css"
    );

    var endScreen = FXML.load(
      EndScreenCtrl.class,
      "client/scenes/EndScreen.fxml",
      "client/css/EndScreen.css"
    );

    var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
    mainCtrl.initialize(primaryStage, splash, connectScreen, waitingRoom, spWaitingRoom, howMuch, instead,
      whatRequiresMoreEnergy, guess, intermediateLeaderboard, activityList, editActivity, helpOverlay, exitOverlay,
      endScreen);

    //stops the thread when user closes the window
    primaryStage.setOnCloseRequest(e -> {
      Platform.exit();
      System.exit(0);
    });
  }
}