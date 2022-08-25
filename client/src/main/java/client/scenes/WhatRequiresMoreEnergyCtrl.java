package client.scenes;

import client.scenes.helpers.QuestionCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import commons.MultipleChoiceQuestion;
import commons.Question;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class WhatRequiresMoreEnergyCtrl extends QuestionCtrl implements Initializable {

  @FXML
  private Button button0;
  @FXML
  private Button button1;
  @FXML
  private Button button2;

  private Button[] buttons;

  private Button clickedButton;
  private MultipleChoiceQuestion question;
  private boolean dbPoint;

  /**
   * Constructor for WhatRequiresMoreEnergyCtrl
   *
   * @param server   server we are on
   * @param mainCtrl controller for the game flow
   */
  @Inject
  WhatRequiresMoreEnergyCtrl(ServerUtils server, MainCtrl mainCtrl) {
    super(server, mainCtrl);
  }

  /**
   * Initializing the WhatRequiresMoreEnergyCtrl
   *
   * @param location  location
   * @param resources resources we're using
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    super.initialize(location, resources);
    buttons = new Button[] {button0, button1, button2};
  }

  /**
   * Displays the WhatRequiresMoreEnergy question screen
   *
   * @param question to show
   */

  @Override
  public void displayQuestion(Question question) {
    displayEmojis();
    this.question = (MultipleChoiceQuestion) question;
    this.clickedButton = null;
    this.dbPoint = false;

    // reset correct button colors
    for (Button button : buttons) {
      button.getStyleClass().removeAll(Collections.singleton("good"));
      button.getStyleClass().removeAll(Collections.singleton("bad"));
      button.setDisable(false);
    }

    Activity[] activities = {
      this.question.getActivity1(),
      this.question.getActivity2(),
      this.question.getActivity3()
    };

    boolean[] correctAnswers = this.question.getCorrect();
    for (int i = 0; i < buttons.length; i++) {
      Activity activity = activities[i];

      // get image
      StackPane imgContainer = new StackPane();
      imgContainer.getStyleClass().add("rounded");
      imgContainer.getStyleClass().add("img");

      ImageView imageView = new ImageView(new Image(
        new ByteArrayInputStream(server.getActivityImage(mainCtrl.serverIp, activity.id))
      ));

      // resize image
      imageView.setFitWidth(1140 / 3.0);
      imageView.setFitHeight(1140 / 3.0);

      imgContainer.getChildren().add(imageView);

      //set image
      buttons[i].setGraphic(imgContainer);

      // image is displayed on top of text
      buttons[i].setContentDisplay(ContentDisplay.TOP);
      buttons[i].setText(activity.getTitle());
      buttons[i].setUserData(correctAnswers[i]);
    }
    displayJokers();
    showPoints();
  }

  /**
   * Shows the correct answer
   */
  @Override
  public void showCorrect() {
    for (Button button : buttons) {
      button.getStyleClass().add((boolean) button.getUserData() ? "good" : "bad");
    }
    if (clickedButton != null && (boolean) clickedButton.getUserData()) {
      showUserCorrect();
    }
  }

  /**
   * Disables the buttons
   */
  @Override
  public void disableButtons() {
    super.disableButtons();
    for (Button button : buttons) {
      button.setDisable(true);
    }
  }

  /**
   * Check the correct answer
   *
   * @param event on MouseClick
   */
  @FXML
  public void checkCorrectAnswer(MouseEvent event) {
    mainCtrl.stopPointsTimer();
    this.clickedButton = (Button) event.getSource();
    for (Button button : buttons) {
      button.setDisable(true);
    }
  }

  /**
   * Shows the correct answer
   */
  public void showUserCorrect() {
    int toAdd = mainCtrl.getPointsOffset();
    if (dbPoint) {
      toAdd *= 2;
    }
    mainCtrl.addPoints(toAdd);
    showPoints();
  }

  /**
   * Activates Hint Joker
   */
  public void hint() {
    hintQ(question.getCorrect(), buttons);
  }

  /**
   * Activates Double Points Joker
   */
  public void doublePoints() {
    dbPoint = doublePointsQ();
  }
}
