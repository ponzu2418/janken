package oit.is.z3577.kaizi.janken.model;

public class Janken {
  private String playerHand;
  private String cpuHand;
  private String result;

  public Janken(String playerHand) {
    this.playerHand = playerHand;
    this.cpuHand = "グー"; // CPUは常にグーを出す
    this.result = judge();
  }

  private String judge() {
    if (playerHand.equals(cpuHand)) {
      return "あいこ";
    }
    if ((playerHand.equals("グー") && cpuHand.equals("チョキ"))
        || (playerHand.equals("チョキ") && cpuHand.equals("パー"))
        || (playerHand.equals("パー") && cpuHand.equals("グー"))) {
      return "勝ち";
    }
    return "負け";
  }

  public String getPlayerHand() {
    return playerHand;
  }

  public String getCpuHand() {
    return cpuHand;
  }

  public String getResult() {
    return result;
  }
}
