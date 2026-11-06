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
    User me = userMapper.selectByName(prin.getName()); // ログインユーザ（自分）

    // 【仕様A】相手(id)が自分(me)を待っている試合中のレコードがあるか調べる
    MatchInfo waiting = matchInfoMapper.selectActiveByUsers(id, me.getId());

    if (waiting == null) {
      // 【仕様B】無い → 自分が先手。task2 と同じく matchinfo に登録して待つ
      MatchInfo matchInfo = new MatchInfo();
      matchInfo.setUser1(me.getId()); // 自分のid
      matchInfo.setUser2(id); // 相手のid
      matchInfo.setUser1Hand(hand); // 自分が選んだ手
      matchInfo.setIsActive(true); // 試合中
      matchInfoMapper.insertMatchInfo(matchInfo);
    } else {
      // 【仕様C】ある → 試合成立。matches に結果を登録する
      Match match = new Match();
      match.setUser1(waiting.getUser1()); // プレイヤーA（先手）のID
      match.setUser2(me.getId()); // プレイヤーB（自分）のID
      match.setUser1Hand(waiting.getUser1Hand()); // プレイヤーAの手（matchinfoに保存されていた）
      match.setUser2Hand(hand); // プレイヤーBの手（今選んだ）
      match.setIsActive(true);
      matchMapper.insertMatch(match);

      // 【仕様D】待機レコードを終了状態（FALSE）にする
      matchInfoMapper.updateInactive(waiting.getId());
    }

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
