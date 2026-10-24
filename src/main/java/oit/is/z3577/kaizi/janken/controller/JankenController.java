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

@Controller
public class JankenController {
  @Autowired
  private Entry entry;
  @Autowired
  UserMapper userMapper;
  @Autowired
  MatchMapper matchMapper;

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
    return "janken.html";
  }

  @GetMapping("/jankengame")
  public String jankengame(@RequestParam String hand, ModelMap model) {
    Janken janken = new Janken(hand);
    model.addAttribute("janken", janken);
    return "janken.html";
  }

}
