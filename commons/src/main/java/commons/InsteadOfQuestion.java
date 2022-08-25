package commons;

import com.fasterxml.jackson.annotation.JsonTypeName;

@SuppressWarnings("unused")
@JsonTypeName("INSTEAD")
public class InsteadOfQuestion extends Question {

  private String title1;
  private String title2;
  private Activity activity1; //X
  private Activity activity2; //Y
  private long factor; //correct answer

  /**
   * Empty Constructor
   */
  public InsteadOfQuestion() {
    super(Type.INSTEAD);
  }

  /**
   * Constructor
   *
   * @param activity1 X
   * @param activity2 Y
   */
  public InsteadOfQuestion(Activity activity1, Activity activity2) {
    super(Type.INSTEAD);
    if (activity1.getConsumption_in_wh() <= activity2.getConsumption_in_wh()) { // factor is never smaller than 1
      Activity holder = activity1;
      activity1 = activity2;
      activity2 = holder;
    }
    factor = Math.round(activity1.getConsumption_in_wh() * 1.0 / activity2.getConsumption_in_wh());
    String titleMock = activity1.getTitle().replace(".", "");
    titleMock = Character.toLowerCase(titleMock.charAt(0)) + titleMock.substring(1);
    this.title1 = "Instead of " + titleMock + ", ";
    titleMock = activity2.getTitle().replace(".", "");
    titleMock = Character.toLowerCase(titleMock.charAt(0)) + titleMock.substring(1);
    this.title2 = titleMock + " ?";
    this.activity1 = activity1;
    this.activity2 = activity2;
  }

  /**
   * Returns how close answer is to correct value
   *
   * @param guessedValue value guessed by player
   * @return int points to be awarded for the response
   * 0 for more than percentage% away from the correct answer
   * value between 100 and 75 otherwise
   * linearly between them for answers within the percentage boundary
   */
  public int calculateHowClose(long guessedValue) {
    return calculateHowClose(guessedValue, factor);
  }


  /**
   * getter for the title 1
   *
   * @return String - Instead of X
   */
  public String getTitle1() {
    return title1;
  }

  /**
   * getter for the title2
   *
   * @return String - how many times could you be doing Y
   */
  public String getTitle2() {
    return title2;
  }

  /**
   * getter for activity 1
   *
   * @return Activity - the 1st activity
   */
  public Activity getActivity1() {
    return activity1;
  }

  /**
   * getter for activity 32
   *
   * @return Activity - the 2nd activity
   */
  public Activity getActivity2() {
    return activity2;
  }

  /**
   * getter for the factor
   *
   * @return factor correct answer
   */
  public long getFactor() {
    return factor;
  }

}
