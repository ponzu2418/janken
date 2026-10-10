package oit.is.z3577.kaizi.janken.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.z3577.kaizi.janken.model.Janken;

@Controller
public class JankenController {

  /**
   * /janken に直接アクセスしたとき．ユーザ名は表示しない．
   */
  @GetMapping("/janken")
  public String janken() {
    return "janken.html";
  }

  @GetMapping("/jankengame")
    public String jankengame(@RequestParam String hand, ModelMap model){
    Janken janken = new Janken(hand);
    model.addAttribute("janken", janken);
    return "janken.html";
  }

  /**
   * index.html のフォームから参加したとき．入力されたユーザ名を janken.html に渡す．
   */
  @PostMapping("/janken")

  public String janken(@RequestParam String username, ModelMap model) {
    model.addAttribute("username", username);
    return "janken.html";
  }

}
