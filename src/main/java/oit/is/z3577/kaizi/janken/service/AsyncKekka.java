package oit.is.z3577.kaizi.janken.service;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.z3577.kaizi.janken.model.Match;
import oit.is.z3577.kaizi.janken.model.MatchMapper;

@Service
public class AsyncKekka {
  // DBに新しい試合結果が入ったかどうかの旗
  boolean dbUpdated = false;

  private final Logger logger = LoggerFactory.getLogger(AsyncKekka.class);

  @Autowired
  MatchMapper matchMapper;

  /**
   * 試合成立時に Controller から呼ばれる．
   * matches に結果を保存し，「DBが更新された」旗を立てる
   * （サンプルの syncBuyFruits に当たる）
   */
  public void syncInsertMatch(Match match) {
    matchMapper.insertMatch(match);
    this.dbUpdated = true;
  }

  /**
   * wait.html から SSE で接続されたときに裏で動き続ける監視ループ
   * （サンプルの asyncShowFruitsList に当たる）
   */
  @Async
  public void asyncShowResult(SseEmitter emitter) {
    try {
      while (true) { // 無限ループで見張り続ける
        // DBが更新されていなければ0.5秒休んで、もう一度確認
        if (false == dbUpdated) {
          TimeUnit.MILLISECONDS.sleep(500);
          continue;
        }
        // isActive が TRUE の試合結果を取得
        ArrayList<Match> activeMatches = matchMapper.selectActiveMatches();
        if (activeMatches.isEmpty()) {
          // 他の接続ですでに処理済みなら旗を下ろして見張りに戻る
          dbUpdated = false;
          continue;
        }
        // ブラウザに試合結果を送る
        emitter.send(activeMatches);
        TimeUnit.MILLISECONDS.sleep(1000);
        dbUpdated = false;

        // dbUpdated を false にした後で，表示した試合を FALSE に更新
        for (Match match : activeMatches) {
          matchMapper.updateMatchInactive(match.getId());
        }
      }
    } catch (Exception e) {
      // ブラウザ側が画面を閉じたり移動したりすると，送信できず例外になる
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
    } finally {
      emitter.complete();
    }
    System.out.println("asyncShowResult complete");
  }
}
