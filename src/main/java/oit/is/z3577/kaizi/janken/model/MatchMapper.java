package oit.is.z3577.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchMapper {

  @Select("SELECT * FROM matches")
  ArrayList<Match> selectAllMatches();

  @Insert("INSERT INTO matches (user1, user2, user1Hand, user2Hand, isActive) VALUES (#{user1}, #{user2}, #{user1Hand}, #{user2Hand}, #{isActive})")
  void insertMatch(Match match);

  @Select("SELECT * FROM matches WHERE isActive = TRUE")
  ArrayList<Match> selectActiveMatches();

  @Update("UPDATE matches SET isActive = FALSE WHERE id = #{id}")
  void updateMatchInactive(int id);
}
