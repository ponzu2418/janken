package oit.is.z3577.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchInfoMapper {

  // 手を選んだときに試合中の情報を登録する
  @Insert("INSERT INTO matchinfo (user1, user2, user1Hand, isActive) VALUES (#{user1}, #{user2}, #{user1Hand}, #{isActive})")
  void insertMatchInfo(MatchInfo matchInfo);

  // 試合中（isActive が TRUE）のものだけ取得する
  @Select("SELECT * FROM matchinfo WHERE isActive = TRUE")
  ArrayList<MatchInfo> selectActiveMatchInfo();

    // 相手(user1)が自分(user2)を待っている試合中のレコードを探す
  @Select("SELECT * FROM matchinfo WHERE isActive = TRUE AND user1 = #{user1} AND user2 = #{user2}")
  MatchInfo selectActiveByUsers(@Param("user1") int user1, @Param("user2") int user2);

    // 試合が成立したので、待機レコードを終了状態にする
  @Update("UPDATE matchinfo SET isActive = FALSE WHERE id = #{id}")
  void updateInactive(int id);
}
