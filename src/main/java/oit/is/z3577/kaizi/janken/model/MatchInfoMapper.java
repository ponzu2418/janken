package oit.is.z3577.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchInfoMapper {

  // 手を選んだときに試合中の情報を登録する
  @Insert("INSERT INTO matchinfo (user1, user2, user1Hand, isActive) VALUES (#{user1}, #{user2}, #{user1Hand}, #{isActive})")
  void insertMatchInfo(MatchInfo matchInfo);

  // 試合中（isActive が TRUE）のものだけ取得する
  @Select("SELECT * FROM matchinfo WHERE isActive = TRUE")
  ArrayList<MatchInfo> selectActiveMatchInfo();
}
