package oit.is.z3577.kaizi.janken.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import oit.is.z3577.kaizi.janken.model.Entry;

import oit.is.z3577.kaizi.janken.model.Janken;
import oit.is.z3577.kaizi.janken.model.User;
import oit.is.z3577.kaizi.janken.model.UserMapper;
import oit.is.z3577.kaizi.janken.model.Match;
import oit.is.z3577.kaizi.janken.model.MatchMapper;
import oit.is.z3577.kaizi.janken.model.MatchInfo;
import oit.is.z3577.kaizi.janken.model.MatchInfoMapper;

@Controller
public class JankenController {
  @Autowired
  private Entry entry;
  @Autowired
  UserMapper userMapper;
  @Autowired
  MatchMapper matchMapper;
  @Autowired
  MatchInfoMapper matchInfoMapper;

  /**
   * /janken に直接アクセスしたとき．ユーザ名は表示しない．
   */
  @GetMapping("/janken")
  public String janken(Principal prin, ModelMap model) {
    String loginUser = prin.getName(); // ログインしているユーザ名
    this.entry.addUser(loginUser);
    model.addAttribute("loginUser", loginUser);
    model.addAttribute("entry", this.entry);

    ArrayList<User> users = userMapper.selectAllUsers();
    model.addAttribute("users", users);
    ArrayList<Match> matches = matchMapper.selectAllMatches();
    model.addAttribute("matches", matches);

    ArrayList<MatchInfo> activeMatches = matchInfoMapper.selectActiveMatchInfo();
    model.addAttribute("activeMatches", activeMatches);

    return "janken.html";
  }

  @GetMapping("/jankengame")
  public String jankengame(@RequestParam String hand, ModelMap model) {
    Janken janken = new Janken(hand);
    model.addAttribute("janken", janken);
    return "janken.html";
  }

  @GetMapping("/match")
  public String match(@RequestParam Integer id, Principal prin, ModelMap model) {
    User opponent = userMapper.selectById(id); // クリックされた相手
    model.addAttribute("loginUser", prin.getName());
    model.addAttribute("opponent", opponent);
    return "match.html";
  }

  @GetMapping("/fight")
  public String fight(@RequestParam Integer id, @RequestParam String hand,
      Principal prin, ModelMap model) {
    User me = userMapper.selectByName(prin.getName()); // ログインユーザ（ほんだ）
    User opponent = userMapper.selectById(id); // 相手（CPU）
    String cpuHand = "Gu"; // CPUの手は固定でOK

    // DBに登録
    Match match = new Match();
    match.setUser1(me.getId());
    match.setUser2(opponent.getId());
    match.setUser1Hand(hand);
    match.setUser2Hand(cpuHand);
    matchMapper.insertMatch(match);

    // 画面表示用
    model.addAttribute("loginUser", prin.getName());
    model.addAttribute("opponent", opponent);
    model.addAttribute("myHand", hand);
    model.addAttribute("cpuHand", cpuHand);
    model.addAttribute("result", judge(hand, cpuHand));
    return "match.html";
  }

  @GetMapping("/wait")
  public String waitMatch(@RequestParam Integer id, @RequestParam String hand,
      Principal prin, ModelMap model) {
    User me = userMapper.selectByName(prin.getName()); // ログインユーザ（ほんだ）

    // matchinfo に試合中の情報を登録
    MatchInfo matchInfo = new MatchInfo();
    matchInfo.setUser1(me.getId()); // 自分のid
    matchInfo.setUser2(id); // 相手のid（リンクで送られてきたもの）
    matchInfo.setUser1Hand(hand); // 自分が選んだ手
    matchInfo.setIsActive(true); // 試合中
    matchInfoMapper.insertMatchInfo(matchInfo);

    model.addAttribute("loginUser", prin.getName());
    return "wait.html";
  }

  private String judge(String my, String cpu) {
    if (my.equals(cpu)) {
      return "Draw";
    }
    if ((my.equals("Gu") && cpu.equals("Choki"))
        || (my.equals("Choki") && cpu.equals("Pa"))
        || (my.equals("Pa") && cpu.equals("Gu"))) {
      return "You Win!";
    }
    return "You Lose";
  }
}
